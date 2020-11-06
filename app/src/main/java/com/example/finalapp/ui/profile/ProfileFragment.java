package com.example.finalapp.ui.profile;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalapp.LoginActivity;
import com.example.finalapp.MainActivity;
import com.example.finalapp.R;
import com.example.finalapp.model.User;
import com.example.finalapp.ui.profile.history.OrderHistoryFragment;
import com.example.finalapp.ui.profile.history.PostHistoryFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private String[] title;
    private View view;
    private DocumentReference documentReference;
    private ImageView userImg;
    private TextView userName;
    private String email;
    public static final String TAG = "ERROR";

    private Uri userImgUri;
    private StorageTask uploadTask;
    private FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (getActivity() == null) {
            return view;
        }

        // history fragments setup
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.historyTab);
        title = view.getResources().getStringArray(R.array.history);
        ViewPagerStateAdapter viewPagerStateAdapter = new ViewPagerStateAdapter(getActivity());
        viewPager.setAdapter(viewPagerStateAdapter);
        viewPager.setOffscreenPageLimit(1);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(title[position]);
            }
        }).attach();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReference = db.collection("users")
                .document(firebaseUser.getUid());

        // user information
        getUserInfo();

        // edit user name
        final TextView edit = view.findViewById(R.id.profileEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editStr = edit.getText().toString();
                if (editStr.equals("Edit")) {
                    edit.setText("Save");
                    userName.setEnabled(true);
                    userName.setBackgroundResource(R.drawable.spinner_border);
                } else {
                    documentReference.update("username", userName.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    edit.setText("Edit");
                                    userName.setEnabled(false);
                                    userName.setBackgroundResource(0);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating", e);
                                }
                            });
                }
            }
        });


        // user image change
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), ProfileFragment.this);
            }
        });


        // logout
        TextView logout;
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });


        return view;
    }


    private void uploadImage() {
        if (userImgUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("users");
            final StorageReference fileReference = storageReference.child(firebaseUser.getUid());

            uploadTask = fileReference.putFile(userImgUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        final String myUrl = downloadUri.toString();
                        documentReference.update("imageurl", myUrl)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Glide.with(view).load(myUrl).into(userImg);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Fail to update", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void getUserInfo() {
        userImg = view.findViewById(R.id.profileUserImg);
        userName = view.findViewById(R.id.profileUser);
        final TextView rate = view.findViewById(R.id.profileRate);

        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        Glide.with(view).load(user.getImageurl()).into(userImg);
                        userName.setText(user.getUsername());
                        rate.setText(Double.toString(user.getRate()));
                        email = user.getEmail();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            userImgUri = result.getUri();
            uploadImage();

        } else {
            Toast.makeText(getContext(), "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    // viewPager2 adapter
    public class ViewPagerStateAdapter extends FragmentStateAdapter {

        Fragment[] fragments;

        public ViewPagerStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            String[] title = getResources().getStringArray(R.array.history);
            fragments = new Fragment[title.length];
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (fragments[position] == null) {
                PostHistoryFragment postHistoryFragment = new PostHistoryFragment();
                OrderHistoryFragment orderHistoryFragment = new OrderHistoryFragment();
                fragments[0] = postHistoryFragment;
                fragments[1] = orderHistoryFragment;
            }
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }

}
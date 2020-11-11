package com.example.finalapp.post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.MainActivity;
import com.example.finalapp.R;
import com.example.finalapp.detail.PhotoDownloadAdapter;
import com.example.finalapp.model.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private Spinner spinnerType, spinnerCategory;
    private PhotoAdapter photoAdapter;
    private List<Uri> list;
    private List<String> downloadList;
    private FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private int counter;
    private Context mContext;

    private EditText title, amount, price, detail;

    private String strType, strCategory, strTitle, strAmount, strPrice, strDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mContext = this;

        spinnerType = addItemOnSpinner(R.id.SpinnerType, R.array.type);
        spinnerCategory = addItemOnSpinner(R.id.SpinnerCategory, R.array.category);

        title = findViewById(R.id.edit_title);
        amount = findViewById(R.id.edit_amount);
        price = findViewById(R.id.edit_price);
        detail = findViewById(R.id.edit_detail);
        TextView post = findViewById(R.id.post);
        ImageView postClose = findViewById(R.id.postClose);

        list = new ArrayList<>();
        downloadList = new ArrayList<>();

        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("posts");

        Button upload = findViewById(R.id.upload);
        photoAdapter = new PhotoAdapter(this);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(PostActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PostActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

//        photoAdapter.setRemoveListener(new PhotoAdapter.RemoveImgListener() {
//            @Override
//            public void onClick(Uri uri) {
//
//            }
//        });


        // close the post
        postClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // post into firebase
                spinnerType = findViewById(R.id.SpinnerType);
                strType = spinnerType.getSelectedItem().toString();
                spinnerCategory = findViewById(R.id.SpinnerCategory);
                strCategory = spinnerCategory.getSelectedItem().toString();

                strTitle = title.getText().toString();
                strAmount = amount.getText().toString();
                strPrice = price.getText().toString();
                strDetail = detail.getText().toString().replaceAll("\\n", "<br />");

                if (TextUtils.isEmpty(strTitle)) {
                    title.setError("Please enter title.");
                    return;
                }
                if (TextUtils.isEmpty(strAmount)) {
                    amount.setError("Please enter an number.");
                    return;
                }
                if (TextUtils.isEmpty(strPrice)) {
                    price.setError("Please enter an number.");
                    return;
                }
                if (TextUtils.isEmpty(strDetail)) {
                    amount.setError("Please enter details.");
                    return;
                }


                if (list.size() > 0){
                    for (Uri uri : list){
                        final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+ getFileExtension(uri));
                        uploadTask = fileReference.putFile(uri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isComplete()){
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return fileReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                counter++;
                                if (task.isSuccessful()){
                                    Uri downloadUri = task.getResult();
                                    downloadList.add(downloadUri.toString());
                                } else {
                                    Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                                if (counter == list.size()){
                                    uploadImage();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                counter++;
                                Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    uploadImage();
                }


            }
        });


        if (getIntent().hasExtra("postid")){
            String postid = getIntent().getStringExtra("postid");
            setDetails(postid);
        }

    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        String postId;
        DocumentReference dbReference;
        if (getIntent().hasExtra("postid")){
            postId = getIntent().getStringExtra("postid");
            dbReference = db.collection("posts").document(postId);
        } else {
            dbReference = db.collection("posts").document();
            postId = dbReference.getId();
        }

        Map<String, Object> post = new HashMap<>();
        post.put("poster", userId);
        post.put("postid", postId);
        post.put("type", strType);
        post.put("title",strTitle);
        post.put("category", strCategory);
        post.put("amount", Integer.parseInt(strAmount));
        post.put("price", Double.parseDouble(strPrice));
        post.put("detail", strDetail);
        post.put("date", new Date());
        post.put("images", downloadList);
        post.put("status", getResources().getStringArray(R.array.status)[0]);

        dbReference.set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, "Failed, please retry", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            RecyclerView revPhoto = findViewById(R.id.rev_photo);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            revPhoto.setLayoutManager(gridLayoutManager);
            revPhoto.setAdapter(photoAdapter);

            assert data != null;
            ClipData clipData = data.getClipData();
            if (clipData != null){
                // multiple image
                for (int i = 0; i < clipData.getItemCount(); i++){
                    list.add(clipData.getItemAt(i).getUri());
                }
            } else {
                list.add(data.getData());
            }

            photoAdapter.setData(list);

        } else {
            Toast.makeText(this, "Something is wrong !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }

    private Spinner addItemOnSpinner(int id, int arrayId){
        Spinner spinner = findViewById(id);
        String[] value = getResources().getStringArray(arrayId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, value);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }


    private void setDetails(String postid) {
        PhotoDownloadAdapter photoDownloadAdapter = new PhotoDownloadAdapter(mContext);
        RecyclerView revPhoto = findViewById(R.id.rev_photo);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        revPhoto.setLayoutManager(gridLayoutManager);
        revPhoto.setAdapter(photoDownloadAdapter);

        readPost(postid, photoDownloadAdapter);
    }


    private void readPost(String postid, final PhotoDownloadAdapter photoDownloadAdapter) {
        final List<Uri> imageUri = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // images
                        Post post = documentSnapshot.toObject(Post.class);
                        assert post != null;

                        for (String str : post.getImages()){
                            imageUri.add(Uri.parse(str));
                        }
                        photoDownloadAdapter.setData(imageUri);


                        String userid = post.getPoster();

                        title.setText(post.getTitle());
                        amount.setText(Long.toString(post.getAmount()));
                        price.setText("$ "+ Double.toString(post.getPrice()));
                        detail.setText(post.getDetail().replaceAll("<br />", "\\n"));
                        selectSpinnerValue(spinnerType, post.getType());
                        selectSpinnerValue(spinnerCategory, post.getCategory());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void selectSpinnerValue(Spinner spinner, String value){
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equals(value)){
                spinner.setSelection(i);
                break;
            }
        }
    }





}
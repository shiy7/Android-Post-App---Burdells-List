package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private Spinner spinnerType, spinnerCategory;
    private Button upload;
    private RecyclerView revPhoto;
    private PhotoAdapter photoAdapter;
    private List<Uri> list = new ArrayList<>();
    private List<String> downloadList = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth mFirebaseAuth;
    StorageTask uploadTask;
    StorageReference storageReference;

    EditText title, amount, price, detail;
    TextView post;
    ImageView postClose;

    String strType, strCategory, strTitle, strAmount, strPrice, strDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        spinnerType = addItemOnSpinner(R.id.SpinnerType, R.array.type);
        spinnerCategory = addItemOnSpinner(R.id.SpinnerCategory, R.array.category);

        title = findViewById(R.id.edit_title);
        amount = findViewById(R.id.edit_amount);
        price = findViewById(R.id.edit_price);
        detail = findViewById(R.id.edit_detail);
        post = findViewById(R.id.post);
        postClose = findViewById(R.id.postClose);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("posts");

        upload = findViewById(R.id.upload);
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
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        // post into firebase
        spinnerType = findViewById(R.id.SpinnerType);
        strType = spinnerType.getSelectedItem().toString();
        spinnerCategory = findViewById(R.id.SpinnerCategory);
        strCategory = spinnerCategory.getSelectedItem().toString();

        strTitle = title.getText().toString();
        strAmount = amount.getText().toString();
        strPrice = price.getText().toString();
        strDetail = detail.getText().toString();

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
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+'.'+getFileExtension(uri));
                uploadTask = fileReference.putFile(uri);
                uploadTask.continueWith(new Continuation() {
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
                        if (task.isSuccessful()){
                            Uri downloadUri = task.getResult();
                            downloadList.add(downloadUri.toString());
                        } else {
                            Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        DocumentReference dbReference = db.collection("posts").document();
        String postId = dbReference.getId();

        Map<String, Object> post = new HashMap<>();
        post.put("poster", userId);
        post.put("postid", postId);
        post.put("type", strType);
        post.put("category", strCategory);
        post.put("amount", Integer.parseInt(strAmount));
        post.put("price", Double.parseDouble(strPrice));
        post.put("detail", strDetail);
        post.put("images", downloadList);

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
            revPhoto = findViewById(R.id.rev_photo);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            revPhoto.setLayoutManager(gridLayoutManager);
            revPhoto.setAdapter(photoAdapter);

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





}
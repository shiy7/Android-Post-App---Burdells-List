package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.finalapp.model.Post;
import com.example.finalapp.ui.home.PostAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private String postid;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    private TextView poster, title, amount, price, detail, date, type, category;
    private ImageView backHome;
    private List<String> images;

    public Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        SharedPreferences preferences = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postid = preferences.getString("postid", "none");
        recyclerView = findViewById(R.id.detail_rev_photo);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(photoAdapter);

        readPost();

//        Glide.with(mContext).load(post.getImages().get(0)).into(holder.postImage);
    }

    private void readPost() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        images.clear();
                        Post post = documentSnapshot.toObject(Post.class);
                        assert post != null;
                        images = post.getImages();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
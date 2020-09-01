package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomePage extends AppCompatActivity {
    Button btnLogOut;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ImageView imageView = findViewById(R.id.imageview1);
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        btnLogOut = findViewById(R.id.button2);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent inToSignUp = new Intent(HomePage.this, MainActivity.class);
                startActivity(inToSignUp);
            }
        });
    }
    private void loadImageByInternetURL() {

        Glide
                .with(this)
                .asGif()
                .into(imageView);
    }
}
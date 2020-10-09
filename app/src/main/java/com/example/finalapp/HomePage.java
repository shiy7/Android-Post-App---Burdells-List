package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {
    Button btnLogOut;
    Button btnTextBookBuying;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ImageView imageView;
    String url = "https://media.giphy.com/media/3xz2BCohVTd7h2Kvfi/giphy.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        // Buy Textbooks Category
        btnTextBookBuying = findViewById(R.id.Textbooks);
        btnTextBookBuying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buyTextbook = new Intent(HomePage.this, BuyTextBooks.class);
                startActivity(buyTextbook);
            }
        });

        // Log Out of the application
        btnLogOut = findViewById(R.id.LogoutButton);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent inToSignUp = new Intent(HomePage.this, RegisterActivity.class);
                startActivity(inToSignUp);
            }
        });
    }

}
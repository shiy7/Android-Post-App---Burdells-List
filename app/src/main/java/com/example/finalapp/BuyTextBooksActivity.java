package com.example.finalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class BuyTextBooksActivity extends AppCompatActivity {
    Button returnToHomePage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_text_books);

        returnToHomePage = findViewById(R.id.LogoutButton);
        returnToHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent retToHomePage = new Intent(BuyTextBooksActivity.this, HomePage.class);
                startActivity(retToHomePage);
            }
        });

    }

}
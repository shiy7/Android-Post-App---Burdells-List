package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText emailID, password;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailID = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.etPassword);
        tvSignIn = findViewById(R.id.textView3);
        btnSignUp = findViewById(R.id.button);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailID.getText().toString();
                String passwordEntry = password.getText().toString();
                if (email.isEmpty() && passwordEntry.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    emailID.setError("Please enter an GT email");
                    emailID.requestFocus();
                } else if (passwordEntry.isEmpty()) {
                    password.setError("Password enter a password");
                    password.requestFocus();
                } else if (email.split("@")[1].equals("gatech.edu")){
                    Toast.makeText(RegisterActivity.this, "Please enter an GT email, not other emails", Toast.LENGTH_SHORT).show();
                } else {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, passwordEntry).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Sign up unsuccessful. Try again!", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(RegisterActivity.this, HomePage.class));
                            }
                        }
                    });
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
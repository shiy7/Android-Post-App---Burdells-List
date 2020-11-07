package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText emailID, password, userName, confirm;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailID = findViewById(R.id.editTextTextEmailAddress);
        userName = findViewById(R.id.editTextUserName);
        password = findViewById(R.id.etPassword);
        confirm = findViewById(R.id.cfPassword);
        tvSignIn = findViewById(R.id.textView3);
        btnSignUp = findViewById(R.id.button);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailID.getText().toString();
                final String username = userName.getText().toString();
                final String passwordEntry = password.getText().toString();
                String cfPassword = confirm.getText().toString();
                if (email.isEmpty() && passwordEntry.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    emailID.setError("Please enter an GT email");
                    emailID.requestFocus();
                } else if (passwordEntry.isEmpty()) {
                    password.setError("Password enter a password");
                    password.requestFocus();
                } else if (!(email.split("@")[1].equals("gatech.edu"))) {
                    Toast.makeText(RegisterActivity.this, "Please enter an GT email, not other emails", Toast.LENGTH_SHORT).show();
                } else if (passwordEntry.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                } else if (!cfPassword.equals(passwordEntry)){
                    Toast.makeText(RegisterActivity.this, "Password not match, please try again", Toast.LENGTH_SHORT).show();
                } else {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, passwordEntry).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Sign up unsuccessful. Try again!", Toast.LENGTH_SHORT).show();
                            } else {
                                Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                                    String userId = firebaseUser.getUid();
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("id", userId);
                                                    user.put("username", username);
                                                    user.put("email", email);
                                                    user.put("address", "");
                                                    user.put("country", "");
                                                    user.put("state", "");
                                                    user.put("city", "");
                                                    user.put("phone","");
                                                    user.put("rate", 0.0);
                                                    user.put("reviewCount", 0);
                                                    user.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/burdells.appspot.com/o/placeholder.jpg?alt=media&token=55243f20-f030-4932-93e5-3320841d8f3a");
                                                    db.collection("users").document(userId)
                                                            .set(user)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(RegisterActivity.this,
                                                                            "Registered successfully. Please check your email for verification",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d("Error", e.getMessage());
                                                                }
                                                            });

                                                } else {
                                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

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
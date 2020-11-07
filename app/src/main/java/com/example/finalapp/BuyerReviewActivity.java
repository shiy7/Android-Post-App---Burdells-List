package com.example.finalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.finalapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.text.DecimalFormat;
import java.util.HashMap;

public class BuyerReviewActivity extends AppCompatActivity {

    private ImageView back;
    private AppCompatRatingBar quality, communication;
    private EditText comment;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_review);

        back = findViewById(R.id.buyerBack);
        quality = findViewById(R.id.buyerQuality);
        communication = findViewById(R.id.buyerCom);
        comment = findViewById(R.id.buyerComment);
        submit = findViewById(R.id.buyerSubmit);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (getIntent().hasExtra("orderid") && getIntent().hasExtra("seller")
                && getIntent().hasExtra("position")){
                    HashMap<String, Object> review = new HashMap<>();
                    final float qualiteRate = quality.getRating();
                    final float comRate = communication.getRating();
                    review.put("quality", qualiteRate);
                    review.put("communication", comRate);
                    if (comment.getText() != null){
                        review.put("comment", comment.getText().toString());
                    }
                    final String reviewee = getIntent().getStringExtra("seller");
                    final String orderId = getIntent().getStringExtra("orderid");
                    final int position = getIntent().getIntExtra("position", 0);
                    assert reviewee != null;
                    assert orderId != null;
                    db.collection("reviews").document(reviewee)
                            .collection("orders").document(orderId)
                            .set(review)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        final float avg = (qualiteRate + comRate) / 2;
                                        final DocumentReference document = db.collection("users").document(reviewee);
                                        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                User user = documentSnapshot.toObject(User.class);
                                                assert user != null;
                                                float rate = user.getRate();
                                                int count = user.getReviewCount() + 1;
                                                Float finalRate;
                                                if (count > 1) {
                                                    finalRate = (rate + avg) / count;
                                                } else {
                                                    finalRate = avg;
                                                }

                                                document.update(
                                                        "rate", finalRate,
                                                        "reviewCount", count
                                                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent intent = new Intent();
                                                        intent.putExtra("orderId", orderId);
                                                        intent.putExtra("position", position);
                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(BuyerReviewActivity.this,
                                                                "Fail to update. Please retry !",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(BuyerReviewActivity.this,
                                                        "Fail to update. Please retry !",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(BuyerReviewActivity.this,
                                                "Fail to update. Please retry !",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}
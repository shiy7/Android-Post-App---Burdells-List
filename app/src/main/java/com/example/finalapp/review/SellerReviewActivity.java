package com.example.finalapp.review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finalapp.R;
import com.example.finalapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SellerReviewActivity extends AppCompatActivity {

    private ImageView back;
    private AppCompatRatingBar payRate, communication;
    private EditText comment;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_review);

        back = findViewById(R.id.sellerBack);
        payRate = findViewById(R.id.sellerPay);
        communication = findViewById(R.id.sellerCom);
        comment = findViewById(R.id.sellerComment);
        submit = findViewById(R.id.sellerSubmit);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // submit infor to reviews database
        //        update review rate in reviewee side
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                Intent intent = getIntent();
                if (intent.hasExtra("orderid") && intent.hasExtra("buyer")
                        && intent.hasExtra("position" ) && intent.hasExtra("postid")
                        && intent.hasExtra("reviewer")){
                    HashMap<String, Object> review = new HashMap<>();
                    final float paymentRate = payRate.getRating();
                    final float comRate = communication.getRating();
                    String reviewer = intent.getStringExtra("reviewer");
                    review.put("payTime", paymentRate);
                    review.put("communication", comRate);
                    review.put("reviewer", reviewer);
                    if (comment.getText() != null){
                        review.put("comment", comment.getText().toString());
                    }
                    final String reviewee = intent.getStringExtra("buyer");
                    final String orderId = intent.getStringExtra("orderid");
                    final int position = intent.getIntExtra("position", 0);
                    String postid = intent.getStringExtra("postid");
                    assert reviewee != null;
                    assert orderId != null;
                    db.collection("reviews").document(reviewee)
                            .collection("postid").document(postid)
                            .collection("orderid").document(orderId)
                            .set(review)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        final float avg = (paymentRate + comRate) / 2;
                                        final DocumentReference document = db.collection("users").document(reviewee);
                                        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                User user = documentSnapshot.toObject(User.class);
                                                assert user != null;
                                                float rate = user.getRate();
                                                long count = user.getReviewCount() + 1;
                                                float finalRate;
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
                                                        Toast.makeText(SellerReviewActivity.this,
                                                                "Fail to update. Please retry !",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SellerReviewActivity.this,
                                                        "Fail to update. Please retry !",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SellerReviewActivity.this,
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
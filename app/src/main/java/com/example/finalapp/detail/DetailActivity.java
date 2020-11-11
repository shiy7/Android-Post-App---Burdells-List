package com.example.finalapp.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalapp.R;
import com.example.finalapp.message.MessageActivity;
import com.example.finalapp.model.Post;
import com.example.finalapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private String postid;
    private RecyclerView recyclerView;
    private PhotoDownloadAdapter photoDownloadAdapter;

    private TextView poster, title, price, detail, date, type, category, posterRate;
    private ImageView backHome, postImg, addShop;
    private List<String> images;
    private Spinner amount;
    private List<String> value;
    private List<Uri> imageUri;
    private Context mContext;
    private ArrayAdapter<String> adapter;
    private Button chat;
    private String posterId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = this;

        chat = findViewById(R.id.chatPoster);

        // get post id
        if (getIntent().hasExtra("postid")) {
            postid = getIntent().getStringExtra("postid");
            setDetails();

            // if poster check own post
            // not need amount, chat, add, poster information
            if (getIntent().hasExtra("poster")) {
                if (getIntent().getBooleanExtra("poster", false)) {
                    amount.setVisibility(View.GONE);
                    chat.setVisibility(View.GONE);
                    addShop.setVisibility(View.GONE);
                    poster.setVisibility(View.GONE);
                    postImg.setVisibility(View.GONE);
                    posterRate.setVisibility(View.GONE);
                }
            }

            // if check the order
            // not need amount, add
            // show orderAmount
            if (getIntent().hasExtra("orderAmount")) {
                String str = getIntent().getStringExtra("orderAmount");
                amount.setVisibility(View.GONE);
                addShop.setVisibility(View.GONE);
                TextView txtAmount, amount;
                txtAmount = findViewById(R.id.txt_orderAmount);
                amount = findViewById(R.id.orderAmount);
                txtAmount.setVisibility(View.VISIBLE);
                amount.setVisibility(View.VISIBLE);
                amount.setText(str);
            }

        }


//         close the detail page
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // add into shop list
        addShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAdd = amount.getSelectedItem().toString();
                if (!strAdd.equals("Select Amount")) {
                    int addAmount = Integer.parseInt(amount.getSelectedItem().toString());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();
                    db.collection("shop").document(userId)
                            .update(postid, FieldValue.increment(addAmount))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    amount.setSelection(0);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DetailActivity.this, "Fail to add", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

        // contact
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("messageTo",posterId);
                mContext.startActivity(intent);
            }
        });
    }


    private void setDetails() {
        photoDownloadAdapter = new PhotoDownloadAdapter(mContext);

        recyclerView = findViewById(R.id.detail_rev_photo);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(photoDownloadAdapter);
        imageUri = new ArrayList<>();


        poster = findViewById(R.id.detailPoster);
        title = findViewById(R.id.detailTitle);
        amount = findViewById(R.id.detailAmount);
        price = findViewById(R.id.detailPrice);
        detail = findViewById(R.id.detailDescription);
        date = findViewById(R.id.detailDate);
        type = findViewById(R.id.detailType);
        category = findViewById(R.id.detailCategory);
        backHome = findViewById(R.id.backHome);
        posterRate = findViewById(R.id.detailPosterRate);
        postImg = findViewById(R.id.detailPosterImg);
        addShop = findViewById(R.id.detailAdd);


        value = new ArrayList<>();
        value.add("Select Amount");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, value);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amount.setAdapter(adapter);

        readPost();


    }

    // read information from posts
    private void readPost() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // images
                        Post post = documentSnapshot.toObject(Post.class);
                        assert post != null;

                        for (String str : post.getImages()) {
                            imageUri.add(Uri.parse(str));
                        }
                        photoDownloadAdapter.setData(imageUri);


                        String userid = post.getPoster();

                        title.setText(post.getTitle());
                        price.setText("$ " + Double.toString(post.getPrice()));
                        detail.setText(post.getDetail().replaceAll("<br />", "\\n"));
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                        date.setText(dateFormat.format(post.getDate()));
                        type.setText(post.getType());
                        category.setText(post.getCategory());

                        //amount spinner
                        long total = post.getAmount();
                        for (int i = 1; i <= total; i++) {
                            value.add(Long.toString(i));
                        }
                        adapter.notifyDataSetChanged();

                        if (getIntent().hasExtra("contactId")){
                            userid = getIntent().getStringExtra("contactId");
                            TextView contact = findViewById(R.id.detailContact);
                            contact.setVisibility(View.VISIBLE);
                        }

                        posterInfo(postImg, poster, posterRate, userid);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    // get poster information
    private void posterInfo(final ImageView posterImg, final TextView poster, final TextView posterRate, String userid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        poster.setText(user.getUsername());
                        DecimalFormat df = new DecimalFormat("#.#");
                        posterRate.setText("(Rate: " + df.format(user.getRate()) + ")");
                        Glide.with(mContext).load(user.getImageurl()).into(posterImg);
                        posterId = user.getId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
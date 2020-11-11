package com.example.finalapp.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalapp.R;
import com.example.finalapp.model.Chat;
import com.example.finalapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    ImageView back;
    CircleImageView profileImage;
    TextView receiverName;
    FirebaseUser fuser;

    FirebaseFirestore db;

    ImageButton send;
    EditText editMessage;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        back = findViewById(R.id.messageBack);
        profileImage = findViewById(R.id.messageImg);
        receiverName = findViewById(R.id.messageName);
        send = findViewById(R.id.messageSend);
        editMessage = findViewById(R.id.messageEdit);

        intent = getIntent();
        final String receiverId = getIntent().getStringExtra("messageTo");

        db = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.messageRecView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        // get receiver information
        receiverInfo(receiverId);




        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editMessage.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(), receiverId, msg );
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

                editMessage.setText("");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    // get receiver infor
    private void receiverInfo(final String userid) {
        db.collection("users").document(userid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        receiverName.setText(user.getUsername());
                        Glide.with(MessageActivity.this).load(user.getImageurl()).into(profileImage);

                        readMessage(fuser.getUid(), userid, user.getImageurl());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void sendMessage(String sender, String receiver, String message){
        HashMap<String, Object> infor = new HashMap<>();
        infor.put("sender", sender);
        infor.put("receiver", receiver);
        infor.put("message", message);
        infor.put("date", new Date());

        db.collection("chats").document()
                .set(infor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(MessageActivity.this, "Fail to send message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void readMessage(final String userId, final String partnerId, final String imageurl){
        mChat = new ArrayList<>();


        db.collection("chats")
                .orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        mChat.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            Chat chat = snapshot.toObject(Chat.class);
                            if (chat.getSender().equals(userId) && chat.getReceiver().equals(partnerId)
                                    || chat.getSender().equals(partnerId) && chat.getReceiver().equals(userId)) {
                                mChat.add(chat);
                            }
                            messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                            recyclerView.setAdapter(messageAdapter);
                        }

                    }
                });
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            mChat.clear();
//                            QuerySnapshot documentSnapshot = task.getResult();
//                            assert documentSnapshot != null;
//                            for (QueryDocumentSnapshot snapshot : documentSnapshot) {
//                                Chat chat = snapshot.toObject(Chat.class);
//                                if (chat.getSender().equals(userId) && chat.getReceiver().equals(partnerId)
//                                        || chat.getSender().equals(partnerId) && chat.getReceiver().equals(userId)) {
//                                    mChat.add(chat);
//                                }
//
//                                messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
//                                recyclerView.setAdapter(messageAdapter);
//                            }
//                        } else {
//                            Toast.makeText(MessageActivity.this, "Error to get message", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

    }
}
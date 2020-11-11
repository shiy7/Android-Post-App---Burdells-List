package com.example.finalapp.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalapp.message.MessageActivity;
import com.example.finalapp.R;
import com.example.finalapp.model.Chat;
import com.example.finalapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    String lastmsg;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.userName.setText(user.getUsername());
        Glide.with(mContext).load(user.getImageurl()).into(holder.profileImage);

        lastMessage(user.getId(), holder.lastMsg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("messageTo", user.getId());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userName;
        public ImageView profileImage;
        private TextView lastMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            profileImage = itemView.findViewById(R.id.profileImage);
            lastMsg = itemView.findViewById(R.id.lastMsg);
        }
    }

    // check last msg
    private void lastMessage(final String userid, final TextView lastMsg){
        lastmsg = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chats")
                .orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot snapshot : value){
                            Chat chat = snapshot.toObject(Chat.class);
                            if (chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userid)
                                    || chat.getSender().equals(userid) && chat.getReceiver().equals(firebaseUser.getUid())) {
                                lastmsg = chat.getMessage();
                            }
                        }
                        if ("default".equals(lastmsg)) {
                            lastMsg.setText("No Message");
                        } else {
                            lastMsg.setText(lastmsg);
                        }
                        lastmsg = "default";
                    }
                });
    }
}

package com.example.finalapp.ui.chat;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        lastMessage(user.getId(), holder.lastMsg, holder.lastTime, holder.lastseen);

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
        public ImageView profileImage, lastseen;
        private TextView lastMsg, lastTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            profileImage = itemView.findViewById(R.id.profileImage);
            lastMsg = itemView.findViewById(R.id.lastMsg);
            lastTime = itemView.findViewById(R.id.lastMsgTime);
            lastseen = itemView.findViewById(R.id.seeMessage);
        }
    }

    // check last msg
    private void lastMessage(final String userid, final TextView lastMsg, final TextView lastTime, final ImageView lastseen){
        lastmsg = "";
        final String lasttime = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chats")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot snapshot : value){
                            Chat chat = snapshot.toObject(Chat.class);
                            if (chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userid)
                                    || chat.getSender().equals(userid) && chat.getReceiver().equals(firebaseUser.getUid())) {
                                lastmsg = chat.getMessage();
                                setTime(chat.getDate().getTime(), new Date().getTime(), lastTime);
                                if (!chat.getSeen() && chat.getReceiver().equals(firebaseUser.getUid())){
                                    lastseen.setVisibility(View.VISIBLE);
                                }
                                break;
                            }
                        }
                        lastMsg.setText(lastmsg);
                    }
                });
    }

    private void setTime(long ts1, long ts2, TextView timeText) {


        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(ts1);
        cal2.setTimeInMillis(ts2);

        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

        boolean sameWeek = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.WEEK_OF_MONTH) == cal2.get(Calendar.WEEK_OF_MONTH);

        if (sameDay) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            timeText.setText(dateFormat.format(ts1));
        } else if (sameWeek) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("E hh:mm a");
            timeText.setText(dateFormat.format(ts1));
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm a");
            timeText.setText(dateFormat.format(ts1));
        }

    }
}

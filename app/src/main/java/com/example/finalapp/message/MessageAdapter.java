package com.example.finalapp.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalapp.R;
import com.example.finalapp.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_RECEIVER = 0;
    public static final int MSG_TYPE_SENDER = 1;

    private final Context mContext;
    private final List<Chat> mChat;
    private final String imageurl;

    FirebaseUser user;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_SENDER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_sender, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_receiver, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);

        holder.showMessage.setText(chat.getMessage());
//        holder.showTime.setText(chat.getDate());

        Glide.with(mContext).load(imageurl).into(holder.profileImage);

        setTime(chat.getDate().getTime(), new Date().getTime(), holder.showTime);

        long preTime = 0;
        if (position > 1) {
            Chat prev = mChat.get(position - 1);
            preTime = prev.getDate().getTime();
        }

        setTimeTextVisibility(chat.getDate().getTime(), preTime, holder.showTime);


    }

    private void setTime(long ts1, long ts2, TextView timeText) {


        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(ts1);
        cal2.setTimeInMillis(ts2);

        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

        boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

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
        } else if (sameMonth) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd hh:mm a");
            timeText.setText(dateFormat.format(ts1));
        } else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            timeText.setText(dateFormat.format(ts1));
        }

    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView showTime) {
        if (ts2 == 0) {
            showTime.setVisibility(View.VISIBLE);
        } else {
            // not show date for 5 minutes
            if (ts1 - ts2 <= 5*60*1000){
                showTime.setVisibility(View.GONE);
            } else {
                showTime.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView showMessage, showTime;
        public ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
            showTime = itemView.findViewById(R.id.showTime);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_SENDER;
        } else {
            return MSG_TYPE_RECEIVER;
        }
    }


}

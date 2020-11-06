package com.example.finalapp.ui.profile.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalapp.DetailActivity;
import com.example.finalapp.PostActivity;
import com.example.finalapp.R;
import com.example.finalapp.model.Post;
import com.example.finalapp.ui.home.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.List;

public class PostHistoryAdapter extends RecyclerView.Adapter<PostHistoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> postHis;

    public PostHistoryAdapter(Context mContext, List<Post> postHis) {
        this.mContext = mContext;
        this.postHis = postHis;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_history_item, parent, false);
        return new PostHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHistoryAdapter.ViewHolder holder, final int position) {
        final Post post = postHis.get(position);
        if (post.getImages() != null && post.getImages().size() != 0){
            Glide.with(mContext).load(post.getImages().get(0)).into(holder.postImage);
        }

        holder.title.setText(post.getTitle());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        holder.date.setText(dateFormat.format(post.getDate()));

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("poster", true);
                mContext.startActivity(intent);
            }
        });


        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder removeBox = new AlertDialog.Builder(mContext);
                removeBox.setMessage("Are you sure to close your post ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("posts").document(post.getPostid())
                                        .update("status", "close")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()){
                                                    Toast.makeText(mContext,"Fail to close the post !", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    postHis.remove(position);
                                                    notifyItemRemoved(position);
                                                    Toast.makeText(mContext,"Post is closed !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", null);
                AlertDialog alert = removeBox.create();
                alert.show();
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostActivity.class);
                intent.putExtra("postid", post.getPostid());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (postHis != null){
            return postHis.size();
        }
       return 0;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView postImage;
        private final ImageView edit;
        private final ImageView remove;
        private final TextView title;
        private final TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postHisImg);
            title = itemView.findViewById(R.id.postHisTitle);
            date = itemView.findViewById(R.id.postHisDate);
            edit = itemView.findViewById(R.id.postHisEdit);
            remove = itemView.findViewById(R.id.postHisRemove);
        }
    }


}

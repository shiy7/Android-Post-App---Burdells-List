package com.example.finalapp.post;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapp.R;

import java.io.IOException;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{

//    interface RemoveImgListener {
//        void onClick(Uri uri);
//    }

    private Context mContext;
    private List<Uri> mListPhoto;

//    private RemoveImgListener removeImgListener;

    public PhotoAdapter(Context mContext) {
        this.mContext = mContext;
    }


    public void setData(List<Uri> list){
        this.mListPhoto = list;
        notifyDataSetChanged();
    }

//    public void setRemoveListener(RemoveImgListener removeImgListener){
//        this.removeImgListener = removeImgListener;
//    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,parent, false);
        return new PhotoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, final int position) {
        Uri uri = mListPhoto.get(position);
        if (uri == null){
            return;
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            if (bitmap != null){
                holder.imgPhoto.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.remove.setVisibility(View.VISIBLE);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListPhoto.remove(position);
                notifyItemRemoved(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mListPhoto != null){
            return mListPhoto.size();
        }
        return 0;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPhoto, remove;

        public PhotoViewHolder(@NonNull View itemView){
            super(itemView);

            imgPhoto = itemView.findViewById(R.id.ima_photo);
            remove = itemView.findViewById(R.id.img_remove);
        }
    }
}

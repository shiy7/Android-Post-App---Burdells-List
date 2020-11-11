package com.example.finalapp.detail;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalapp.R;

import java.util.List;

public class PhotoDownloadAdapter extends RecyclerView.Adapter<PhotoDownloadAdapter.PhotoViewHolder>{
    private Context mContext;
    private List<Uri> downloadUri;

    public PhotoDownloadAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Uri> list){
        this.downloadUri = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = downloadUri.get(position);
        if (uri == null){
            return;
        }

        Glide.with(mContext).load(uri).into(holder.imgPhoto);
    }

    @Override
    public int getItemCount() {
        if (downloadUri != null){
            return downloadUri.size();
        }
        return 0;
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPhoto;

        public PhotoViewHolder(@NonNull View itemView){
            super(itemView);

            imgPhoto = itemView.findViewById(R.id.ima_photo);
        }
    }
}

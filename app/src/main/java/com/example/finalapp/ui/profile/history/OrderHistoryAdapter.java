package com.example.finalapp.ui.profile.history;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapp.BuyerReviewActivity;
import com.example.finalapp.DetailActivity;
import com.example.finalapp.R;
import com.example.finalapp.SellerReviewActivity;
import com.example.finalapp.model.Order;
import com.example.finalapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>{

    private Context mContext;
    private List<Order> orderList;

    public OrderHistoryAdapter(Context mContext, List<Order> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.order_history_item, parent, false);
        return new OrderHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Order order = orderList.get(position);

        holder.orderId.setText(order.getId());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        holder.date.setText(dateFormat.format(order.getDate()));

        holder.orderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("postid", order.getPostid());
                intent.putExtra("orderAmount", Integer.toString(order.getAmount()));
                mContext.startActivity(intent);
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        if (userId.equals(order.getBuyer())){
            holder.status.setText(order.getBuyerStatus());
        } else {
            holder.status.setText(order.getSellerStatus());
        }

        holder.status.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                String str = holder.status.getText().toString();
                final String[] array = mContext.getResources().getStringArray(R.array.oderStatus);
                switch (str) {
                    case "Get Paid ?": {
                        AlertDialog.Builder pay = new AlertDialog.Builder(mContext);
                        pay.setMessage("Are you sure get the payment ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("orders").document(order.getId())
                                                .update("sellerStatus", array[2])
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(mContext, "Fail to update status !", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            holder.status.setText(array[2]);
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("Cancel", null);
                        AlertDialog alert = pay.create();
                        alert.show();

                        break;
                    }
                    case "Received ?": {
                        AlertDialog.Builder receive = new AlertDialog.Builder(mContext);
                        receive.setMessage("Are you sure receive the service ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("orders").document(order.getId())
                                                .update("buyerStatus", array[2])
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(mContext, "Fail to update status !", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            holder.status.setText(array[2]);
                                                        }
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("Cancel", null);
                        AlertDialog alert = receive.create();
                        alert.show();

                        break;
                    }
                    case "Review":
                        Intent intent;
                        if (userId.equals(order.getBuyer())){
                            intent = new Intent(mContext, BuyerReviewActivity.class);
                            intent.putExtra("orderid", order.getId());
                            intent.putExtra("seller", order.getSeller());
                            intent.putExtra("position", position);
                        } else {
                            intent = new Intent(mContext, SellerReviewActivity.class);
                            intent.putExtra("orderid", order.getId());
                            intent.putExtra("buyer", order.getBuyer());
                        }
                        ((Activity)mContext).startActivityForResult(intent, 100);
                        break;
                    case "Done":
                        holder.status.setTextColor(R.color.gray);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (orderList != null){
            return orderList.size();
        }
        return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView orderId, date, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.orderHisDate);
            orderId = itemView.findViewById(R.id.orderHisID);
            status = itemView.findViewById(R.id.orderHisStatus);
        }
    }
}

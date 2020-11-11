package com.example.finalapp.ui.shop;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.finalapp.detail.DetailActivity;
import com.example.finalapp.R;
import com.example.finalapp.model.Post;
import com.example.finalapp.model.Shop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


// Add items to shopping in the shopping fragment
// adapter: care about what you want to do in shopping_cart_items.xml
// shopFrgament - shopfragment.xml
// shop fragment add items to the order part of the database when hit submit
// adapter: plus or minus will connect with the shop database
// Once submit clear the clear the shopping database
//

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {
    private Context mContext;
    private List<Shop> mShop;

    private FirebaseUser firebaseUser;

    public ShopAdapter(Context mContext, List<Shop> mShop) {
        this.mContext = mContext;
        this.mShop = mShop;
    }

    public void setData(List<Shop> list) {
        this.mShop = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_cart_item, parent, false);
        return new ShopAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ShopAdapter.ViewHolder holder, final int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Shop shop = mShop.get(position);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final int[] maxAmount = new int[1];

        db.collection("posts").document(shop.getPostid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Post post = task.getResult().toObject(Post.class);
                            if (post.getImages() != null && post.getImages().size() != 0) {
                                Glide.with(mContext).load(post.getImages().get(0)).into(holder.postImageShopping);
                            }
                            holder.postTitleShopping.setText(post.getTitle());
                            holder.priceShopping.setText("$ " + Double.toString(post.getPrice()));
                            maxAmount[0] = post.getAmount();
                        }
                    }
                });

        // set the quantity of the textView
        holder.updateQuantityValue.setText(Integer.toString(shop.getQuantity()));

        // remove item from shopping cart when button removeFromShoppingList is clicked
        holder.removeFromShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("shop").document(firebaseUser.getUid())
                        .collection("posts").document(shop.getPostid())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mShop.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(mContext, "Item is successfully removed !", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Fail to remove", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        // display details of the post when they click the post title in shopping cart
        holder.postTitleShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("postid", shop.getPostid());
                mContext.startActivity(intent);
            }
        });

        //reduce the quantity of the item when you click reduce quantity button
        holder.reduceQuantityShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.updateQuantityValue.getText().toString());
                quantity--;
                if (quantity < 0) {
                    AlertDialog.Builder alterBox = new AlertDialog.Builder(mContext);
                    alterBox.setMessage("The quantity cannot be decreased more !!");
                    AlertDialog alert = alterBox.create();
                    alert.show();
                    holder.updateQuantityValue.setText("0");
                } else {
                    db.collection("shop").document(firebaseUser.getUid())
                            .collection("posts").document(shop.getPostid())
                            .update("quantity", FieldValue.increment(-1))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Display the quantity in the textView
                                    int quantity = Integer.parseInt(holder.updateQuantityValue.getText().toString());
                                    quantity--;
                                    holder.updateQuantityValue.setText(Integer.toString(quantity));

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, "Fail to add", Toast.LENGTH_SHORT).show();
                                }
                            });
                }


            }
        });

        // increase the quantity of the item you want
        holder.addToShopQuantityShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.updateQuantityValue.getText().toString());
                quantity++;
                if (quantity > maxAmount[0]){
                    AlertDialog.Builder alterBox = new AlertDialog.Builder(mContext);
                    alterBox.setMessage("The quantity cannot exceed the post max amount !!");
                    AlertDialog alert = alterBox.create();
                    alert.show();
                    holder.updateQuantityValue.setText(Integer.toString(maxAmount[0]));
                } else {
                    db.collection("shop").document(firebaseUser.getUid())
                            .collection("posts").document(shop.getPostid())
                            .update("quantity", FieldValue.increment(1))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Display the quantity in the textView
                                    int quantity = Integer.parseInt(holder.updateQuantityValue.getText().toString());
                                    quantity++;
                                    holder.updateQuantityValue.setText(Integer.toString(quantity));

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, "Fail to add", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mShop.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImageShopping, addToShopQuantityShopping, reduceQuantityShopping, removeFromShoppingList;
        public TextView postTitleShopping, priceShopping, updateQuantityValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            removeFromShoppingList = itemView.findViewById(R.id.remove_from_shopping_list);
            postImageShopping = itemView.findViewById(R.id.post_image_shopping_cart);
            addToShopQuantityShopping = itemView.findViewById(R.id.add_quantity);
            postTitleShopping = itemView.findViewById(R.id.postTitleShopping);
            reduceQuantityShopping = itemView.findViewById(R.id.reduce_quantity);
            priceShopping = itemView.findViewById(R.id.price_shopping_cart);
            updateQuantityValue = itemView.findViewById(R.id.quantity_update_view);
        }

    }

}

package com.example.finalapp.ui.shop;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalapp.R;
import com.example.finalapp.model.Post;
import com.example.finalapp.model.Shop;
import com.example.finalapp.ui.home.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ShopFragment extends Fragment {
    //Variables for shopping cart display
    private RecyclerView recyclerView;
    private ShopAdapter shopAdapter;
    private List<Shop> shopList;
    private TextView submitshoppingList;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    /**
     * Inflate the fragment shop view and begin to setup for showing all the items added to the shopping cart
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shop, container, false);


        recyclerView = view.findViewById(R.id.recycler_view_shop_fragment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        shopList = new ArrayList<>();
        shopAdapter = new ShopAdapter(getContext(), shopList);
        recyclerView.setAdapter(shopAdapter);

        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // display the shop items
        final Map<String, Long> postIdList = new HashMap<>();

        DocumentReference shopReference = db.collection("shop")
                .document(firebaseUser.getUid());


        db.collection("shop").document(firebaseUser.getUid())
                .collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            shopList.clear();
                            for (QueryDocumentSnapshot reference : task.getResult()) {
                                Shop shop = reference.toObject(Shop.class);
                                shopList.add(shop);
                            }
                            shopAdapter.notifyDataSetChanged();
                        }
                    }
                });


        // Submit shopping list needs to remove all items from shopping list

        submitshoppingList = view.findViewById(R.id.submit_shopping_cart);
        submitshoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < shopList.size(); i++) {

                    final Shop shop = shopList.get(i);
                    final int position = i;

                    db.collection("posts").document(shop.getPostid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Post post = task.getResult().toObject(Post.class);
                                        int available = post.getAmount();

                                        if (available < shop.getQuantity()) {
                                            AlertDialog.Builder shortcut = new AlertDialog.Builder(getContext());
                                            shortcut.setMessage("The required amount is not available");
                                            AlertDialog alert = shortcut.create();
                                            alert.show();
                                        } else {
                                            // create order
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("amount", shop.getQuantity());
                                            if (post.getType().equals("Service")) {
                                                map.put("buyer", firebaseUser.getUid());
                                                map.put("seller", post.getPoster());
                                            } else {
                                                map.put("seller", firebaseUser.getUid());
                                                map.put("buyer", post.getPoster());
                                            }
                                            map.put("buyerStatus", "Received ?");
                                            map.put("sellerStatus", "Get Paid ?");
                                            DocumentReference reference = db.collection("orders").document();
                                            map.put("id", reference.getId());
                                            map.put("postid", shop.getPostid());
                                            map.put("totalPrice", shop.getTotalPrice());
                                            map.put("date", new Date());
                                            reference.set(map)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                removeFromCart(position, shop.getPostid());
                                                                updatePostAmount(shop.getPostid(), shop.getQuantity());
                                                            } else {
                                                                Toast.makeText(getContext(),
                                                                        "Fail to submit order",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }


                                    } else {
                                        Toast.makeText(getContext(), "Fail to submit order",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
            }
        });
        return view;
    }

    private void removeFromCart(final int position, String postid) {
        // to remove from shop list
        db.collection("shop")
                .document(firebaseUser.getUid())
                .collection("posts")
                .document(postid)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        shopList.remove(position);
                        shopAdapter.notifyItemChanged(position);
                    }
                });
    }


    // update post amount
    private void updatePostAmount(final String postid, int quantity) {
        db.collection("posts").document(postid)
                .update("amount", FieldValue.increment(-quantity))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Fail to update",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
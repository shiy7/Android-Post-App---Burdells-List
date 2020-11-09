package com.example.finalapp.ui.shop;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ShopFragment extends Fragment {
    //Variables for shopping cart display
    private Spinner selectPost;
    private RecyclerView recyclerView;
    private ShopAdapter shopAdapter;
    private List<Post> shopList;
    private TextView submitshoppingList;

    /**
     * Inflate the fragment shop view and begin to setup for showing all the items added to the shopping cart
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        selectPost = view.findViewById(R.id.Shop_Cart_select);
        String[] value = getResources().getStringArray(R.array.select);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, value);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPost.setAdapter(adapter);

        recyclerView = view.findViewById(R.id.recycler_view_shop_fragment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        shopList = new ArrayList<>();
        shopAdapter = new ShopAdapter(getContext(), shopList);
        recyclerView.setAdapter(shopAdapter);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference reference = db.collection("shop");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference documentReference = reference.document(firebaseUser.getUid());
        // display the shop items
        final Map<String, Long> postIdList = new HashMap<>();


//        db.collection("shop").document(firebaseUser.getUid()).get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    shopList.clear();
//                    Map<String, Object> map = documentSnapshot.getData();
//                    if (map != null) {
//                        for (final Map.Entry<String, Object> entry : map.entrySet()) {
//                            // want info from the post
//                            postIdList.put(entry.getKey(), (Long) entry.getValue());
//                        }
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "Failed shopping cart", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        for (final Map.Entry<String, Long> entry : postIdList.entrySet()) {
//            db.collection("posts").document(entry.getKey()).get()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            Post post = documentSnapshot.toObject(Post.class);
//                            post.setQuantity(Long.parseLong(entry.getValue().toString()));
//                            shopList.add(post);
//                        }
//                    });
//        }
//       shopAdapter.setData(shopList);
//
//        // Submit shopping list needs to remove all items from shopping list
//
//        submitshoppingList = view.findViewById(R.id.submit_shopping_cart);
//        submitshoppingList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                db.collection("shop").document(firebaseUser.getUid())
//                        .delete()
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                shopList.clear();
//                                shopAdapter.setData(shopList);
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
//            }
//        });
        return view;
    }

    /**
     * Read the data that is placed in the shopping cart portion of the database
     */
    private void readShop(DocumentReference documentReference) {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    shopList.clear();
                    Map<String, Object> map = documentSnapshot.getData();
                    if (map != null) {
                        for (final Map.Entry<String, Object> entry : map.entrySet()) {
                            String postId = entry.getKey();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("posts").document(postId).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Post post = documentSnapshot.toObject(Post.class);
                                            post.setQuantity(Long.parseLong(entry.getValue().toString()));
                                            shopList.add(post);
                                        }
                                    });
                        }
                        shopAdapter.setData(shopList);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed shopping cart", Toast.LENGTH_SHORT).show();
            }
        });



//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot.exists()) {
//                        shopList.clear();
//                        Map<String, Object> map = documentSnapshot.getData();
//                        if (map != null) {
//                            for (final Map.Entry<String, Object> entry : map.entrySet()) {
//                                String postId = entry.getKey();
//                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                db.collection("posts").document(postId).get()
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                        Post post = documentSnapshot.toObject(Post.class);
//                                        post.setQuantity(Integer.parseInt(entry.getValue().toString()));
//                                        shopList.add(post);
//                                    }
//                                });
//                            }
//                            shopAdapter.setData(shopList);
//                        }
//                    }
//
//                } else {
//                    Toast.makeText(getActivity(), "Failed to display shopping list", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
}
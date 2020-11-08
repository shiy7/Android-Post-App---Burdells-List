package com.example.finalapp.ui.shop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

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

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        shopList = new ArrayList<>();
        shopAdapter = new ShopAdapter(getContext(), shopList);
        recyclerView.setAdapter(shopAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference reference = db.collection("shop");



        // Submit shopping list needs to remove all items from shopping list
        submitshoppingList.setOnClickListener(new View.OnClickListener() {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = firestore.collection("shop").document();
            @Override
            public void onClick(View view) {
                // Need to set tags to store for processing as well as transfer
                // data to the order portion of the user
                documentReference.delete();
            }
        });

        /**
         * Create a query to reference to get all the posts for reading
         */
        selectPost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Query query = reference.whereEqualTo("status", "processing");
                if (position == 0) {
                    readShop(query);
                } else if (position == 1 || position == 2) {
                    query = query.whereEqualTo("type", selected);
                    readShop(query);
                } else {
                    query = query.whereEqualTo("category", selected);
                    readShop(query);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    /**
     * Read the data that is placed in the shopping cart portion of the database
     */
    private void readShop(Query query){
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            shopList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Post post = document.toObject(Post.class);
                                shopList.add(post);
                            }
                            shopAdapter.notifyDataSetChanged();
                        } else{
                            Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
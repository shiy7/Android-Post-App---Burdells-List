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
import android.widget.Toast;

import com.example.finalapp.R;
import com.example.finalapp.model.Post;
import com.example.finalapp.ui.home.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {
    //Variables for shopping cart display
    private Spinner selectPost;
    private Spinner removePost;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> shopList;

    /**
     * Inflate the fragment shop view and begin to setup for showing all the items added to the shopping cart
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        removePost = view.findViewById(R.id.Remove_from_shopping_cart);
        selectPost = view.findViewById(R.id.postSelect);
        String[] value = getResources().getStringArray(R.array.select);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, value);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPost.setAdapter(adapter);
        removePost.setAdapter(adapter);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        shopList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), shopList);
        recyclerView.setAdapter(postAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference reference = db.collection("shop");

        /**
         * Remove Post from the shopping list having trouble getting the selected item and choosing it to be removed
         * calls the removeItemFromShop() function to remove the shopping list item from firebase shop portion
         */
        removePost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Query query = reference.whereEqualTo("status", "active");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * Create a query to reference to get all the posts
         */
        selectPost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Query query = reference.whereEqualTo("status", "active");
                if (position == 0){
                    readShop(query);
                } else if (position == 1 || position == 2){
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
                            postAdapter.notifyDataSetChanged();
                        } else{
                            Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * This method will remove item from the shopping cart list
     */
    public void removeItemFromShop() {
        return;
    }
}
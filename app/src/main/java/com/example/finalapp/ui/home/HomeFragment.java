package com.example.finalapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapp.MainActivity;
import com.example.finalapp.PostActivity;
import com.example.finalapp.R;
import com.example.finalapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private Spinner selectPost;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        selectPost = view.findViewById(R.id.postSelect);
        String[] value = getResources().getStringArray(R.array.select);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_spinner_item, value);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPost.setAdapter(adapter);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference reference = db.collection("posts");
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        selectPost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Query query = reference.whereEqualTo("status", "active")
                        .whereNotEqualTo("poster", fuser.getUid())
                        .orderBy("poster");
                if (position == 0){
                    readPost(query);
                } else if (position == 1 || position == 2){
                    query = query.whereEqualTo("type", selected);
                    readPost(query);
                } else {
                    query = query.whereEqualTo("category", selected);
                    readPost(query);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void readPost(Query query){
        query.orderBy("date").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            postList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                            }
                            postAdapter.notifyDataSetChanged();
                        } else{
                            Toast.makeText(getActivity(), "Failed to get data", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
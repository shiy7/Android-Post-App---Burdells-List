package com.example.finalapp.ui.profile.history;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.finalapp.model.Order;
import com.example.finalapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class OrderHistoryFragment extends Fragment {

    private List<Order> orderList;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private OrderHistoryAdapter orderHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        Spinner selectOrder = view.findViewById(R.id.orderHisSelect);
        String[] value = getResources().getStringArray(R.array.orderSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.requireActivity(), android.R.layout.simple_spinner_item, value);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectOrder.setAdapter(adapter);

        RecyclerView recyclerView = view.findViewById(R.id.orderHis_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        orderList = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(getContext(), orderList);
        recyclerView.setAdapter(orderHistoryAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        selectOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Query query = db.collection("orders");
                if (position == 0) {
                    readOrder(query);
                } else if (position == 1 ) {
                    query = query.whereEqualTo("buyer", firebaseUser.getUid());
                    readOrder(query);
                } else {
                    query = query.whereEqualTo("seller", firebaseUser.getUid());
                    readOrder(query);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }


    private void readOrder(Query query) {
        query.orderBy("date").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            orderList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Order order = document.toObject(Order.class);
                                orderList.add(order);
                            }
                            orderHistoryAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK ){

                Toast.makeText(getContext(), "Right !", Toast.LENGTH_SHORT).show();

//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                String orderId = data.getStringExtra("orderId");
//                final int position = data.getIntExtra("position", 0);
//                db.collection("orders").document(orderId)
//                        .update("buyerStatus", "Done")
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                    orderHistoryAdapter.notifyItemChanged(position);
//                                }
//                            }
//                        });
            }

        } else {
            Toast.makeText(getContext(), "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }

    }
}
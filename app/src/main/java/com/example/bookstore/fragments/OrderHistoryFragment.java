package com.example.bookstore.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.models.Order;
import com.example.bookstore.adaptes.OrderHistoryAdapter;
import com.example.bookstore.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;
    private List<Order> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private FirebaseFirestore firestore;

    public static OrderHistoryFragment newInstance(int position) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderHistoryAdapter(getContext(), orderList);
        recyclerView.setAdapter(adapter);

        loadOrdersFromFirestore(position);
        return view;
    }

    private void loadOrdersFromFirestore(int position) {
        Query query;
        switch (position) {
            case 0: // All
                query = firestore.collection("orders");
                break;
            case 1: // Processing
                query = firestore.collection("orders").whereEqualTo("status", "Pending");
                break;
            case 2: // Confirmed
                query = firestore.collection("orders").whereEqualTo("status", "Confirmed");
                break;
            case 3: // Completed
                query = firestore.collection("orders").whereEqualTo("status", "Completed");
                break;
            case 4: // Cancelled
                query = firestore.collection("orders").whereEqualTo("status", "Cancelled");
                break;
            default:
                query = firestore.collection("orders");
                break;
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            orderList.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Order order = document.toObject(Order.class);
                orderList.add(order);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle any errors
        });
    }
}

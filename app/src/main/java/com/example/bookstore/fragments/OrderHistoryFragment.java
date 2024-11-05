package com.example.bookstore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.R;
import com.example.bookstore.adaptes.OrderHistoryAdapter;
import com.example.bookstore.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment  {
    private static final String ARG_POSITION = "position";
    private int position;
    private List<Order> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private ProgressBar progressBar; // Khai báo ProgressBar
    private FirebaseAuth auth;
    // ID người dùng cố định
  // private static final String USER_ID = "TV1xyM399EgkXAHanQPoyIzcLnA3";

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
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
        Log.d("OrderHistoryFragment", "Position: " + position);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        progressBar = view.findViewById(R.id.progressBar); // Khởi tạo ProgressBar

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderHistoryAdapter(getContext(), orderList);
        recyclerView.setAdapter(adapter);

        // Load orders based on the selected tab position
        loadOrdersFromFirestore(position);
        return view;
    }

    private void loadOrdersFromFirestore(int position) {
        Log.d("OrderHistoryFragment", "Loading orders for position: " + position);

        // Hiển thị ProgressBar khi bắt đầu tải
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE); // Ẩn RecyclerView
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        // Sử dụng USER_ID cố định
        String userId = user != null ? user.getUid() : "TV1xyM399EgkXAHanQPoyIzcLnA3";;
        Log.d("OrderHistoryFragment", "User ID: " + userId);

        // Remove previous listener if exists
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        Query query = db.collection("Orders").document(userId).collection("Order");

        // Apply filters based on tab position
        switch (position) {
            case 1: query = query.whereEqualTo("status", "0"); break; // Pending
            case 2: query = query.whereEqualTo("status", "1"); break; // Confirmed
            case 3: query = query.whereEqualTo("status", "2"); break; // Completed
            case 4: query = query.whereEqualTo("status", "3"); break; // Cancelled
        }

        listenerRegistration = query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            // Ẩn ProgressBar khi tải xong
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE); // Hiển thị lại RecyclerView

            if (e != null) {
                Log.e("OrderHistoryFragment", "Error loading orders", e);
                Toast.makeText(getContext(), "Failed to load orders.", Toast.LENGTH_SHORT).show();
                return;
            }

            orderList.clear(); // Clear old data
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String orderId = document.getId();  // Lấy ID của document
                    Order order = document.toObject(Order.class);
                    order.setOrderId(orderId);  // Thiết lập orderId cho đối tượng Order
                    orderList.add(order);
                }
                adapter.notifyDataSetChanged();
                Log.d("OrderHistoryFragment", "Loaded " + orderList.size() + " orders.");
            } else {
                Log.d("OrderHistoryFragment", "No orders found.");

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove Firestore listener to avoid memory leaks
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

}

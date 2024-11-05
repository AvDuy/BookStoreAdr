package com.example.bookstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookstore.R;
import com.example.bookstore.adaptes.ProductHistoryAdapter;
import com.example.bookstore.models.CartItem;
import com.example.bookstore.models.Order;
import com.example.bookstore.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailHistoryActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailHistoryActivity";
    private RecyclerView recyclerViewProducts;
    private ProductHistoryAdapter productAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private FirebaseFirestore db;

    private TextView textOrderId, textOrderDate, textOrderStatus, textOrderTotalAmount;
    private Button btnCancelOrder;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_history);
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại OrderHistoryActivity
                Intent intent = new Intent(OrderDetailHistoryActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                finish(); // Kết thúc OrderDetailHistoryActivity
            }
        });
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        textOrderId = findViewById(R.id.textOrderId);
        textOrderDate = findViewById(R.id.textOrderDate);
        textOrderStatus = findViewById(R.id.textOrderStatus);
        textOrderTotalAmount = findViewById(R.id.textOrderTotalAmount);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = (currentUser != null) ? currentUser.getUid() : "TV1xyM399EgkXAHanQPoyIzcLnA3";

        productAdapter = new ProductHistoryAdapter(this, cartItemList);
        recyclerViewProducts.setAdapter(productAdapter);

        // Retrieve Order from intent
        Order order = (Order) getIntent().getSerializableExtra("orders");
        if (order != null) {
            displayOrderDetails(order);
            loadCartItems(order.getCartId());
        } else {
            Toast.makeText(this, "Order details not available.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if order details are not present
        }
    }

    private void displayOrderDetails(Order order) {
        textOrderId.setText("Order ID: " + order.getOrderId());
        textOrderDate.setText("Order Date: " + order.getCreatedAt());
        textOrderTotalAmount.setText("Total Amount: " + order.getTotalAmount() + " VND");

        // Check order status
        int status = Integer.parseInt(order.getStatus());
        textOrderStatus.setText("Status: " + getStatusText(status));
        btnCancelOrder.setVisibility(status == 0 ? View.VISIBLE : View.GONE);

        // Set button click listener
        if (status == 0) {
            btnCancelOrder.setOnClickListener(v -> cancelOrder(order.getOrderId()));
        }
    }

    private String getStatusText(int status) {
        switch (status) {
            case 0: return "Pending";
            case 1: return "Confirmed";
            case 2: return "Completed";
            case 3: return "Canceled";
            default: return "Unknown";
        }
    }

    private void loadCartItems(String cartId) {
        db.collection("Cart").document(userId)
                .collection("CartItem").document(cartId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                List<Map<String, Object>> cartItems = (List<Map<String, Object>>) document.get("cartItems");
                                if (cartItems != null) {
                                    cartItemList.clear();
                                    for (Map<String, Object> item : cartItems) {
                                        Map<String, Object> productDetails = (Map<String, Object>) item.get("product");
                                        if (productDetails != null) {
                                            String imgUrl = (String) productDetails.get("image");
                                            String name = (String) productDetails.get("name");
                                            double price = ((Number) productDetails.get("price")).doubleValue();
                                            int quantity = ((Number) item.get("quantity")).intValue();
                                            double productTotal = ((Number) item.get("productTotal")).doubleValue();

                                            Product product = new Product(imgUrl, name, price);
                                            CartItem cartItem = new CartItem(product, quantity, productTotal);

                                            cartItemList.add(cartItem);
                                        }
                                    }
                                    productAdapter.notifyDataSetChanged();
                                } else {
                                    Log.w(TAG, "No cart items found for cartId: " + cartId);
                                    Toast.makeText(OrderDetailHistoryActivity.this, "No cart items found!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.w(TAG, "No document found for cartId: " + cartId);
                                Toast.makeText(OrderDetailHistoryActivity.this, "Failed to load cart items.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(TAG, "Error getting document.", task.getException());
                            Toast.makeText(OrderDetailHistoryActivity.this, "Failed to load cart items.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void cancelOrder(String orderId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "3"); // Set status to Canceled

        db.collection("Orders").document(userId).collection("Order").document(orderId)
                .update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(OrderDetailHistoryActivity.this, "Order canceled successfully", Toast.LENGTH_SHORT).show();
                        textOrderStatus.setText("Status: Canceled");
                        btnCancelOrder.setVisibility(View.GONE); // Hide the cancel button
                    } else {
                        Toast.makeText(OrderDetailHistoryActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error updating order status", task.getException());
                    }
                });
    }
}

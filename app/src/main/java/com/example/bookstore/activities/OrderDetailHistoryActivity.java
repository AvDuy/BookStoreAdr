package com.example.bookstore.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookstore.R;
import com.example.bookstore.adaptes.ProductHistoryAdapter;
import com.example.bookstore.models.CartItem;
import com.example.bookstore.models.Order;
import com.example.bookstore.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailHistoryActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailHistoryActivity";
    private RecyclerView recyclerViewProducts;
    private ProductHistoryAdapter productAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private TextView textOrderId, textOrderDate, textOrderStatus, textOrderTotalAmount, textPaymentMethod, textAddress; // Added textAddress
    private Button btnCancelOrder;
    private FirebaseUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_history);

        // Initialize views, Firebase, and necessary variables
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to OrderHistoryActivity
                Intent intent = new Intent(OrderDetailHistoryActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                finish(); // Close OrderDetailHistoryActivity
            }
        });

        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        textOrderId = findViewById(R.id.textOrderId);
        textOrderDate = findViewById(R.id.textOrderDate);
        textOrderStatus = findViewById(R.id.textOrderStatus);
        textOrderTotalAmount = findViewById(R.id.textOrderTotalAmount);
        textPaymentMethod = findViewById(R.id.textPaymentMethod); // Initialize textPaymentMethod
        textAddress = findViewById(R.id.textAddress); // Initialize textAddress
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
        textOrderDate.setText("Order Date: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(order.getCreatedAt()));
        textOrderTotalAmount.setText("Total Amount: " + order.getTotalAmount() + " VND");

        // Get and set payment method
        String paymentMethod = order.getPaymentMethod();
        textPaymentMethod.setText("Payment Method: " + paymentMethod);

        // Check if addressId is not null
        String addressId = order.getAddressId();
        if (addressId != null && !addressId.isEmpty()) {
            loadAddress(addressId);
        } else {
            Log.w(TAG, "Address ID is null or empty");
            textAddress.setText("Address: Not provided");
        }

        // Check order status
        int status = Integer.parseInt(order.getStatus());
        textOrderStatus.setText("Status: " + getStatusText(status));
        btnCancelOrder.setVisibility(status == 0 ? View.VISIBLE : View.GONE);

        // Set button click listener for cancel order
        if (status == 0) {
            btnCancelOrder.setOnClickListener(v -> cancelOrder(order.getOrderId()));
        }
    }

    private void loadAddress(String addressId) {
        userId = (currentUser != null) ? currentUser.getUid() : "O101GwCnB5VWnl1WAjNlvnOBM2i2";
        db.collection("users").document(userId)
                .collection("addresses").document(addressId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Access multiple fields from the document
                        String country = task.getResult().getString("country");
                        String district = task.getResult().getString("district");
                        String province = task.getResult().getString("province");
                        String street = task.getResult().getString("street");
                        String ward = task.getResult().getString("ward");

                        // Format the address string
                        String address = String.format("Street: %s, Ward: %s, District: %s, Province: %s, Country: %s",
                                street, ward, district, province, country);
                        textAddress.setText("Address: " + address);
                    } else {
                        Log.w(TAG, "Failed to load address", task.getException());
                        textAddress.setText("Address: Unknown");
                    }
                });
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
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
    private void cancelOrder(String orderId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Hiển thị ProgressDialog khi đang xử lý
                    progressDialog.setMessage("Đang hủy đơn hàng...");
                    progressDialog.show();

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("status", "3"); // Đặt trạng thái thành "Đã hủy"

                    db.collection("Orders").document(userId).collection("Order").document(orderId)
                            .update(updates)
                            .addOnCompleteListener(task -> {
                                progressDialog.dismiss(); // Đóng ProgressDialog
                                if (task.isSuccessful()) {
                                    showSnackbar("Đơn hàng đã được hủy thành công");
                                    textOrderStatus.setText("Trạng thái: Đã hủy");
                                    btnCancelOrder.setVisibility(View.GONE); // Ẩn nút hủy

                                    // Gửi thông báo hủy đơn hàng
                                    String title = "Đơn hàng đã được hủy";
                                    String message = "Đơn hàng ID: " + orderId + " đã được hủy thành công.";
                                    sendNotification(title, message);

                                    // Lưu thông báo vào Firestore
                                    saveNotificationToFirestore(title, message);
                                } else {
                                    showSnackbar("Hủy đơn hàng không thành công");
                                    Log.w(TAG, "Lỗi khi cập nhật trạng thái đơn hàng", task.getException());
                                }
                            });
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss(); // Đóng hộp thoại nếu người dùng chọn không
                })
                .show();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "order_cancel_channel";

        // Tạo NotificationChannel cho Android Oreo trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Order Cancellation", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo Intent để mở OrderHistoryActivity khi người dùng nhấn vào thông báo
        Intent intent = new Intent(this, OrderHistoryActivity.class);
        // Thêm FLAG_IMMUTABLE vào PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Tạo Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Hình ảnh biểu tượng thông báo
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Tự động hủy thông báo khi người dùng nhấn vào

        // Hiển thị thông báo
        notificationManager.notify(1, builder.build());
    }



    private void saveNotificationToFirestore(String title, String message) {
        // Lấy thời gian hiện tại dưới dạng chuỗi định dạng
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());  // Gọi trực tiếp hàm `new Date()`

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("date", currentDateTime);  // Lưu thời gian hiện tại dưới dạng chuỗi

        db.collection("Notifications").document(userId)
                .collection("UserNotifications").add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Notification saved successfully: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error saving notification", e);
                });
    }


}

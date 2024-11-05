package com.example.bookstore.adaptes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookstore.activities.OrderDetailHistoryActivity;
import com.example.bookstore.models.Cart;
import com.example.bookstore.models.CartItem;
import com.example.bookstore.models.Order;
import com.example.bookstore.R;
import com.example.bookstore.models.Product;
import com.google.android.play.integrity.internal.u;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private static final String TAG = "OrderHistoryAdapter"; // Thêm biến TAG để sử dụng cho log

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view, this); // Pass the adapter instance to ViewHolder
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        // Lấy đối tượng Order từ danh sách dựa trên vị trí
        Order order = orderList.get(position);

        // Cập nhật thông tin cho ViewHolder
        holder.textOrderId.setText("Order ID: " + order.getOrderId()); // Hiển thị ID của đơn hàng với chú thích
        holder.textOrderStatus.setText("Status: " + getStatusString(order.getStatus())); // Hiển thị trạng thái đơn hàng với chú thích
        holder.textOrderDate.setText("Date: " + order.getCreatedAt().toString()); // Hiển thị ngày tạo đơn hàng với chú thích
        holder.textOrderTotal.setText("Total Amount: " + String.valueOf(order.getTotalAmount()) + " VND"); // Hiển thị tổng số tiền với chú thích

        // Gọi hàm loadImageForOrder để tải hình ảnh sản phẩm liên quan đến đơn hàng
        loadImageForOrder(holder, order);
    }

    private String getStatusString(String status) {
        switch (status) {
            case "0":
                return "Pending"; // Trạng thái đang chờ
            case "1":
                return "Confirmed"; // Trạng thái đã xác nhận
            case "2":
                return "Completed"; // Trạng thái đã hoàn thành
            case "3":
                return "Cancelled"; // Trạng thái đã hủy
            default:
                return "Unknown"; // Trạng thái không xác định
        }
    }

    private void loadImageForOrder(OrderViewHolder holder, Order order) {
        String cartId = order.getCartId();
        Log.d(TAG, "Loading cart for order with cart ID: " + cartId);

        if (cartId == null) {
            Log.e(TAG, "Cart ID is null.");
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "TV1xyM399EgkXAHanQPoyIzcLnA3";
        // Retrieve the cart items for the specified user and cartId
        FirebaseFirestore.getInstance()
                .collection("Cart")
                .document(userId) // Replace with the appropriate user ID
                .collection("CartItem")
                .document(cartId) // Assuming cartId is the document ID for CartItem
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract the list of cart items from the document
                        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) documentSnapshot.get("cartItems");
                        if (cartItems != null) {
                            double total = 0d; // Initialize total variable
                            List<Product> products = new ArrayList<>();

                            for (Map<String, Object> item : cartItems) {
                                // Get the product details map
                                Map<String, Object> productDetails = (Map<String, Object>) item.get("product");

                                // Extract product information from the nested map
                                if (productDetails != null) {
                                    String imgUrl = (String) productDetails.get("image");
                                    String name = (String) productDetails.get("name");
                                    double price = ((Number) Objects.requireNonNull(productDetails.get("price"))).doubleValue();
                                    int quantity = ((Number) item.get("quantity")).intValue();
                                    double productTotal = ((Number) item.get("productTotal")).doubleValue();

                                    // Create a Product instance
                                    Product product = new Product(imgUrl, name, price);

                                    // Add price to total and add the product to the list
                                    total += productTotal;
                                    products.add(product);

                                    // Optionally, you can load the product image for the first item here
                                    if (products.size() == 1) { // Load the image for the first product only
                                        loadProductImage(holder, imgUrl);
                                    }
                                }
                            }

                            // You can also display the total price or update UI here if needed
                            Log.d(TAG, "Total amount for cart: " + total);
                            // displayProducts(products, total); // If you have a method to display products
                        } else {
                            Log.e(TAG, "No cart items found in document.");
                        }
                    } else {
                        Log.e(TAG, "Cart item document does not exist for cart ID: " + cartId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading cart item for cart ID: " + cartId, e);
                });
    }

    private void loadProductImage(OrderViewHolder holder, String imageUrl) {
        if (imageUrl != null) {
            Log.d(TAG, "Loading image from URL: " + imageUrl);
            Glide.with(holder.imageProduct.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.plusicon)
                    .error(R.drawable.plusicon)
                    .into(holder.imageProduct);
        } else {
            Log.e(TAG, "Image URL is null, setting default image.");
            holder.imageProduct.setImageResource(R.drawable.plusicon); // Set a default image if the URL is null
        }
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId;
        TextView textOrderStatus;
        TextView textOrderDate;
        TextView textOrderTotal;
        ImageView imageProduct; // Thêm ImageView cho hình ảnh sản phẩm
        private OrderHistoryAdapter adapter; // Tham chiếu đến adapter

        public OrderViewHolder(@NonNull View itemView, OrderHistoryAdapter adapter) {
            super(itemView);
            this.adapter = adapter; // Gán adapter cho biến
            textOrderId = itemView.findViewById(R.id.textOrderId);
            textOrderStatus = itemView.findViewById(R.id.textOrderStatus);
            textOrderDate = itemView.findViewById(R.id.textOrderDate);
            textOrderTotal = itemView.findViewById(R.id.textOrderTotal);
            imageProduct = itemView.findViewById(R.id.imageProduct);

            // Thiết lập sự kiện nhấp chuột vào itemView
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition(); // Lấy vị trí của item được nhấp
                if (position != RecyclerView.NO_POSITION) {
                    // Lấy đối tượng Order tương ứng với vị trí
                    Order order = adapter.orderList.get(position); // Lấy đối tượng order từ adapter
                    // Khởi tạo Intent để mở OrderDetailActivity
                    Intent intent = new Intent(itemView.getContext(), OrderDetailHistoryActivity.class);
                    intent.putExtra("orders", order); // Truyền Order ID vào Intent
                    itemView.getContext().startActivity(intent); // Bắt đầu activity chi tiết đơn hàng
                }
            });
        }
    }
}



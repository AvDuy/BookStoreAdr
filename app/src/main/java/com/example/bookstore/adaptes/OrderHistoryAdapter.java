package com.example.bookstore.adaptes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookstore.models.Order;
import com.example.bookstore.R;
import com.example.bookstore.activities.OrderDetailHistoryActivity;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textOrderId.setText("ID: " + order.getUserId());
        holder.textOrderStatus.setText("Order status: " + order.getStatus());
        holder.textOrderDate.setText("Ordered date: " + order.getCreatedAt());
        holder.textOrderTotal.setText("Total: " + order.getTotalAmount() + " VNĐ");

        // Load the first product's image if available
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            String imageUrl = order.getItems().get(0).getImage(); // Get the image URL of the first product
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.plusicon) // Placeholder image while loading
                    .error(R.drawable.plusicon) // Error image if load fails
                    .into(holder.imageProduct);
        } else {
            holder.imageProduct.setImageResource(R.drawable.plusicon); // Set a default image if no products
        }

        // Click listener for opening order detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailHistoryActivity.class);
            intent.putExtra("orderId", order.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ViewHolder for each item in RecyclerView
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId;
        TextView textOrderStatus;
        TextView textOrderDate;
        TextView textOrderTotal;
        ImageView imageProduct; // Add ImageView for the product image

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.textOrderId);
            textOrderStatus = itemView.findViewById(R.id.textOrderStatus);
            textOrderDate = itemView.findViewById(R.id.textOrderDate);
            textOrderTotal = itemView.findViewById(R.id.textOrderTotal);
            imageProduct = itemView.findViewById(R.id.imageProduct); // Initialize ImageView
        }
    }
}

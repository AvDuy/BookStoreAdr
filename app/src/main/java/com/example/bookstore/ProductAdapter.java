package com.example.bookstore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookstore.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    private List<Product> products;
    private OnProductClickListener listener;

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
        return new ProductHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(products.get(position).getImage()) // Assuming getImage() returns a String URL
                .into(holder.imv); // Your ImageView
        holder.tv_title.setText(products.get(position).getName());
        holder.tv_price.setText(String.format("%.2f", products.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder{
        ImageView imv;
        TextView tv_title, tv_des, tv_price, tv_amount;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            imv = itemView.findViewById(R.id.imv_ava);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_price = itemView.findViewById(R.id.productPrice);
            tv_amount = itemView.findViewById(R.id.productPrice3);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onProductClick(products.get(pos)); // Notify the listener about the clicked item
                    }
                    Log.d("GestureActivity_log", "Da chon anh: " + pos);
                }
            });
        }
    }
    // Define the interface
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
}

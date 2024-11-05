package com.example.bookstore.adaptes;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookstore.R;
import com.example.bookstore.models.CartItem;
import java.util.List;

public class ProductHistoryAdapter extends RecyclerView.Adapter<ProductHistoryAdapter.ProductViewHolder> {
    private final List<CartItem> cartItemList;
    private final Context context;

    public ProductHistoryAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        holder.textProductName.setText(cartItem.getProduct().getName());
        holder.textProductQuantity.setText("Quantity: " + cartItem.getQuantity());
        holder.textProductPrice.setText("Price: " +"$" + cartItem.getProductTotal()  );

        // Load image with Glide
        Glide.with(context)
                .load(cartItem.getProduct().getImage())
                .into(holder.imageProduct);
    }


        @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textProductQuantity, textProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textProductQuantity = itemView.findViewById(R.id.textProductQuantity);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
        }
    }
}

package com.example.bookstore.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookstore.CheckOutActivity;
import com.example.bookstore.LocationActivity;
import com.example.bookstore.R;
import com.example.bookstore.models.Cart;
import com.example.bookstore.models.CartItem;
import com.example.bookstore.models.NewProductsModel;
import com.example.bookstore.models.PopularProductsModel;
import com.example.bookstore.models.Product;
import com.example.bookstore.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DetailedActivity extends AppCompatActivity {
    private List<CartItem> cartItems;
    ImageView detailedImg;
    TextView rating, name, description, price, quantity;
    Button addToCart, buyNow;
    ImageView addItems, removeItems;
    int totalQuantity = 1;
    int totalPrice = 0;
    private Cart cart;
    //New Products
    NewProductsModel newProductsModel = null;

    //Popular Products
    PopularProductsModel popularProductsModel = null;

    //Show All
    ShowAllModel showAllModel = null;
    FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        final Object obj = getIntent().getSerializableExtra("detailed");

        if (obj instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
        } else if (obj instanceof PopularProductsModel) {
            popularProductsModel = (PopularProductsModel) obj;
        } else if (obj instanceof ShowAllModel) {
            showAllModel = (ShowAllModel) obj;
        }

        detailedImg = findViewById(R.id.detailed_img);
        quantity = findViewById(R.id.quantity);
        name = findViewById(R.id.detailed_name);
        rating = findViewById(R.id.rating);
        description = findViewById(R.id.detailed_desc);
        price = findViewById(R.id.detailed_price);
        addToCart = findViewById(R.id.add_to_cart);
        buyNow = findViewById(R.id.buy_now);
        addItems = findViewById(R.id.add_item);
        removeItems = findViewById(R.id.remove_item);

        //New Products
        if (newProductsModel != null) {
            Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailedImg);
            name.setText(newProductsModel.getName());
            rating.setText(newProductsModel.getRating());
            description.setText(newProductsModel.getDescription());
            price.setText(String.valueOf(newProductsModel.getPrice()));
            name.setText(newProductsModel.getName());

            totalPrice = newProductsModel.getPrice() * totalQuantity;
        }
        //Popular Products
        if (popularProductsModel != null) {
            Glide.with(getApplicationContext()).load(popularProductsModel.getImg_url()).into(detailedImg);
            name.setText(popularProductsModel.getName());
            rating.setText(popularProductsModel.getRating());
            description.setText(popularProductsModel.getDescription());
            price.setText(String.valueOf(popularProductsModel.getPrice()));
            name.setText(popularProductsModel.getName());
            totalPrice = popularProductsModel.getPrice() * totalQuantity;
        }

        //Show ALl Products
        if (showAllModel != null) {
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailedImg);
            name.setText(showAllModel.getName());
            rating.setText(showAllModel.getRating());
            description.setText(showAllModel.getDescription());
            price.setText(String.valueOf(showAllModel.getPrice()));
            name.setText(showAllModel.getName());
            totalPrice = showAllModel.getPrice() * totalQuantity;
        }

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBuyNow();
            }
        });

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQuantity<10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                    if(newProductsModel != null){
                        totalPrice = newProductsModel.getPrice()*totalQuantity;
                    }
                    if(popularProductsModel != null){
                        totalPrice = popularProductsModel.getPrice()*totalQuantity;
                    }
                    if(showAllModel != null){
                        totalPrice = showAllModel.getPrice()* totalQuantity;
                    }
                }
            }
        });

        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQuantity>1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });
    }

    private void setBuyNow(){
        cartItems = new ArrayList<>();
        int quanti = Integer.parseInt(quantity.getText().toString());
        Product product = new Product(getIntent().getStringExtra("imgURL"),name.getText().toString(),Double.parseDouble(price.getText().toString()));
        cartItems.add(new CartItem(
                product,quanti,product.getPrice()*quanti));

        cart = new Cart(cartItems);
        firestore.collection("Cart").document(auth.getCurrentUser().getUid())
                .collection("CartItem")
                .add(cart)
                .addOnSuccessListener(documentReference -> {
                    String cartId= documentReference.getId();
                    Log.d("CartItem", "CraftItem add with ID: " + cartId);;
                    Intent intent = new Intent(DetailedActivity.this, CheckOutActivity.class);
                    intent.putExtra("cartId", cartId);
                    intent.putExtra("action", "buynow");
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Log.w("CartItem", "Error adding address", e));
    }

    private void addToCart() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String cartID = sharedPreferences.getString("cartId", null); // null is the default value if "cartId" is not found
        if(cartID == null){
            cartItems = new ArrayList<>();
            int quanti = Integer.parseInt(quantity.getText().toString());
            Product product = new Product(getIntent().getStringExtra("imgURL"),name.getText().toString(),Double.parseDouble(price.getText().toString()));
            cartItems.add(new CartItem(
                    product,quanti,product.getPrice()*quanti));

            cart = new Cart(cartItems);
            firestore.collection("Cart").document(auth.getCurrentUser().getUid())
                    .collection("CartItem")
                    .add(cart)
                    .addOnSuccessListener(documentReference -> {
                        String cartId= documentReference.getId();
                        Log.d("CartItem", "CraftItem add with ID: " + cartId);;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("cartId", cartId);
                        editor.apply();
                        Toast.makeText(DetailedActivity.this,"Add to cart",Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Log.w("CartItem", "Error adding address", e));
        }else{
            firestore.collection("Cart")
                    .document(auth.getCurrentUser().getUid())
                    .collection("CartItem")
                    .document(cartID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Retrieve existing cart data
                        Cart existingCart = documentSnapshot.toObject(Cart.class);

                        if (existingCart != null) {
                            // Get the current cartItems list or initialize it if null
                            List<CartItem> cartItems = existingCart.getCartItems();
                            if (cartItems == null) {
                                cartItems = new ArrayList<>();
                            }

                            // Create new CartItem
                            int quanti = Integer.parseInt(quantity.getText().toString());
                            Product product = new Product(
                                    getIntent().getStringExtra("imgURL"),
                                    name.getText().toString(),
                                    Double.parseDouble(price.getText().toString())
                            );
                            CartItem newCartItem = new CartItem(product, quanti, product.getPrice() * quanti);

                            // Add the new item to the cartItems list
                            cartItems.add(newCartItem);

                            // Update the cart with the modified cartItems list
                            existingCart.setCartItems(cartItems);

                            // Update Firestore with the modified cart
                            firestore.collection("Cart")
                                    .document(auth.getCurrentUser().getUid())
                                    .collection("CartItem")
                                    .document(cartID)
                                    .set(existingCart) // set() will overwrite only the fields we modify
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("CartItem", "Cart updated with new item: " + cartID);
                                        Toast.makeText(DetailedActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("CartItem", "Error adding item to cart", e);
                                        Toast.makeText(DetailedActivity.this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("CartItem", "Error retrieving cart", e);
                        Toast.makeText(DetailedActivity.this, "Failed to retrieve cart", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
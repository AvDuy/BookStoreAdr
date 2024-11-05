package com.example.bookstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.R;
import com.example.bookstore.models.Cart;
import com.example.bookstore.models.CartItem;
import com.example.bookstore.models.Order;
import com.example.bookstore.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetailHistoryActivity extends AppCompatActivity {
    static final String TAG = "OrderDetailHistoryActivity";
    private TextView textOrderId, textOrderDate, textOrderStatus, textOrderTotalAmount, textShippingAddress;
    private Button btnCancelOrder;
    private RecyclerView recyclerViewProducts;
 //   private ProductAdapter productAdapter;
    private FirebaseFirestore db;
    private List<CartItem> cartItemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Intent i = getIntent();


        // Retrieve the Orders object passed from the previous activity
       // Order orders = (Order) i.getSerializableExtra("orders");
        setContentView(R.layout.activity_order_detail_history);

        db = FirebaseFirestore.getInstance();
        loadCartItems("U3JY43CvNLRggsHUMWKB");

    }
    private void loadCartItems(String cartId) {


        // Load the cart document using the user's UID and cart ID
        db.collection("Cart").document("TV1xyM399EgkXAHanQPoyIzcLnA3")
                .collection("CartItem").document(cartId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Get the list of cart items from the document
                                List<Map<String, Object>> cartItems = (List<Map<String, Object>>) document.get("cartItems");
                                if (cartItems != null && !cartItems.isEmpty()) {
                                    double  total = 0d; // Initialize total amount
                                    cartItemList.clear(); // Clear previous items

                                    // Loop through each cart item and extract details
                                    for (Map<String, Object> item : cartItems) {
                                        Map<String, Object> productDetails = (Map<String, Object>) item.get("product");
                                        if (productDetails != null) {
                                            String imgUrl = (String) productDetails.get("image");
                                            String name = (String) productDetails.get("name");
                                            double price = ((Number) productDetails.get("price")).doubleValue();
                                            int quantity = ((Number) item.get("quantity")).intValue();
                                            double productTotal = ((Number) item.get("productTotal")).doubleValue();

                                            // Create a Product instance
                                           // Product product = new Product(imgUrl, name, price);
                                            //cartItemList.add(product); // Add to the cart items list


                                        }
                                    }

                                    // Notify the adapter about the new data
                                    // productAdapter.notifyDataSetChanged(); // Uncomment when the adapter is ready

                                    // Display the products and total amount

                                } else {
                                    Log.w("loadCartItems", "No cart items found for cartId: " + cartId);
                                    Toast.makeText(OrderDetailHistoryActivity.this, "No cart items found!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.w("loadCartItems", "No such document for cartId: " + cartId);
                                Toast.makeText(OrderDetailHistoryActivity.this, "Cart not found!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("loadCartItems", "Error getting document.", task.getException());
                            Toast.makeText(OrderDetailHistoryActivity.this, "Failed to load cart items.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }







}




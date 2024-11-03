package com.example.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.models.Address;
import com.example.bookstore.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private TextView userAddressTextView, totalPrice;
    private Button changeLocation;
    AlertDialog.Builder builder;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CheckScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        userAddressTextView = findViewById(R.id.userAddress);
        // Fetch user data
        fetchUserAddress();

        fetchCartProduct();

        changeLocation = findViewById(R.id.btn_location);
        changeLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Start LocationActivity
                Intent intent = new Intent(CheckOutActivity.this, LocationActivity.class);
                startActivity(intent);
            }
        });

    }

    private void displayProducts(List<Product> products, double total){
        totalPrice = findViewById(R.id.price);
        totalPrice.setText(String.format("%.2f", total));

        // Find RecyclerView inside the inflated layout
        RecyclerView rec = findViewById(R.id.cartList);
        ProductAdapter adapter = new ProductAdapter(products, this);
        rec.setLayoutManager(new LinearLayoutManager(this));
        rec.setAdapter(adapter);

    }

    private void fetchCartProduct(){
        db.collection("AllProducts")
                .limit(5) // Limit to 3 products
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Product> products = new ArrayList<>();
                            double total = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Assuming you have a Product class to hold product data
                                Product product = new Product(
                                        document.getString("img_url"),
                                        document.getString("name"),
                                        document.getDouble("price"));
                                total += product.getPrice();
                                products.add(product);
                            }

                            // Do something with the list of products (e.g., update UI)
                            displayProducts(products, total);
                        } else {
                            // Handle the error
                            Log.w("MainActivity", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void fetchUserAddress() {

        String userId = "currentUserId";  // Replace with actual user ID

        db.collection("users").document("userId")
                .collection("addresses").document("u0i1eyDFkXNFcAbSNZdd")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Address address = document.toObject(Address.class);

                            if (address != null) {
                                // Display address data
                                String fullAddress = address.getStreet() + ", " + address.getWard() + ", "
                                        + address.getDistrict() + ", " +address.getProvince() ;
                                userAddressTextView.setText(fullAddress);
                            }
                        } else {
                            Log.d("Firebase", "No such document");
                        }
                    } else {
                        Log.d("Firebase", "Failed with: ", task.getException());
                    }
                });
    }

    @Override
    public void onProductClick(Product product) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
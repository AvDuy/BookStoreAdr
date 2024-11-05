package com.example.bookstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.bookstore.models.Cart;
import com.example.bookstore.models.CartItem;
import com.example.bookstore.models.Order;
import com.example.bookstore.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CheckOutActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private TextView userAddressTextView, totalPrice, userPhone;
    private Button changeLocation, submitOrder;
    private List<Product> orderList;
    private List<CartItem> cartItems;
    private String addressId, cartId;
    private Double total = 0d;
    private Cart cart;
    private RadioGroup paymentRadioGroup;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //signInAnonmously();
        setContentView(R.layout.checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CheckScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addressId = getIntent().getStringExtra("AddressID");
        cartId = getIntent().getStringExtra("cartId");
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String cartID = sharedPreferences.getString("cartId", null); // null is the default value if "cartId" is not found

        // Initialize views
        userAddressTextView = findViewById(R.id.userAddress);
        userPhone = findViewById(R.id.userPhone);
        // Fetch user data
        if(addressId != null){
            fetchUserAddress();
        }
        if (getIntent().getStringExtra("action")!= null){
            fetchCartProduct();
        }else{
            cartId = cartID;
            fetchCartProduct();
        }
        //fetchAllProduct();

        submitOrder = findViewById(R.id.btn_ordersubmit);
        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSubmitOrder();
            }
        });

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

    private void fetchCartProduct() {
        if(cartId !=null) {
            db.collection("Cart").document(currentUser.getUid())
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
                                        total = 0d;
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

                                                // Create a Product instance (modify the Product class if needed to handle quantity and productTotal)
                                                Product product = new Product(imgUrl, name, price);

                                                // Add price to total and add the product to the list
                                                total += productTotal;
                                                products.add(product);
                                            }

                                        }
                                        if (orderList == null) {
                                            orderList = products;
                                        }
                                        // Display the list of products and the total price
                                        displayProducts(products, total);
                                    }
                                } else {
                                    Log.w("MainActivity", "No such document");
                                }
                            } else {
                                // Handle the error
                                Log.w("MainActivity", "Error getting document.", task.getException());
                            }
                        }
                    });
        }
    }

    private void setUp(){
        cartItems = new ArrayList<>();
        cartItems.add(new CartItem(
                orderList.get(0),1,orderList.get(0).getPrice()*1));
        cartItems.add(new CartItem(
                orderList.get(1),1,orderList.get(1).getPrice()*1));
        cartItems.add(new CartItem(
                orderList.get(2),1,orderList.get(2).getPrice()*1));

        cart = new Cart(cartItems);
        db.collection("Cart").document(currentUser.getUid())
                .collection("CartItem")
                .add(cart)
                .addOnSuccessListener(documentReference -> {
                    cartId = documentReference.getId();
                    Log.d("CartItem", "CraftItem add with ID: " + cartId);
                    fetchCartProduct();
                })
                .addOnFailureListener(e -> Log.w("CartItem", "Error adding address", e));
    }

    private void setSubmitOrder(){
        Date now = new Date();
        paymentRadioGroup = findViewById(R.id.payment_method);
        int selectedPayment = paymentRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedPayment);
        //Order User Cart Add Payment total status
        Order order = new Order(currentUser.getUid(),cartId,addressId,selectedRadioButton.getText().toString(),total,"0",now, now);
        db.collection("Orders").document(currentUser.getUid())
                .collection("Order")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    String orderId = documentReference.getId();
                    Log.d("OrderAdd", "OrderAdd added with ID: " + orderId);
                    Toast.makeText(CheckOutActivity.this, "Order successfully.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    // Redirect to CheckOutActivity and pass the AddressID
                    //Intent intent = new Intent(LocationActivity.this, CheckOutActivity.class);
                    //intent.putExtra("AddressID", addressId);
                    //startActivity(intent);
                })
                .addOnFailureListener(e -> Log.w("OrderAdd", "Error adding address", e));

        // Do something with the selected values
    }

    private void signInAnonmously(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signInAnonymously", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user != null ? user.getUid() : null;
                            Log.d("signInAnonymously", "Signed in anonymously with UID: " + userId);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signInAnonymously", "signInAnonymously:failure", task.getException());
                            Toast.makeText(CheckOutActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
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

    private void fetchAllProduct(){
        db.collection("AllProducts")
                .limit(5) // Limit to 3 products
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Product> products = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Assuming you have a Product class to hold product data
                                Product product = new Product(
                                        document.getString("img_url"),
                                        document.getString("name"),
                                        document.getDouble("price"));
                                total += product.getPrice();
                                products.add(product);
                            }
                            if(orderList == null){
                                orderList = products;
                                setUp();

                            }
                            // Do something with the list of products (e.g., update UI)
                            //displayProducts(products, total);
                        } else {
                            // Handle the error
                            Log.w("MainActivity", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void fetchUserAddress() {
        db.collection("users").document(currentUser.getUid())
                .collection("addresses").document(addressId)
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
                                address.setAddressId(addressId);
                                userPhone.setText(document.getString("phone"));
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
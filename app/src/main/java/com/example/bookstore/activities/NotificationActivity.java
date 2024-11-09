package com.example.bookstore.activities;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookstore.MapsFragment;
import com.example.bookstore.PermissionUtils;
import com.example.bookstore.ProductAdapter;
import com.example.bookstore.R;
import com.example.bookstore.RegisterActivity;
import com.example.bookstore.ResetPasswordActivity;
import com.example.bookstore.adapters.MyCartAdapter;
import com.example.bookstore.adaptes.NotificationAdapter;
import com.example.bookstore.models.Address;
import com.example.bookstore.models.Cart;
import com.example.bookstore.models.CartItem;
import com.example.bookstore.models.MyCartModel;
import com.example.bookstore.models.Notification;
import com.example.bookstore.models.Order;
import com.example.bookstore.models.Product;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "NotificationActivity";
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private FirebaseFirestore db;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        // Thiết lập Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications"); // Thiết lập tiêu đề cho toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiện nút quay lại

        // Thiết lập sự kiện cho nút quay lại
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
            startActivity(intent); // Chuyển đến MainActivity
            finish(); // Kết thúc NotificationActivity
        });
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        db = FirebaseFirestore.getInstance();
        loadNotifications();
    }

    private void loadNotifications() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "TV1xyM399EgkXAHanQPoyIzcLnA3";

        db.collection("Notifications").document(userId).collection("UserNotifications")
                .orderBy("date")  // Sắp xếp theo thời gian dạng `String`
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notificationList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            notificationList.add(notification);
                        }
                        notificationAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting notifications", task.getException());
                    }
                });
    }

    public static class BankingActivity extends AppCompatActivity {

        Button btnReturn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.banking);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            btnReturn = findViewById(R.id.btn__return);
            btnReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BankingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public static class CartActivity extends AppCompatActivity {
        int overAllTotalAmount;
        TextView overAllAmount;
        Toolbar toolbar;
        RecyclerView recyclerView;
        List<MyCartModel> cartModelList;
        MyCartAdapter cartAdapter;
        private FirebaseAuth auth;
        private FirebaseFirestore firestore;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cart);

            auth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
           // toolbar = findViewById(R.id.my_cart_toolbar);
            setSupportActionBar(toolbar);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            //get data from my cart adapter
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("MyTotalAmount"));

            recyclerView = findViewById(R.id.cart_rec);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            cartModelList = new ArrayList<>();
            cartAdapter = new MyCartAdapter(this,cartModelList);
            recyclerView.setAdapter(cartAdapter);
            overAllAmount = findViewById(R.id.tv_total);

            firestore.collection("Cart").document(auth.getCurrentUser().getUid())
                    .collection("CartItem").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (DocumentSnapshot doc: task.getResult().getDocuments()){
                                    MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                    cartModelList.add(myCartModel);
                                    cartAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }

        public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int totalBill = intent.getIntExtra("totalAmount",0);
                overAllAmount.setText("Total Amount: " +totalBill +"$");
            }
        };

        public void CheckOut(View view) {
            Intent intent = new Intent(CartActivity.this, CheckOutActivity.class);
            startActivity(intent);
        }

        public void deleteItem(String cartId) {
            // Get the current user's cart collection reference
            firestore.collection("Cart")
                    .document(auth.getCurrentUser().getUid())
                    .collection("CartItem")
                    .document(cartId)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Remove from local list and notify the adapter
                                for (int i = 0; i < cartModelList.size(); i++) {
                                    if (cartModelList.get(i).getCartId().equals(cartId)) {
                                        cartModelList.remove(i);
                                        cartAdapter.notifyItemRemoved(i);
                                        break; // Exit loop after removing
                                    }
                                }
                            } else {
                                // Handle error
                                Log.w("Firestore", "Error deleting document", task.getException());
                            }
                        }
                    });
        }

    }

    public static class CheckOutActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
        private TextView userAddressTextView, totalPrice, userPhone, userName;
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

            cartId = getIntent().getStringExtra("cartId");
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String cartID = sharedPreferences.getString("cartId", null); // null is the default value if "cartId" is not found
            addressId = addressId = getIntent().getStringExtra("AddressID");;

            // Initialize views
            userAddressTextView = findViewById(R.id.userAddress);
            userPhone = findViewById(R.id.userPhone);
            userName = findViewById(R.id.userName);
            // Fetch user data
            if(addressId != null){
                fetchUserAddress();
            }else{
                fetchFirstUserAddress();
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
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("cartId"); // Replace "cartId" with the specific key you want to remove
                        editor.apply();
                        cartId = null;
                        Toast.makeText(CheckOutActivity.this, "Order successfully.",
                                Toast.LENGTH_SHORT).show();
                        if(selectedRadioButton.getText().toString().equals("Online Banking")){
                            Intent intent = new Intent(CheckOutActivity.this, BankingActivity.class);
                            startActivity(intent);
                        }else {
                        Intent intent = new Intent(CheckOutActivity.this, MainActivity.class);
                        startActivity(intent);}
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

        private void fetchFirstUserAddress(){
            db.collection("users").document(currentUser.getUid())
                    .collection("addresses")
                    .limit(1)  // Limit the query to fetch only the first document
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0); // Get the first document
                            Address address = document.toObject(Address.class);

                            if (address != null) {
                                // Display address data
                                String fullAddress = address.getStreet() + ", " + address.getWard() + ", "
                                        + address.getDistrict() + ", " + address.getProvince();
                                address.setAddressId(document.getId());

                                userPhone.setText(document.getString("phone"));
                                userName.setText(document.getString("name"));
                                userAddressTextView.setText(fullAddress);
                            }
                        } else {
                            Log.d("Firebase", "No address found for this user.");
                            userPhone.setText("No phone number found!");
                            userName.setText("No name found!");
                            userAddressTextView.setText("No address found!");
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
                                    userName.setText(document.getString("name"));
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

    public static class LocationActivity extends AppCompatActivity {
        private Button createNew;
        private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private boolean mLocationPermissionGranted;
        private AutocompleteSupportFragment autoComplete;

        // seting tinh thanh
        private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
        private RequestQueue requestQueue;
        private ArrayList<String> provinceList = new ArrayList<>();
        private ArrayList<String> districtList = new ArrayList<>();
        private ArrayList<String> wardList = new ArrayList<>();
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private String lat, lon;

        FirebaseFirestore firestore;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.choose_location);
            placeSelect();

            createNew = findViewById(R.id.btn_submit);
            createNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedProvince = spinnerProvince.getSelectedItem().toString();
                    String selectedDistrict = spinnerDistrict.getSelectedItem().toString();
                    String selectedWard = spinnerWard.getSelectedItem().toString();
                    TextView tvStreet = findViewById(R.id.tv_street);
                    TextView tvName = findViewById(R.id.tv_yourname);
                    TextView tvPhone = findViewById(R.id.tv_phone);
                    String enteredStreet = tvStreet.getText().toString();
                    String enteredName = tvName.getText().toString();
                    String enteredPhone = tvPhone.getText().toString();

                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        // Save an address for a user
                        Map<String, Object> address = new HashMap<>();
                        address.put("name", enteredName);
                        address.put("phone", enteredPhone);
                        address.put("street", enteredStreet);
                        address.put("ward", selectedWard);
                        address.put("district", selectedDistrict);
                        address.put("province", selectedProvince);
                        address.put("country", "Việt Nam");
                        address.put("latitude", lat);
                        address.put("longitude", lon);

                        db.collection("users").document(currentUser.getUid())
                                .collection("addresses")
                                .add(address)
                                .addOnSuccessListener(documentReference -> {
                                    String addressId = documentReference.getId();
                                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("addressID", addressId);
                                    editor.apply();
                                    Log.d("Firestore", "Address added with ID: " + addressId);

                                    // Redirect to CheckOutActivity and pass the AddressID
                                    Intent intent = new Intent(LocationActivity.this, CheckOutActivity.class);
                                    intent.putExtra("AddressID", addressId);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> Log.w("Firestore", "Error adding address", e));

                        // Do something with the selected values
                        Log.d("SpinnerValues", "Province: " + selectedProvince + ", District: " + selectedDistrict + ", Ward: " + selectedWard);
                    }else {
                        Log.e("Firestore", "User is not signed in");
                    }
                }
            });


            Places.initialize(this,"[REDACTED]");
            autoComplete =(AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autoComplete);
            autoComplete.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG));
            autoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();

                    //mapFrag.zoomOnMap(latLng);
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.d("TAG", "Error" );
                }
            });

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.choose_location), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        protected void placeSelect(){
            spinnerProvince = findViewById(R.id.tinh_spinner);
            spinnerDistrict = findViewById(R.id.quan_spinner);
            spinnerWard = findViewById(R.id.phuong_spinner);

            requestQueue = Volley.newRequestQueue(this);

            loadProvinces();
        }

        private void loadProvinces() {
            String url = "https://esgoo.net/api-tinhthanh/1/0.htm";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("error") == 0) {
                            JSONArray data = response.getJSONArray("data");
                            provinceList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject province = data.getJSONObject(i);
                                provinceList.add(province.getString("full_name"));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_spinner_item, provinceList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerProvince.setAdapter(adapter);

                            spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // Load districts for the selected province
                                    loadDistricts(data.optJSONObject(position).optString("id"));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue.add(request);
        }

        private void loadDistricts(String provinceId) {
            String url = "https://esgoo.net/api-tinhthanh/2/" + provinceId + ".htm";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("error") == 0) {
                            JSONArray data = response.getJSONArray("data");
                            districtList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject district = data.getJSONObject(i);
                                districtList.add(district.getString("full_name"));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_spinner_item, districtList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerDistrict.setAdapter(adapter);

                            spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // Load wards for the selected district
                                    loadWards(data.optJSONObject(position).optString("id"));
                                    zoomIng(data, position);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) { }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LocationActivity.this, "Error loading districts", Toast.LENGTH_SHORT).show();
                }
            });

            requestQueue.add(request);
        }

        private void loadWards(String districtId) {
            String url = "https://esgoo.net/api-tinhthanh/3/" + districtId + ".htm";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("error") == 0) {
                            JSONArray data = response.getJSONArray("data");
                            wardList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject ward = data.getJSONObject(i);
                                wardList.add(ward.getString("full_name"));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_spinner_item, wardList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerWard.setAdapter(adapter);
                            spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    zoomIng(data, position);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) { }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LocationActivity.this, "Error loading wards", Toast.LENGTH_SHORT).show();
                }
            });

            requestQueue.add(request);
        }

        protected void zoomIng(JSONArray data, int position){
            // Load wards for the selected district
            double latitude = data.optJSONObject(position).optDouble("latitude", 0.0);
            double longitude = data.optJSONObject(position).optDouble("longitude", 0.0);
            lat = String.valueOf(latitude);
            lon = String.valueOf(longitude);
            if(latitude != 0 && longitude != 0){
                LatLng latLng = new LatLng(latitude, longitude);
                MapsFragment mapFrag = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                mapFrag.zoomOnMap(latLng);
            }
        }

        @Override
        protected void onResumeFragments() {
            super.onResumeFragments();

            // Retrieve MapsFragment instance
            MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Check if permission was denied
            if (mapsFragment != null && mapsFragment.isPermissionDenied()) {
                showMissingPermissionError();
            }
        }

        private void showMissingPermissionError() {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(true).show(getSupportFragmentManager(), "dialog");
        }
    }

    public static class LoginActivity extends AppCompatActivity {

        EditText email, password;
        private FirebaseAuth auth;
        Button googleSignin;

        private static final int RC_SIGN_IN = 9001;
        private GoogleSignInClient googleSignInClient;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            auth = FirebaseAuth.getInstance();


            email = findViewById(R.id.email);
            password = findViewById(R.id.password);
            googleSignin = findViewById(R.id.btn_google_signin);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))  // Use Web Client ID from Firebase
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(this, gso);

            TextView forgotPassword = findViewById(R.id.tv_forgot);
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                }
            });
        }

        public void signIn(View view) {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (!isValidEmail(userEmail)) {
                Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(userPassword)) {
                Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userPassword.length() < 6) {
                Toast.makeText(this, "Password too short, enter minimum 6 chareaters", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                // To clear all entries in SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear(); // This will remove all entries
                                editor.apply(); // Save the changes

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Please login again!" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        private boolean isValidEmail(String email) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        public void signUp(View view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }


        public void googleSigning(View view) {
            googlesignin();
        }

        private void googlesignin() {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Check if result is from Google Sign-In
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign-In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign-In failed
                    Log.w("LoginActivity", "Google sign-in failed", e);
                    Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign-in successful, navigate to the next activity
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // Sign-in failed
                            Log.w("LoginActivity", "Firebase sign-in failed", task.getException());
                            Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    });
        }

        private void updateUI(FirebaseUser user) {
            if (user != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please sign in to continue.", Toast.LENGTH_SHORT).show();
            }

        }
    }
}

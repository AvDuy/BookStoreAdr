package com.example.bookstore;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LocationActivity extends AppCompatActivity {
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
                TextView tvPhone = findViewById(R.id.tv_phone);
                String enteredStreet = tvStreet.getText().toString();
                String enteredPhone = tvPhone.getText().toString();

                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    // Save an address for a user
                    Map<String, Object> address = new HashMap<>();
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
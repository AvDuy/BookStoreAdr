package com.example.bookstore;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
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

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        placeSelect();

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

//        createNew.findViewById(R.id.add_location_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openPlacePicker();
//            }
//        });
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, provinceList);
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, districtList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDistrict.setAdapter(adapter);

                        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // Load wards for the selected district
                                loadWards(data.optJSONObject(position).optString("id"));
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
                Toast.makeText(MainActivity.this, "Error loading districts", Toast.LENGTH_SHORT).show();
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, wardList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerWard.setAdapter(adapter);
                        spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                // Load wards for the selected district
                                double latitude = data.optJSONObject(position).optDouble("latitude", 0.0);
                                double longitude = data.optJSONObject(position).optDouble("longitude", 0.0);
                                LatLng latLng = new LatLng(latitude, longitude);
                                MapsFragment mapFrag = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

                                mapFrag.zoomOnMap(latLng);
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
                Toast.makeText(MainActivity.this, "Error loading wards", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
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

    protected void pushFirebase() {
        firestore = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("First Name", "Khanh tre");
        user.put("Second Name", "Nguyen");
        user.put("Nickname", "NGu lon");

        firestore.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }
}
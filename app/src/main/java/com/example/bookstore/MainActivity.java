package com.example.bookstore;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    private ImageView ava;
private TextView user_name, user_email, user_phone, user_dob, user_gender;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        initUI();
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        // Load user info from Firestore
        showUserInfo();
    }

    private void initUI() {
        //ava = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.name);
        user_gender = findViewById(R.id.gender);
        user_dob = findViewById(R.id.birth);
        user_email = findViewById(R.id.email);
        user_phone = findViewById(R.id.phone);
    }

    private void showUserInfo() {
        DocumentReference docRef = firestore.collection("User_info").document("1");
        // Fetch data from Firestore
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Convert document to user_info object
                user_info userInfo = documentSnapshot.toObject(user_info.class);

                if (userInfo != null) {
                    user_name.setText(userInfo.getName());
                    user_gender.setText(userInfo.isGender() ? "Male" : "Female");
                    user_dob.setText(userInfo.getDob());
                    user_email.setText(userInfo.getEmail());
                    user_phone.setText(String.valueOf(userInfo.getPhone()));
                } else {
                    Log.d("MainActivity", "User info is null");
                }
            } else {
                Log.d("MainActivity", "No such document");
            }
        }).addOnFailureListener(e -> Log.w("MainActivity", "Error getting document", e));
    }
}
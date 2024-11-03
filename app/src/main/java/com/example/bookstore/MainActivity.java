package com.example.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth ;
    String userId;
    private ImageView ava;
    private Button changeProfile;
    private TextView user_name, user_email, user_phone, user_dob, user_gender;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        firestore = FirebaseFirestore.getInstance();
        initUI();
        showUserInfo();
        changeProfile = findViewById(R.id.btn_change_info);
        changeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdateProfile.class);
            startActivity(intent);
        });

        //mAuth = FirebaseAuth.getInstance();
        //userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    }

    private void initUI() {
        ava = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.uname);
        user_gender = findViewById(R.id.ugender);
        user_dob = findViewById(R.id.udob);
        user_email = findViewById(R.id.uemail);
        user_phone = findViewById(R.id.uphone);
    }

    private void showUserInfo() {
        //Test data
        DocumentReference docRef = firestore.collection("users").document("1");

        //auth = FirebaseAuth.getInstance();
        //userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        //DocumentReference docRef = firestore.collection("users").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                user_infoDAO userInfo = documentSnapshot.toObject(user_infoDAO.class);
                if (userInfo != null) {
                    ImageView profileImage = findViewById(R.id.profile_image);
                    Picasso.get()
                            .load(userInfo.getAvatar())
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_avatardefault)
                            .into(profileImage);
                    user_name.setText(userInfo.getName());
                    user_gender.setText(userInfo.isGender() ? "Male" : "Female");
                    user_dob.setText(userInfo.getDob());
                    user_email.setText(userInfo.getEmail());
                    user_phone.setText(userInfo.getPhone());
                } else {
                    Log.d("MainActivity", "User info is null");
                }
            } else {
                Log.d("MainActivity", "No such document");
            }
        }).addOnFailureListener(e -> Log.w("MainActivity", "Error getting document", e));
    }
}

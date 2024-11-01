package com.example.bookstore;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
private ImageView ava;
private TextView name;
private TextView gender;
private TextView dob;
private TextView email;
private TextView phone;
val db = Firebase.firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initUI();

    }
    private void initUI() {
        ava = findViewById(R.id.profile_image);
        name = findViewById(R.id.uname);
        gender = findViewById(R.id.ugender);
        dob = findViewById(R.id.udob);
        email = findViewById(R.id.uemail);
        phone = findViewById(R.id.uphone);
    }

    private void showUserInfo() {
        FirebaseUser user = FirebaseAuth.
    }
}
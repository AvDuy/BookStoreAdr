package com.example.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookstore.activities.OrderHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class User_menu extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ImageView avatarImageView;
    private TextView usernameTextView;
    private Button infoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_menu);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        avatarImageView = findViewById(R.id.ava);
        usernameTextView = findViewById(R.id.username);
        infoButton = findViewById(R.id.info);


        loadUserInfo();

        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(User_menu.this, UserProfile.class);
            startActivity(intent);
        });
    }

    private void loadUserInfo() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DocumentReference docRef = firestore.collection("users").document(userId);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("Name");
                    String avatarUrl = documentSnapshot.getString("Avatar");

                    usernameTextView.setText(name);

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Picasso.get()
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_avatardefault)
                                .into(avatarImageView);
                    }
                } else {
                    Log.d("User_menu", "No such document");
                }
            }).addOnFailureListener(e -> Log.w("User_menu", "Error getting document", e));
        } else {
            Log.w("User_menu", "User not logged in");
        }
    }

    public void history(View view) {
        Intent intent = new Intent(User_menu.this, OrderHistoryActivity.class);
        startActivity(intent);
    }
}

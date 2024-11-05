package com.example.bookstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookstore.R;
import com.example.bookstore.adaptes.NotificationAdapter;
import com.example.bookstore.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

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

}

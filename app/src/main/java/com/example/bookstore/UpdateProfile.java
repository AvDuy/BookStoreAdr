package com.example.bookstore;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
public class UpdateProfile extends  AppCompatActivity{
    private EditText edtName, edtGender, edtDob, edtEmail, edtPhone;
    private Button saveButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_userprofile);

        // Khởi tạo các thành phần
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        edtName = findViewById(R.id.edt_name);
        edtGender = findViewById(R.id.edt_gender);
        edtDob = findViewById(R.id.edt_dob);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone);
        saveButton = findViewById(R.id.save_button);

        // Hiển thị thông tin người dùng hiện tại
        showUserInfo();

        // Xử lý nút "Save" để cập nhật thông tin
        saveButton.setOnClickListener(v -> updateUserInfo());
    }

    private void showUserInfo() {
        DocumentReference docRef = firestore.collection("users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                user_infoDAO userInfo = documentSnapshot.toObject(user_infoDAO.class);
                if (userInfo != null) {
                    edtName.setText(userInfo.getName());
                    edtGender.setText(userInfo.isGender() ? "Male" : "Female");
                    edtDob.setText(userInfo.getDob());
                    edtEmail.setText(userInfo.getEmail());
                    edtPhone.setText(userInfo.getPhone());
                }
            }
        }).addOnFailureListener(e -> Log.w("UpdateProfileActivity", "Error getting document", e));
    }

    private void updateUserInfo() {
        String name = edtName.getText().toString().trim();
        boolean gender = edtGender.getText().toString().equalsIgnoreCase("Male");
        String dob = edtDob.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        DocumentReference docRef = firestore.collection("users").document(userId);
        docRef.update(
                "name", name,
                "gender", gender,
                "dob", dob,
                "email", email,
                "phone", phone
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();  // Quay lại MainActivity sau khi cập nhật thành công
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            Log.w("UpdateProfileActivity", "Error updating document", e);
        });
    }
}


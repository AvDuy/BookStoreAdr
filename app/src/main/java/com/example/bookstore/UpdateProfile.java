package com.example.bookstore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProfile extends AppCompatActivity {
    private EditText edtName, edtGender, edtDob, edtEmail, edtPhone;
    private Button saveButton, chooseImageButton;
    private ImageView profileImageView;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> getContentLauncher;
    private FirebaseAuth auth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_userprofile);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        edtName = findViewById(R.id.edt_name);
        edtGender = findViewById(R.id.edt_gender);
        edtDob = findViewById(R.id.edt_dob);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone);
        saveButton = findViewById(R.id.save_button);
        chooseImageButton = findViewById(R.id.btn_choose_image);
        profileImageView = findViewById(R.id.profile_image);
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if (imageUri != null) {
                            profileImageView.setImageURI(imageUri);
                        }
                    }
                }
        );

        showUserInfo();
        saveButton.setOnClickListener(v -> updateUserInfo());
        chooseImageButton.setOnClickListener(v -> chooseImage());
    }

    private void showUserInfo() {
        if (userId != null) {
            DocumentReference docRef = firestore.collection("users").document(userId); // Sử dụng userId

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    user_infoDAO userInfo = documentSnapshot.toObject(user_infoDAO.class);
                    if (userInfo != null) {
                        edtName.setText(userInfo.getName());
                        edtGender.setText(userInfo.isGender() ? "Male" : "Female");
                        edtDob.setText(userInfo.getDob());
                        edtEmail.setText(userInfo.getEmail());
                        edtPhone.setText(userInfo.getPhone());
                        // Tải hình ảnh vào ImageView nếu có URL
                        if (userInfo.getAvatar() != null) {
                            Glide.with(this)
                                    .load(userInfo.getAvatar())
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_avatardefault)
                                    .into(profileImageView);
                        }
                    }
                }
            }).addOnFailureListener(e -> Log.w("UpdateProfileActivity", "Error getting document", e));
        } else {
            Log.d("UpdateProfileActivity", "User is not authenticated");
        }
    }

    private void updateUserInfo() {
        String name = edtName.getText().toString().trim();
        boolean gender = edtGender.getText().toString().equalsIgnoreCase("Male");
        String dob = edtDob.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Nếu có hình ảnh được chọn, tải hình ảnh lên Firebase
        if (imageUri != null) {
            final String fileName = "profile_" + System.currentTimeMillis();
            StorageReference fileReference = storageReference.child(fileName);
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String avatarUrl = uri.toString();
                        // Cập nhật thông tin người dùng bao gồm cả URL hình ảnh
                        if (userId != null) {
                            DocumentReference docRef = firestore.collection("users").document(userId); // Sử dụng userId
                            docRef.update(
                                    "Name", name,
                                    "Gender", gender,
                                    "Date of birth", dob,
                                    "Email", email,
                                    "Phone", phone,
                                    "Avatar", avatarUrl // Cập nhật URL hình ảnh vào Firestore
                            ).addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                Log.w("UpdateProfileActivity", "Error updating document", e);
                            });
                        }
                    })).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                Log.w("UpdateProfileActivity", "Error uploading image", e);
            });
        } else {
            // Nếu không có hình ảnh, chỉ cập nhật thông tin không có Avatar
            if (userId != null) {
                DocumentReference docRef = firestore.collection("users").document(userId); // Sử dụng userId
                docRef.update(
                        "Name", name,
                        "Gender", gender,
                        "Date of birth", dob,
                        "Email", email,
                        "Phone", phone
                ).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    Log.w("UpdateProfileActivity", "Error updating document", e);
                });
            }
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getContentLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}

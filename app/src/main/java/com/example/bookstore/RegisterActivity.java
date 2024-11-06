package com.example.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookstore.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email,phone,password;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        /*if(auth.getCurrentUser() != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }*/
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);

    }

    public void signup(View view){
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this,"Enter Name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this,"Enter Email Address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPhone)){
            Toast.makeText(this,"Enter Phone Number!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"Enter Password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(userPassword.length()<6){
            Toast.makeText(this,"Password too short, enter minimum 6 chareaters",Toast.LENGTH_SHORT).show();
            return;
        }

       auth.createUserWithEmailAndPassword(userEmail,userPassword)
               .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){

                           saveUserDataToFirestore(userName,userEmail,userPhone);
                       }else{
                           Toast.makeText(RegisterActivity.this,"Register Failed" + task.getException(),Toast.LENGTH_SHORT).show();
                       }
                   }
               });

        //startActivity(new Intent(RegisterActivity.this,MainActivity.class));
    }

    private void saveUserDataToFirestore(String userName, String userEmail, String userPhone) {
        String userID = auth.getCurrentUser().getUid();

        Map<String,Object> userMap = new HashMap<>();
        userMap.put("Name",userName);
        userMap.put("Email",userEmail);
        userMap.put("Phone",userPhone);

        firestore.collection("users").document(userID)
                .set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        }else {
                            Toast.makeText(RegisterActivity.this, "Error saving user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void signin(View view){
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
}
package com.example.bookstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookstore.CheckOutActivity;
import com.example.bookstore.LoginActivity;
import com.example.bookstore.R;
import com.example.bookstore.RegisterActivity;
import com.example.bookstore.User_menu; // Nếu bạn có lớp này
import com.example.bookstore.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Fragment homeFragment;

    Toolbar toolbar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator (R.drawable.ic_launcher_foreground);


        homeFragment = new HomeFragment();
        loadFragment(homeFragment);



    }


    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container,homeFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else if (id == R.id.menu_my_cart) {
            startActivity(new Intent( MainActivity.this, CheckOutActivity.class));
            return true;
        }else if (id == R.id.thongbao) {
            openNotifications();
            return true;
        }else if (id == R.id.menu) {
            startActivity(new Intent( MainActivity.this, User_menu.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void openNotifications() {
        // Mở thông báo
        Intent intent = new Intent(this, NotificationActivity.class); // Thay NotificationActivity bằng hoạt động thông báo của bạn
        startActivity(intent);
    }
}
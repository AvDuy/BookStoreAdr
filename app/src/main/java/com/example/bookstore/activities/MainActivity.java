package com.example.bookstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.bookstore.R;
import com.example.bookstore.User_menu; // Nếu bạn có lớp này
import com.example.bookstore.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Fragment homeFragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Thiết lập toolbar
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_foreground);

        // Tải fragment chính
        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Nạp menu vào toolbar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Xử lý sự kiện nhấp vào các mục menu
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            logoutUser();
            return true;
        } else if (id == R.id.menu_my_cart) {
            openMyCart();
            return true;
        } else if (id == R.id.thongbao) {
            openNotifications();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void logoutUser() {

    }

    private void openMyCart() {

    }

    private void openNotifications() {
        // Mở thông báo
        Intent intent = new Intent(this, NotificationActivity.class); // Thay NotificationActivity bằng hoạt động thông báo của bạn
        startActivity(intent);
    }
}

package com.example.bookstore.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bookstore.adaptes.OrderPagerAdapter;
import com.example.bookstore.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.FirebaseApp;

public class OrderHistoryActivity extends AppCompatActivity {

    private ViewPager2 viewPagerOrderStatus;
    private TabLayout tabLayoutOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_history);

//        Toolbar toolbar = findViewById(R.id.tb1);
//        setSupportActionBar(toolbar);
//
//        ImageView backArrow = findViewById(R.id.backArrow);
//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                 // Go back to the previous screen
//            }
//        });
        // Liên kết các view với ID trong layout XML
        viewPagerOrderStatus = findViewById(R.id.viewPagerOrderStatus);
        tabLayoutOrderStatus = findViewById(R.id.tabLayoutOrderStatus);

        // Tạo adapter cho ViewPager2
        OrderPagerAdapter adapter = new OrderPagerAdapter(this);
        viewPagerOrderStatus.setAdapter(adapter);

        // Thiết lập TabLayoutMediator để đồng bộ TabLayout và ViewPager2
        new TabLayoutMediator(tabLayoutOrderStatus, viewPagerOrderStatus,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("ALL");
                                break;
                            case 1:
                                tab.setText("Process");
                                break;
                            case 2:
                                tab.setText("Confirm");
                                break;
                            case 3:
                                tab.setText("Completed");
                                break;
                            case 4:
                                tab.setText("Canceled");
                                break;
                        }
                    }
                }).attach(); // Đảm bảo rằng `attach()` được gọi để kích hoạt TabLayoutMediator
    }
}

package com.example.bookstore;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class homepageActivity extends AppCompatActivity{

    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        Anhxa();
        ActionBar();
        ActionViewFilpper();
    }
    private void ActionViewFilpper() {
        ArrayList<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://vietgigs.vn/public/storage/gigs/gallery/large/90054AE20DD9E8EFE72C.webp");
        mangquangcao.add("https://sachsuthattphcm.com.vn/wp-content/uploads/2023/07/chantrang.jpg");
        mangquangcao.add("https://thietkelogo.edu.vn/uploads/images/thiet-ke-do-hoa-khac/banner-sach/1.png");
        mangquangcao.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcShGIzA-fGaVR3MczFVGn-3MvfrcjqHGB_X4g&s");
        mangquangcao.add("https://file.hstatic.net/1000290493/collection/banner_trang_chu-01_717ce87a0f8340a1a43fa0920a876bea.png");
        for (int i=0;i<mangquangcao.size();i++) {
            ImageView imageView = new ImageView (getApplicationContext());
            Glide.with(getApplicationContext()).load (mangquangcao.get(i)).into(imageView);
            imageView.setScaleType (ImageView. ScaleType.FIT_XY);
            viewFlipper.addView(imageView);

        }
        viewFlipper.setFlipInterval (3000);
        viewFlipper.setAutoStart (true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar (toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Anhxa() {
        toolbar = findViewById(R.id.toobarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        navigationView = findViewById(R.id.navigationview);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        drawerLayout = findViewById(R.id.drawerlayout);
    }
}


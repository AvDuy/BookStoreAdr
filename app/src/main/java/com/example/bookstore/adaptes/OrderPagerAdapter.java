package com.example.bookstore.adaptes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bookstore.fragments.OrderHistoryFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OrderHistoryFragment.newInstance(position); // Sử dụng phương thức static để khởi tạo fragment
    }

    @Override
    public int getItemCount() {
        return 5; // Số lượng tab
    }
}

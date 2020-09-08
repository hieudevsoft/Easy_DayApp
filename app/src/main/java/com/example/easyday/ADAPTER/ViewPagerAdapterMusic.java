package com.example.easyday.ADAPTER;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPagerAdapterMusic extends FragmentPagerAdapter {
    List<Fragment> fragment;

    public ViewPagerAdapterMusic(@NonNull FragmentManager fm, List<Fragment> fragment) {
        super(fm);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragment.get(position);
    }

    @Override
    public int getCount() {
        return fragment.size();
    }
}

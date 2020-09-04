package com.example.easyday.ACTIVITY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;


import com.example.easyday.ADAPTER.ScreenSliderFragment;
import com.example.easyday.CONTROL.DepthPageTransformer;
import com.example.easyday.FRAGMENT.MeFragment;
import com.example.easyday.R;
import com.example.easyday.CONTROL.TOOL;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeScreen extends AppCompatActivity implements MeFragment.OnClickListener {
    ViewPager2 viewPager2;
    BottomNavigationView bottomNavigationView;
    RelativeLayout relativeLayout;
    List<Fragment> listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_home_screen);
        mapping();
        settingsViewPager2();
        settingsNavBottom();
        updateBottomNav();
    }

    private void mapping()
    {
        viewPager2 = findViewById(R.id.viewPager2);
        bottomNavigationView = findViewById(R.id.bottomNav);
        relativeLayout = findViewById(R.id.main_layout_home);
    }

    private void settingsViewPager2()
    {
        listFragment = TOOL.makeListFragment();
        ScreenSliderFragment screenSliderFragment = new ScreenSliderFragment(this,listFragment);
        viewPager2.setAdapter(screenSliderFragment);
        viewPager2.setPageTransformer(new DepthPageTransformer());
    }

    private void settingsNavBottom()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_themes:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.menu_home:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.menu_me:
                        viewPager2.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(viewPager2.getCurrentItem()==0)
        {

        }
        else
        {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()-1);
        }
    }
    private void setSelectedItemBottom(int position)
    {
        switch (position)
        {
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.menu_themes);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.menu_home);
                break;
            case 2:
                bottomNavigationView.setSelectedItemId(R.id.menu_me);
                break;
        }
    }

    private void updateBottomNav()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSelectedItemBottom(viewPager2.getCurrentItem());
                    }
                });
            }
        }, 100, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(Fragment fragment : getSupportFragmentManager().getFragments())
        {
            fragment.onActivityResult(requestCode, resultCode,data);
        }
    }

    @Override
    public void onClickImageButton(int number) {
            setSelectedItemBottom(number);
            viewPager2.setCurrentItem(number);
    }
}
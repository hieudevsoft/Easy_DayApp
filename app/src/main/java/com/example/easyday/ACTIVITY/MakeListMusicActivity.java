package com.example.easyday.ACTIVITY;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;

public class MakeListMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_make_list_music);
        getSharedPreferences("POSITION_SONG", MODE_PRIVATE).edit().putBoolean("onCreate", true).apply();
        Log.d("PlayMusicFragment","backPressOnCreateActivity: " + getSharedPreferences("POSITION_SONG",MODE_PRIVATE).getBoolean("backPress", false)+"");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setUpControlMusicContinue();
        finish();
    }
    public void setUpControlMusicContinue()
    {
        getSharedPreferences("POSITION_SONG", MODE_PRIVATE).edit().putBoolean("backPress", true).apply();
        getSharedPreferences("POSITION_SONG", MODE_PRIVATE).edit().putBoolean("onCreate", false).apply();
    }
}
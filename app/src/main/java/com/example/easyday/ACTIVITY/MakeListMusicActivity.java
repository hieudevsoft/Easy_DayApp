package com.example.easyday.ACTIVITY;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;

public class MakeListMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_make_list_music);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
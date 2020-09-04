package com.example.easyday.ACTIVITY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.FRAGMENT.MapFragment;
import com.example.easyday.R;

public class MapActivity extends AppCompatActivity {
    MapFragment mapFragment;
    SearchView place;
    Button bt_Findpath;
    RelativeLayout layout_InforPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_map);
        mapping();
        openMap();
    }

    private void mapping()
    {
        place = findViewById(R.id.search_place);
        bt_Findpath = findViewById(R.id.bt_FindPath);
        layout_InforPath = findViewById(R.id.layout_Inforpath);
    }

    private void openMap()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.fragment_Map);
        mapFragment.setSearchView(place);
        mapFragment.setBt_FindPath(bt_Findpath);
        mapFragment.setSearchLocation(layout_InforPath);
        mapFragment.setFindPath(layout_InforPath);

    }
}
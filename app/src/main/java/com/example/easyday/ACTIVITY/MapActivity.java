package com.example.easyday.ACTIVITY;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.FRAGMENT.home.MapFragment;
import com.example.easyday.R;

public class MapActivity extends AppCompatActivity {
    MapFragment mapFragment;
    SearchView place;
    Button bt_Findpath;
    RelativeLayout layout_InforPath;
    TextView tv_destination,tv_distance,tv_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOOL.FULL_SCREEN(this);
        setContentView(R.layout.activity_map);
        mapping();
    }

    private void mapping()
    {
        place = findViewById(R.id.search_place);
        bt_Findpath = findViewById(R.id.bt_FindPath);
        layout_InforPath = findViewById(R.id.layout_Inforpath);
        tv_destination = findViewById(R.id.tv_Destination);
        tv_time = findViewById(R.id.tv_time);
        tv_distance = findViewById(R.id.text_way);
    }

    @Override
    protected void onResume() {
        checkGPSDevice();
        Log.d("MapFragment","Resume  ...." + "GPS: " + getCheckGPSDevice());
        if(getCheckGPSDevice()){
            Log.d("MapFragment","Map on ....");
            openMap();
        }
        super.onResume();
    }

    private void openMap()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.fragment_Map);
        if(mapFragment!=null) {
            {
            mapFragment.getStarted();
            mapFragment.setSearchView(place);
            mapFragment.setBt_FindPath(bt_Findpath);
            mapFragment.setSearchLocation(layout_InforPath);
            mapFragment.setFindPath(layout_InforPath);
            }
        }else TOOL.setToast(this, "Không thể tạo Google Map");
    }

    private void checkGPSDevice()
    {
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(this)
                    .setMessage("Bật định vị GPS cho thiết bị")
                    .setPositiveButton("Bật", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            getApplicationContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Không", null).show();
        }
    }

    private boolean getCheckGPSDevice()
    {
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}
        Log.d("MapFragment","GPS : " + gps_enabled + " networkEnnabled: " + network_enabled );
        if(gps_enabled&&network_enabled) return true;
        else return false;
    }
}
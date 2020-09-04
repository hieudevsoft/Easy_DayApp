package com.example.easyday.FRAGMENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    private GoogleMap map;
    SearchView searchView;
    private String locationCurrent;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<LatLng> listPoint;
    LatLng location_search;
    MarkerOptions markerOptions;
    Button bt_FindPath;
    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    public MapFragment() {
        this.listPoint = new ArrayList<>();
        this.markerOptions = new MarkerOptions();
        this.location_search=null;
        this.getMapAsync(this);
    }

    public void setBt_FindPath(Button bt_FindPath) {
        this.bt_FindPath = bt_FindPath;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        getCurrentLocation();
    }

    public void setFindPath(final RelativeLayout inforPath)
    {
        bt_FindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_FindPath.setVisibility(View.GONE);
                inforPath.setVisibility(View.VISIBLE);
                inforPath.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_infor_path_enter));
            }
        });
    }

    public void setSearchLocation(final RelativeLayout inforPath) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bt_FindPath.setVisibility(View.VISIBLE);
                    }
                }, 1200);
                inforPath.setVisibility(View.GONE);
                inforPath.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_infor_path_exit));
                if(listPoint.size()==2) {
                      map.clear();
                    listPoint.remove(1);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current));
                    map.addMarker(markerOptions.position(listPoint.get(0)).title(locationCurrent));
                }
                String location = searchView.getQuery().toString();
                ArrayList<Address> list = null;
                if (location != null && location.length() != 0) {
                    Geocoder geocoder = new Geocoder(requireContext());
                    try {
                        list = (ArrayList<Address>) geocoder.getFromLocationName(location, 1);
                        if(list.size()==0) TOOL.setToast(getContext(), "Address not found!");
                        else {
                            Address address = list.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            listPoint.add(latLng);
                            location_search = latLng;
                            markerOptions.title(location);
                            markerOptions.position(latLng);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                            map.addMarker(markerOptions);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                return;
        } showMyLocation();
    }
    @SuppressLint("MissingPermission")
    private void showMyLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null)
                {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    try {
                        List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Address address = list.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            listPoint.add(latLng);
                            locationCurrent = address.getAddressLine(0);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current));
                            markerOptions.title(locationCurrent);
                            markerOptions.position(latLng);
                            map.addMarker(markerOptions);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==44 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            showMyLocation();
        }
    }
}

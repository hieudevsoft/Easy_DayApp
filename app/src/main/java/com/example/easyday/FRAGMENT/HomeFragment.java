package com.example.easyday.FRAGMENT;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.easyday.ACTIVITY.MapActivity;
import com.example.easyday.ACTIVITY.ToDoListActivity;
import com.example.easyday.CLASS.FlashLightClass;
import com.example.easyday.CONTROL.SendTheme;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    ImageView imageViewTheme;
    ToggleButton buttonFlashLight;
    final int REQUEST_CODE_FLASHLIGHT = 10;
    final static String urlWebserviceInsert = "https://easayday.000webhostapp.com/insertDataTheme.php";
    final static String urlWebserviceGetData = "https://easayday.000webhostapp.com/getDataTheme.php";
    final static String urlWebserviceUpdate = "https://easayday.000webhostapp.com/updateDataTheme.php";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageViewTheme = view.findViewById(R.id.image_Theme_Home);
        buttonFlashLight = view.findViewById(R.id.bt_flashlight);
        getDataFromServer(urlWebserviceGetData);
        SendTheme viewModel = ViewModelProviders.of(requireActivity()).get(SendTheme.class);
        viewModel.getUri().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String uri) {
                updateData(FirebaseAuth.getInstance().getCurrentUser().getUid().concat("theme"), uri, urlWebserviceUpdate, getContext());
                imageViewTheme.setImageBitmap(TOOL.convertStringToBitmap(uri));
            }
        });
        settingsRequestPermissionFlashLight();
        view.findViewById(R.id.bt_addNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ToDoListActivity.class) );
            }
        });
        view.findViewById(R.id.bt_Map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), MapActivity.class) );
            }
        });
        return view;
    }
    private void setFlashLight() {
        buttonFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonFlashLight.isChecked()) {
                    buttonFlashLight.setTextOn("");
                    new FlashLightClass(getContext()).turnFlashLightOff();
                } else {
                    buttonFlashLight.setTextOff("");
                    new FlashLightClass(getContext()).turnFlashLightOn();
                }
            }
        });
    }

    private void settingsRequestPermissionFlashLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
                        Manifest.permission.CAMERA)) {
                    Toast.makeText(getContext(), "Please access Camera", Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(
                            (Activity) getContext(), new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_FLASHLIGHT);
                }
            }
        }
        {
            setFlashLight();
        }
    }
    private void insertData(final String id, final String urlTheme, String url)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("success"))
                    Toast.makeText(getContext(), "insert done", Toast.LENGTH_LONG).show();
                else Toast.makeText(getContext(), "insert failure" , Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("idThemeUser",id);
                map.put("urlThemeUser", urlTheme);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getDataFromServer(final String url)
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                boolean checkID = false;
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid().concat("theme");
                for(int i = 0; i < response.length();i++)
                {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if(jsonObject.getString("ID").equals(id))
                        {
                            checkID = true;
                            String temp =jsonObject.getString("URLTHEME");
                            if(temp.contains("https://"))
                                Glide.with(getContext()).load(temp).centerCrop().into(imageViewTheme);
                                else
                            imageViewTheme.setImageBitmap(TOOL.convertStringToBitmap(temp));
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(!checkID) insertData(id, "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Black_colour.jpg/1200px-Black_colour.jpg", urlWebserviceInsert);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Disconnect to Server", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private static void updateData(final String id, final String urlTheme,String url,Context context)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("success"))
                    Log.d("TAG","Done update~");
                else Log.d("TAG","Failure update~");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("idThemeUser",id);
                map.put("urlThemeUser", urlTheme);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FLASHLIGHT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setFlashLight();
                }
                else Toast.makeText(getContext(), "Please access Camera", Toast.LENGTH_LONG).show();
                    break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
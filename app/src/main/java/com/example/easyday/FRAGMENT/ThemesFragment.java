package com.example.easyday.FRAGMENT;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.easyday.ADAPTER.ThemeAdpater;
import com.example.easyday.CONTROL.SendTheme;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class ThemesFragment extends Fragment implements LifecycleOwner {
    static List<String> listThemes = new ArrayList<>();
    FloatingActionButton floatingButtonAdd;
    GridView gridView;
    static ThemeAdpater themeAdapter;
    final int PICK_IMAGE = 111;
    private SendTheme viewModel;
    private static FirebaseUser user;
    private DatabaseReference db;
    final static String urlWebserviceGetData = "https://easayday.000webhostapp.com/getDataTheme.php";
    final static String urlWebserviceInsert = "https://easayday.000webhostapp.com/insertDataTheme.php";
    final static String urlWebserviceUpdate = "https://easayday.000webhostapp.com/updateDataTheme.php";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_themes, container, false);
        gridView = view.findViewById(R.id.GridView_themes);
        floatingButtonAdd = view.findViewById(R.id.floatingActionButton);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("Themes").child(user.getUid());
        themeAdapter = new ThemeAdpater(listThemes, getContext(),getActivity());
        gridView.setAdapter(themeAdapter);
        getDataFromServer(urlWebserviceGetData);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Long Click", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        clickButton();
        super.onResume();
    }
    private void clickButton() {
        floatingButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null)
                Toast.makeText(getActivity(), "Theme error!!", Toast.LENGTH_LONG).show();
            else {
                Uri uriImg = data.getData();
                CropImage.activity(uriImg).start(getContext(), this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                StringBuilder stringBuilder = new StringBuilder();
                final Uri resultUri = result.getUri();
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    listThemes.add(TOOL.convertBitMapToString(bitmap));
                    for(String uri:listThemes)
                        stringBuilder.append(uri+",");
                    updateData(user.getUid(), stringBuilder.toString(), urlWebserviceUpdate,getContext());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
    public static void setOnLongClickItem(final int position, final Context context) {
                final AlertDialog.Builder deleteTheme = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_DayNight_Dialog));
                deleteTheme.setTitle("Question?");
                deleteTheme.setMessage("Do you want remove theme?");
                deleteTheme.setIcon(R.drawable.ic_themes_me);
                deleteTheme.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        listThemes.remove(position);
                        for (String uri : listThemes)
                            stringBuilder.append(uri + ",");
                        updateData(user.getUid(), stringBuilder.toString(), urlWebserviceUpdate,context);
                        themeAdapter.notifyDataSetChanged();
                    }
                });
                deleteTheme.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                deleteTheme.show();
    }
    private void getDataFromServer(final String url)
    {
        listThemes.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                boolean checkID = false;
                for(int i = 0; i < response.length();i++)
                {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        if(jsonObject.getString("ID").equals(user.getUid()))
                        {
                            checkID = true;
                            String temp =jsonObject.getString("URLTHEME");
                            for(String uri:temp.split(","))
                                if(uri.length()>1)
                                listThemes.add(uri);
                            themeAdapter.notifyDataSetChanged();
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(!checkID) insertData(user.getUid(), "", urlWebserviceInsert);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Disconnect to Localhost", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private void insertData(final String id, final String urlTheme, String url)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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


}

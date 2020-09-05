package com.example.easyday.CONTROL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.easyday.ADAPTER.ThemeAdapter;
import com.example.easyday.ENTITY.Theme;
import com.example.easyday.R;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpersServiceThemes {
    private static String urlWebserviceGetData = "https://easayday.000webhostapp.com/getDataTheme.php";
    private static String urlWebserviceInsert = "https://easayday.000webhostapp.com/insertDataTheme.php";
    private static String urlWebserviceUpdate = "https://easayday.000webhostapp.com/updateDataTheme.php";

    public static String getUrlWebserviceGetData() {
        return urlWebserviceGetData;
    }

    public static String getUrlWebserviceInsert() {
        return urlWebserviceInsert;
    }

    public static String getUrlWebserviceUpdate() {
        return urlWebserviceUpdate;
    }

    public static void setUrlWebserviceUpdate(String urlWebserviceUpdate) {
        HelpersServiceThemes.urlWebserviceUpdate = urlWebserviceUpdate;
    }

    public static void getDataFromServer(final String url, final FirebaseUser user, final List<String> listThemes, final ThemeAdapter adapter, final Context context)
    {
        listThemes.clear();
        final List<String> tempList = new ArrayList<>();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
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
                                    tempList.add(uri);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(!checkID) insertData(user.getUid(), context,"", getUrlWebserviceInsert());
                listThemes.addAll(tempList);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Disconnect to Localhost", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private static void insertData(final String id, Context context, final String urlTheme, String url)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
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
    
    public static void updateData(final String id, final String urlTheme,String url,Context context)
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

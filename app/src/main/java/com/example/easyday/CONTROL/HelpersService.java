package com.example.easyday.CONTROL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.easyday.ADAPTER.RecyclerAdapterMainNote;
import com.example.easyday.ENTITY.ImageNote;
import com.example.easyday.ENTITY.Note;
import com.example.easyday.FRAGMENT.note.MainNoteFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HelpersService {
//    private static String urlInsertNoteFromService = "http://192.168.1.7:8080/EasyDayFile/insertDataNote.php";
//    private static String urlGetNoteFromService ="http://192.168.1.7:8080/EasyDayFile/getDataNote.php";
//    private static String urlUpdateDataFromService ="http://192.168.1.7:8080/EasyDayFile/updateDataNote.php";
//    private static String urlDeleteNoteFromService ="http://192.168.1.7:8080/EasyDayFile/deleteDataNote.php";
    private static String urlInsertNoteFromService = "https://easayday.000webhostapp.com/insertDataNote.php";
    private static String urlGetNoteFromService ="https://easayday.000webhostapp.com/getDataNote.php";
    private static String urlUpdateDataFromService ="https://easayday.000webhostapp.com/updateDataNote.php";
    private static String urlDeleteNoteFromService ="https://easayday.000webhostapp.com/deleteDataNote.php";
    public static String getUrlInsertNoteFromService() {
        return urlInsertNoteFromService;
    }

    public static void setUrlInsertNoteFromService(String urlInsertNoteFromService) {
        HelpersService.urlInsertNoteFromService = urlInsertNoteFromService;
    }

    public static String getUrlGetNoteFromService() {
        return urlGetNoteFromService;
    }

    public static void setUrlGetNoteFromService(String urlGetNoteFromService) {
        HelpersService.urlGetNoteFromService = urlGetNoteFromService;
    }

    public static String getUrlUpdateDataFromService() {
        return urlUpdateDataFromService;
    }

    public static void setUrlUpdateDataFromService(String urlUpdateDataFromService) {
        HelpersService.urlUpdateDataFromService = urlUpdateDataFromService;
    }

    public static String getUrlDeleteNoteFromService() {
        return urlDeleteNoteFromService;
    }

    public static void setUrlDeleteNoteFromService(String urlDeleteNoteFromService) {
        HelpersService.urlDeleteNoteFromService = urlDeleteNoteFromService;
    }

    public static void getNoteFromServiceByIds(final Context context, String url, final String idUser) {

        final List<Note> noteList = new ArrayList<>();
        Long timeNow = System.currentTimeMillis();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Bitmap imageNote = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) response.get(i);
                        if (jsonObject.getString("ID").contains(idUser)) {
                            String ID = jsonObject.getString("ID");
                            String description = jsonObject.getString("DESCRIPTION");
                            String idNoteServer = ID.substring(idUser.length());
                            boolean check = false;
                            for (Note note : noteList)
                                if (note.getIdNote().equals(ID.split("_")[0].concat("_"))) {
                                    check = true;
                                    imageNote = TOOL.convertStringToBitmap(jsonObject.getString("IMAGE"));
                                    try{
                                    note.getImgNotes().add(new ImageNote(imageNote, description));
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    finally {
                                        break;
                                    }
                                }
                            if (check) continue;
                            else {
                                try {
                                    String title = jsonObject.getString("TITLE");
                                    String content = jsonObject.getString("CONTENT");
                                    int level = Integer.parseInt(jsonObject.getString("LEVEL"));
                                    if(idNoteServer.split("_").length>1)
                                    {
                                        Note note = new Note(ID.split("_")[0].concat("_"),title,content,level);
                                        imageNote = TOOL.convertStringToBitmap(jsonObject.getString("IMAGE"));
                                        note.setImgNotes(new ArrayList<ImageNote>());
                                        note.getImgNotes().add(new ImageNote(imageNote,description));
                                        noteList.add(note);
                                    }else
                                    {
                                        Note note = new Note(ID.split("_")[0].concat("_"),title,content,level);
                                        note.setImgNotes(new ArrayList<ImageNote>());
                                        noteList.add(note);
                                    }
                                } catch (Exception e) {
                                    TOOL.setToast(context, "Failure in Note not in noteList");
                                }
                            }
                        } else continue;
                    } catch (JSONException e) {
                        TOOL.setToast(context, "Failure get object by id");
                    }
                }
                SendListNote sendListNote = ViewModelProviders.of((FragmentActivity) context).get(SendListNote.class);
                sendListNote.getListNote().postValue(noteList);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TOOL.setToast(context, "Loading Note....");
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    public static void checkIdNote(final Context context, String url, final String idUser, final String idNote, final EditText e) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) response.get(i);

                        if (jsonObject.getString("ID").contains(idUser)) {
                            String ID = jsonObject.getString("ID");
                            String idNoteServer = ID.substring(idUser.length());
                            if(idNoteServer.split("_")[0].equals(e.getText().toString().trim()))
                            {
                                e.setText("");
                                e.setError("ID NOTE already exists");
                                e.requestFocus();
                                SendCheckIdNote sendCheckIdNote = ViewModelProviders.of((FragmentActivity) context).get(SendCheckIdNote.class);
                                sendCheckIdNote.getCheckId().postValue(true);
                                return;
                            }
                        } else continue;

                    } catch (JSONException e) {
                        TOOL.setToast(context, "Failure get object by id");
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
        SendCheckIdNote sendCheckIdNote = ViewModelProviders.of((FragmentActivity) context).get(SendCheckIdNote.class);
        sendCheckIdNote.getCheckId().postValue(false);
    }
    public static void deleteImageNoteByPosition(final Context context,final String id)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, getUrlDeleteNoteFromService(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TOOL.setToast(context, "Disconnect to server");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("idNote", id);
                return map;
            }
        };
        requestQueue.add(request);
    }
}

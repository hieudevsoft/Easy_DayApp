package com.example.easyday.CONTROL;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpersService {
    public final static String urlInsertNoteFromService = "https://easayday.000webhostapp.com/insertDataNote.php";
    public final static String urlGetNoteFromService ="https://easayday.000webhostapp.com/getDataNote.php";
    public final static String urlUpdateDataFromService ="https://easayday.000webhostapp.com/updateDataNote.php";
    public final static String urlDeleteNoteFromService ="https://easayday.000webhostapp.com/deleteDataNote.php";

    public static void getNoteFromServiceByIds(final Context context, String url, final String idUser, final RecyclerAdapterMainNote recyclerAdapterMainNote, final List<Note> noteList,final List<Note> noteListTemp, final int sort) {
        noteList.clear();
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
                noteListTemp.addAll(noteList);
                if(sort>=0){
                    switch (sort)
                    {
                        case 0:
                            if(noteList.size()>0)
                            Collections.sort(noteList, new Comparator<Note>() {
                                @Override
                                public int compare(Note o1, Note o2) {
                                    if(o1.getLevel()>o2.getLevel())
                                        return 1;else
                                            if(o1.getLevel()<o2.getLevel())
                                                return -1;else
                                    return 0;
                                }
                            });
                            break;
                        case 1:
                            if(noteList.size()>0)
                            Collections.sort(noteList, new Comparator<Note>() {
                                @Override
                                public int compare(Note o1, Note o2) {
                                    if(o1.getLevel()>o2.getLevel())
                                        return -11;else
                                    if(o1.getLevel()<o2.getLevel())
                                        return 1;else
                                        return 0;
                                }
                            });
                            break;
                    }
                }

                recyclerAdapterMainNote.notifyDataSetChanged();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TOOL.setToast(context, "Disconnect sever~");
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
                TOOL.setToast(context, "Disconnect sever~");
            }
        });
        requestQueue.add(jsonArrayRequest);
        SendCheckIdNote sendCheckIdNote = ViewModelProviders.of((FragmentActivity) context).get(SendCheckIdNote.class);
        sendCheckIdNote.getCheckId().postValue(false);
    }
    public static void deleteImageNoteByPosition(final Context context,final String id)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, urlDeleteNoteFromService, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.trim().equals("success"))
                    TOOL.setToast(context, "Deleted Successful~");
                else TOOL.setToast(context, "Deleted failure!! " +response);
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

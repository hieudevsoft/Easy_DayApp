package com.example.easyday.FRAGMENT.note;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.easyday.ADAPTER.RecyclerAdapterSetNote;
import com.example.easyday.CONTROL.HelpersService;
import com.example.easyday.CONTROL.SendCheckIdNote;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.ENTITY.ImageNote;
import com.example.easyday.ENTITY.Note;
import com.example.easyday.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SetNoteFragment extends Fragment {
    FloatingActionButton bt_done_note, bt_back_note, bt_camera_note;
    MediaPlayer mediaPlayerDone;
    MediaPlayer mediaPlayerBack;
    MediaPlayer mediaPlayerOpenCam;
    List<ImageNote> imageNoteList;
    List<Note> noteList;
    TextInputEditText edt_title;
    EditText edt_content;
    int sizeImagenote;
    FirebaseUser user;
    EditText edt_idNote;
    AppCompatSpinner spinner;
    String idUser;
    RecyclerView recyclerImageNote;
    RecyclerAdapterSetNote recyclerAdapterSetNote;
    final int PICK_IMAGE_NOTE = 5;
    final int REQUEST_PICK_IMAGE_NOTE = 6;

    final static String TAG="SetNoteFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.set_note_layout, null);
        user = FirebaseAuth.getInstance().getCurrentUser();
        idUser = user.getUid();
        imageNoteList = new ArrayList<>();
        noteList = new ArrayList<>();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        initAdapter();
        if (getArguments() != null) {
            SetNoteFragmentArgs args = SetNoteFragmentArgs.fromBundle(getArguments());
            Note note = args.getNote();
            if (note != null) {
                updateView(note);

            } else
                openTerm();
        }
        bt_done_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null) {
                    SetNoteFragmentArgs args = SetNoteFragmentArgs.fromBundle(getArguments());
                    Note note = args.getNote();
                    if (note != null)
                        updateDataNote();
                    else insertDataNote();
                }
            }
        });
        bt_back_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerBack = MediaPlayer.create(getContext(), R.raw.back);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Navigation.findNavController(view).navigate(SetNoteFragmentDirections.actionReturnMainNote());
                        mediaPlayerBack.release();
                        mediaPlayerBack = null;
                    }
                }, 1000);
                mediaPlayerBack.start();
            }
        });

        bt_camera_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerOpenCam = MediaPlayer.create(getContext(), R.raw.open_camera);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        settingsRequestPermissionCamera();
                        mediaPlayerOpenCam.release();
                        mediaPlayerOpenCam = null;
                    }
                }, 1000);
                mediaPlayerOpenCam.start();

            }
        });
    }




    private void updateView(Note note) {
        edt_idNote.setText(note.getIdNote().split("_")[0].substring(idUser.length()));
        edt_idNote.setEnabled(false);
        edt_title.setText(note.getTitle());
        spinner.setSelection(note.getLevel() - 1);
        edt_content.setText(note.getContent());
        try {
            imageNoteList.addAll(note.getImgNotes());
            sizeImagenote = imageNoteList.size();
        }catch (Exception e)
        {
            sizeImagenote = 0;
        }
        recyclerAdapterSetNote.notifyDataSetChanged();
    }

    private void settingsRequestPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
                        Manifest.permission.CAMERA)) {
                    Toast.makeText(getContext(), "Please access Camera", Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(
                            (Activity) getContext(), new String[]{Manifest.permission.CAMERA},
                            REQUEST_PICK_IMAGE_NOTE);
                }
            }
        }
        {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE_NOTE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else
                    Toast.makeText(getContext(), "Please access Camera", Toast.LENGTH_LONG).show();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openCamera() {
        Intent openCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCam, PICK_IMAGE_NOTE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_NOTE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            imageNoteList.add(new ImageNote(image, ""));
            recyclerAdapterSetNote.notifyDataSetChanged();
        }
    }


    private void updateDataNote() {
        String idNote = edt_idNote.getText().toString().trim();
        String titleNote = edt_title.getText().toString().trim();
        String contentNote = edt_content.getText().toString().trim();
        if (titleNote.length() == 0) {
            edt_title.setError("It's mandatory");
            edt_title.requestFocus();
        } else {
            int level = spinner.getSelectedItemPosition() + 1;
            if (imageNoteList.size() != 0) {
                Log.d(TAG, "ImageNoteList size != 0" );
                if (imageNoteList.size() < sizeImagenote) {
                    Log.d(TAG, "imaNotelist size < sizeImagenote" );
                    int distance = sizeImagenote - imageNoteList.size();
                    Log.d(TAG, "distance = " + distance + "" );
                    while (distance > 0) {
                        sizeImagenote--;
                        HelpersService.deleteImageNoteByPosition(getContext(), idUser.concat(idNote).concat("_").concat(sizeImagenote + ""));
                        distance--;
                    }
                    for (int i = 0; i < imageNoteList.size(); i++)
                        if (i == 0)
                        {
                            if(sizeImagenote!=0)
                            updateNote(HelpersService.getUrlUpdateDataFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), titleNote, contentNote,
                                    TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                            else insertNote(HelpersService.getUrlInsertNoteFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), titleNote, contentNote,
                                    TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                        }
                        else {
                            if(sizeImagenote!=0)
                            updateNote(HelpersService.getUrlUpdateDataFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), "", "",
                                    TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                            else insertNote(HelpersService.getUrlInsertNoteFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), "", "",
                                    TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                        }
                } else {
                    Log.d(TAG, "imaNotelist size >= sizeImagenote" );
                    for (int i = 0; i < imageNoteList.size(); i++)
                        if (i == 0)
                        {
                            if(sizeImagenote!=0){
                                Log.d(getTAG(), "sizeImagenote != 0 ");
                                updateNote(HelpersService.getUrlUpdateDataFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), "", "",
                                        TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                            }
                            else
                            {
                                Log.d(getTAG(), "sizeImagenote == 0 ");
                                insertNote(HelpersService.getUrlInsertNoteFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), "", "",
                                        TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                            }
                        }
                        else {
                            if(sizeImagenote>0&&i<sizeImagenote)
                                updateNote(HelpersService.getUrlUpdateDataFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), "", "",
                                        TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                            else insertNote(HelpersService.getUrlInsertNoteFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), "", "",
                                    TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                        }

                }
            } else {
                Log.d(getTAG(), "imageList = 0");
                try {
                    int distance = sizeImagenote - imageNoteList.size();
                    while (distance > 0) {
                        sizeImagenote--;
                        HelpersService.deleteImageNoteByPosition(getContext(), idUser.concat(idNote).concat("_").concat(sizeImagenote + ""));
                        distance--;
                    }
                }catch (Exception e)
                {

                }
            }
            updateNote(HelpersService.getUrlUpdateDataFromService(), user.getUid().concat(idNote).concat("_"), titleNote, contentNote, ""
                    , "", level, getContext());
            mediaPlayerDone = MediaPlayer.create(getContext(), R.raw.done);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Navigation.findNavController(getView()).navigate(SetNoteFragmentDirections.actionReturnMainNote());
                    mediaPlayerDone.release();
                    mediaPlayerDone = null;
                }
            }, 1000);
            mediaPlayerDone.start();
        }
    }

    public static void updateNote(String urlWebService, final String id, final String title, final String content, final String image, final String description, final int level, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlWebService, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("idNote", id);
                map.put("titleNote", title);
                map.put("contentNote", content);
                map.put("imageNote", image);
                map.put("descriptionNote", description);
                map.put("levelNote", String.valueOf(level));
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    public static void insertNote(String urlWebService, final String id, final String title, final String content, final String image, final String description, final int level, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlWebService, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("idNote", id);
                map.put("titleNote", title);
                map.put("contentNote", content);
                map.put("imageNote", image);
                map.put("descriptionNote", description);
                map.put("levelNote", String.valueOf(level));
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void insertDataNote() {
        String idNote = edt_idNote.getText().toString().trim();
        String titleNote = edt_title.getText().toString().trim();
        if (idNote.length() == 0) {
            edt_idNote.setError("It's mandatory");
            edt_idNote.requestFocus();
        } else if (titleNote.length() == 0) {
            edt_title.setError("It's mandatory");
            edt_title.requestFocus();
        } else {
            HelpersService.checkIdNote(getContext(), HelpersService.getUrlDeleteNoteFromService(), user.getUid(), edt_idNote.getText().toString().trim(),edt_idNote);
            SendCheckIdNote sendCheckIdNote = ViewModelProviders.of((FragmentActivity) getContext()).get(SendCheckIdNote.class);
            sendCheckIdNote.getCheckId().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if(aBoolean){

                    }
                    else {
                        String idNote = edt_idNote.getText().toString().trim();
                        String titleNote = edt_title.getText().toString().trim();
                        String contentNote = edt_content.getText().toString().trim();
                         if (!idNote.contains("_")) {
                             int level = spinner.getSelectedItemPosition() + 1;
                             insertNote(HelpersService.getUrlInsertNoteFromService(), user.getUid().concat(idNote).concat("_"), titleNote, contentNote, ""
                                     , "", level, getContext());
                            if (imageNoteList.size() != 0)
                                for (int i = 0; i < imageNoteList.size(); i++)
                                    if (i == 0)
                                        insertNote(HelpersService.getUrlInsertNoteFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), titleNote, contentNote,
                                                TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());
                                    else
                                        insertNote(HelpersService.getUrlInsertNoteFromService(), user.getUid().concat(idNote + "_").concat(String.valueOf(i)), "", "",
                                                TOOL.convertBitMapToString(imageNoteList.get(i).getImageNote()), imageNoteList.get(i).getDescriptionImage(), level, getContext());

                mediaPlayerDone = MediaPlayer.create(getContext(), R.raw.done);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Navigation.findNavController(getView()).navigate(SetNoteFragmentDirections.actionReturnMainNote());
                        mediaPlayerDone.release();
                        mediaPlayerDone = null;
                    }
                }, 1000);
                mediaPlayerDone.start();
                        } else {
                            edt_idNote.setError("ID invalid!!");
                            edt_idNote.requestFocus();
                        }
                    }
                }
            });
        }
    }

    private void mapping() {
        bt_done_note = getView().findViewById(R.id.bt_done_note);
        bt_back_note = getView().findViewById(R.id.bt_back_note);
        edt_title = getView().findViewById(R.id.edt_title_note);
        edt_content = getView().findViewById(R.id.edt_content_note);
        edt_idNote = getView().findViewById(R.id.edt_id_Note);
        bt_camera_note = getView().findViewById(R.id.bt_camera_note);
        recyclerImageNote = getView().findViewById(R.id.recyclerViewImageNote);
        spinner = getView().findViewById(R.id.spinner_priority);
    }

    private void initAdapter() {
        recyclerAdapterSetNote = new RecyclerAdapterSetNote(getContext(), imageNoteList);
        recyclerImageNote.setHasFixedSize(true);
        StaggeredGridLayoutManager layout = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerImageNote.setLayoutManager(layout);
        recyclerImageNote.setAdapter(recyclerAdapterSetNote);
    }
    private void openTerm() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_must_enter_id_note);
        dialog.findViewById(R.id.bt_ok_term_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static String getTAG() {
        return TAG;
    }
}

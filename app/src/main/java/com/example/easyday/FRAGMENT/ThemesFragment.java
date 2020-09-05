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
import com.example.easyday.ADAPTER.ThemeAdapter;
import com.example.easyday.CONTROL.HelpersServiceThemes;
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
    static ThemeAdapter themeAdapter;
    final int PICK_IMAGE = 111;
    private SendTheme viewModel;
    private static FirebaseUser user;
    private DatabaseReference db;

    public static FirebaseUser getUser() {
        return user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_themes, container, false);
        gridView = view.findViewById(R.id.GridView_themes);
        floatingButtonAdd = view.findViewById(R.id.floatingActionButton);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("Themes").child(user.getUid());
        themeAdapter = new ThemeAdapter(listThemes, getContext(),getActivity());
        gridView.setAdapter(themeAdapter);
        HelpersServiceThemes.getDataFromServer(HelpersServiceThemes.getUrlWebserviceGetData(), user, listThemes, themeAdapter, getContext());
        return view;
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
                    HelpersServiceThemes.updateData(user.getUid(), stringBuilder.toString(), HelpersServiceThemes.getUrlWebserviceUpdate(),getContext());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}

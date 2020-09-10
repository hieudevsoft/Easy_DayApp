package com.example.easyday.FRAGMENT.music.fragmentsviewpager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.easyday.ADAPTER.AdapterSong;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.ENTITY.MusicsFile;
import com.example.easyday.R;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS;


public class ListMusicFragment extends Fragment {
    View view;
    RecyclerView recyclerviewListMusic;
    boolean checkPermission = false;
    final static String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    final static int REQUEST_WRITE_EXTERNAL_STORAGE = 1248;
    public static List<MusicsFile> listMusics;
    AdapterSong adapterSong;
    final static String TAG="ListMusicFragment";

    public static String getTAG() {
        return TAG;
    }

    public ListMusicFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_music, container, false);
        mapping(view);
        Log.d(getTAG(),"isCheckSDCard: " +isCheckSDCard()+"");
        if(isCheckSDCard())
        requestPermission();
        else TOOL.setToast(getContext(), "An occurred...");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(getTAG(),"Permission Granted: " +checkPermission+"");

        super.onActivityCreated(savedInstanceState);
    }

    private void mapping(View view) {
        recyclerviewListMusic = view.findViewById(R.id.recyclerViewListMusic);
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            checkPermission=true;
            initComponents();
            setUpInitAdapter();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), WRITE_EXTERNAL_STORAGE)) {
                TOOL.setToast(getContext(), "Please access storage!");
            } else
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_WRITE_EXTERNAL_STORAGE&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            checkPermission = true;
            initComponents();
            setUpInitAdapter();
            TOOL.setToast(getContext(), "Access Storage~");
        }
        else requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isCheckSDCard()
    {
        Boolean isSDCardPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        Boolean isSdcardSupportDevice = Environment.isExternalStorageRemovable();
        if(isSDCardPresent||isSdcardSupportDevice)
            return true;
        else {
            TOOL.setToast(getContext(), "SD card not supported!");
            return false;
        }
    }
    private void initComponents()
    {
        listMusics = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String [] projections = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };
        Cursor cursor= getContext().getContentResolver().query(uri, projections, null, null,null);
        if(cursor!=null) {
            while (cursor.moveToNext()&!cursor.isAfterLast()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String data = cursor.getString(3);
                String artist = cursor.getString(4);

                MusicsFile musicsFile = new MusicsFile(album, title, duration, data, artist);
                listMusics.add(musicsFile);

            }
            Log.d(getTAG(),"ListMusics size : " + listMusics.size()+"");
        }
        else TOOL.setToast(getContext(), "null");
    }
    private void setUpInitAdapter()
    {
        if(!(listMusics.size()<1)) {
            adapterSong = new AdapterSong(listMusics, getContext());
            recyclerviewListMusic.setHasFixedSize(true);
            LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
            recyclerviewListMusic.setLayoutManager(layout);
            recyclerviewListMusic.setAdapter(adapterSong);
        } else TOOL.setToast(getContext(),"No songs yet!");
    }
}
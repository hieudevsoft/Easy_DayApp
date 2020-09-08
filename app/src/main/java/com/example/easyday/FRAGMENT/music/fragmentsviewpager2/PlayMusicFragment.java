package com.example.easyday.FRAGMENT.music.fragmentsviewpager2;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.easyday.R;

import jp.wasabeef.blurry.Blurry;

public class PlayMusicFragment extends Fragment {

    ImageView imageBlur,imageSong;
    TextView nameSong,artistSong,startTime,endTime;
    ImageButton bt_prev,bt_next,bt_play,bt_shuffle,bt_looping;
    View view;
    SeekBar durationSong;
    public PlayMusicFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_play_music, container, false);

        mapping(view);
        return view;
    }

    private void mapping(View view){
        imageBlur = view.findViewById(R.id.blurImageSongs);
        imageSong = view.findViewById(R.id.image_Song);
        nameSong = view.findViewById(R.id.name_Song);
        artistSong = view.findViewById(R.id.artist_Song);
        startTime = view.findViewById(R.id.start_time_song);
        endTime = view.findViewById(R.id.end_time_song);
        bt_looping = view.findViewById(R.id.bt_lopping_song);
        bt_next = view.findViewById(R.id.bt_next_song);
        bt_play = view.findViewById(R.id.bt_play_pause);
        bt_shuffle = view.findViewById(R.id.bt_shuffle_song);
        bt_prev = view.findViewById(R.id.bt_prev_song);
        durationSong = view.findViewById(R.id.duration_Song);
    }

}
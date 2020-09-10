package com.example.easyday.FRAGMENT.music.fragmentsviewpager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easyday.CONTROL.SendPositionSong;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.ENTITY.MusicsFile;
import com.example.easyday.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.blurry.Blurry;

public class PlayMusicFragment extends Fragment {

    ImageView imageBlur,imageSong;
    TextView nameSong,artistSong,startTime,endTime;
    ImageButton bt_prev,bt_next,bt_play,bt_shuffle,bt_looping;
    View view;
    SeekBar durationSong;
    int currentTimeSong;
    int timeSongMax=-1;
    int positionSong = -1;
    List<MusicsFile> listSongs;
    MediaPlayer mediaPlayer;
    Handler handler;
    SharedPreferences sharedPreferences;
    final static String TAG = "PlayMusicFragment";

    public static String getTAG() {
        return TAG;
    }

    public PlayMusicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(getTAG(), "onCreateView");
        view =  inflater.inflate(R.layout.fragment_play_music, container, false);
        mapping(view);
        handler = new Handler();
        sharedPreferences = getActivity().getSharedPreferences("POSITION_SONG", Context.MODE_PRIVATE);
        getPositionSong();
        try {
            setUpControl();
        }catch (Exception e)
        {

        }

        durationSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser)
                {
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
//        listSongs.addAll(ListMusicFragment.listMusics);
//        positionSong = sharedPreferences.getInt("positionSong", -1);
//        setUpViewSongs(listSongs.get(positionSong));
//        bt_play.setImageResource(R.drawable.ic_play_song);
//        initMediaPlayer(listSongs.get(positionSong));
//        setSeekBarMove();

        super.onResume();
    }

    private void mapping(View view){
        imageBlur = view.findViewById(R.id.blurImageSongs);
        imageSong = view.findViewById(R.id.imageSongsAlbum);
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

    private void getPositionSong()
    {
        SendPositionSong sendPositionSong = ViewModelProviders.of((FragmentActivity) getContext()).get(SendPositionSong.class);
        sendPositionSong.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                listSongs = new ArrayList<>();
                positionSong = integer;
                if(positionSong!=-1)
                {
                    listSongs.addAll(ListMusicFragment.listMusics);
                    sharedPreferences.edit().putString("uriSong", listSongs.get(positionSong).getData()).apply();
                    sharedPreferences.edit().putInt("positionSong", positionSong).apply();
                    setUpControlClickPosition();
                }
                else
                TOOL.setToast(getContext(), "An occurred...");
            }
        });
    }

    private void setUpViewSongs(MusicsFile musicsFile) {
        byte[] image = TOOL.getAlbumArt(musicsFile.getData());
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        TOOL.setBlurForImageView(getContext(), imageBlur, bitmap, 1);
        imageSong.setImageBitmap(bitmap);
        nameSong.setText(musicsFile.getTitle());
        artistSong.setText(musicsFile.getArtist());
        endTime.setText(formattedTime(String.valueOf(Long.parseLong(musicsFile.getDuration())/1000)));

    }

    private String formattedTime(String time)
    {
        String timeOld="";
        String timeNew="";
        int timereal = Integer.parseInt(time);
        int minute = timereal/60;
        int second = timereal%60;
        timeNew = minute+":"+second;
        timeOld = minute+":"+"0"+second;
        if(second>=10)
        {
            return timeNew;
        }
        else return timeOld;
    }

    private void setSeekBarMove()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                       int currentTimeSong = mediaPlayer.getCurrentPosition()/1000;
                       sharedPreferences.edit().putInt("currentPositionTimeSong",
                               currentTimeSong).apply();
                       Log.d(getTAG(),"timeSong: " + sharedPreferences.getInt("currentPositionTimeSong",0));
                       durationSong.setProgress(currentTimeSong);
                       startTime.setText(formattedTime(String.valueOf(currentTimeSong)));
                       handler.postDelayed(this, 1000);
                    }
        });
    }
    private void setSeekBarMoveNotClick()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sharedPreferences.edit().putInt("currentPositionTimeSong",
                        currentTimeSong).apply();
                Log.d(getTAG(),"timeSongNotClick: " + durationSong.getProgress());
                startTime.setText(formattedTime(String.valueOf(durationSong.getProgress())));
                durationSong.setProgress(durationSong.getProgress()+1);
                handler.postDelayed(this, 1000);
            }
        });

    }
    private void initMediaPlayer(MusicsFile musicsFile)
    {
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(musicsFile.getData()));
            mediaPlayer.start();
        }else {
            mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(musicsFile.getData()));
            mediaPlayer.start();
        }
        durationSong.setMax(mediaPlayer.getDuration()/1000);
        sharedPreferences.edit().putInt("durationSongMax", mediaPlayer.getDuration()/1000).apply();
    }

    private void setUpControl()
    {
        listSongs = new ArrayList<>();
        listSongs.addAll(ListMusicFragment.listMusics);
        positionSong = sharedPreferences.getInt("positionSong", -1);
        currentTimeSong = sharedPreferences.getInt("currentPositionTimeSong",0);
        timeSongMax = sharedPreferences.getInt("durationSongMax",0);
        Log.d(getTAG(),"Current time song: " + currentTimeSong+"");
        Log.d(getTAG(),"Time Song Max: " + timeSongMax+"");
        durationSong.setMax(timeSongMax);
        durationSong.setProgress(currentTimeSong);
        setUpViewSongs(listSongs.get(positionSong));
        bt_play.setImageResource(R.drawable.ic_play_song);
        setSeekBarMoveNotClick();
    }

    private void setUpControlClickPosition()
    {
        listSongs = new ArrayList<>();
        listSongs.addAll(ListMusicFragment.listMusics);
        positionSong = sharedPreferences.getInt("positionSong", -1);
        setUpViewSongs(listSongs.get(positionSong));
        bt_play.setImageResource(R.drawable.ic_play_song);
        initMediaPlayer(listSongs.get(positionSong));
        setSeekBarMove();
    }

}
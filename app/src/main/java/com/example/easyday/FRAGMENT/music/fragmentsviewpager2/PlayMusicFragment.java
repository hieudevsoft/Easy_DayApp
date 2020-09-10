package com.example.easyday.FRAGMENT.music.fragmentsviewpager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.easyday.CONTROL.SendPositionSong;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.ENTITY.MusicsFile;
import com.example.easyday.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayMusicFragment extends Fragment {

    ImageView imageBlur, imageSong;
    TextView nameSong, artistSong, startTime, endTime;
    ImageButton bt_prev, bt_next, bt_play, bt_shuffle, bt_looping;
    View view;
    SeekBar durationSong;
    int currentTimeSong;
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
        view = inflater.inflate(R.layout.fragment_play_music, container, false);
        mapping(view);
        handler = new Handler();
        initSharedPreferences();
        logShuffleAndLooping();
        getPositionSongViewModel();
        try {
            setUpControl();
        } catch (Exception e) {
        }
        durationSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
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

    private void logShuffleAndLooping() {
        Log.d(getTAG(),"Shuffle: " +sharedPreferences.getBoolean("checkShuffle", false));
        Log.d(getTAG(),"Looping: " +sharedPreferences.getBoolean("checkLooping", false));
    }

    @Override
    public void onResume() {
        try {
            btClickButtonPlay();
            btClickButtonNext();
            btClickButtonPrev();
            btClickButtonShuffle();
            btClickButtonLooping();
        } catch (Exception e) {
            TOOL.setToast(getContext(), "No song play!");
        }
        super.onResume();
    }

    private void btClickButtonPrev() {

        bt_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevSong();
            }
        });
    }

    private void btClickButtonNext() {
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });
    }

    private void btClickButtonPlay() {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlayCLick();
            }
        });

    }

    private void btClickButtonShuffle() {
        shuffleButtonClick();
    }

    private void btClickButtonLooping() {
        loopingButtonClick();
    }

    // Retrieve position Song
    private void getPositionSongViewModel() {
        SendPositionSong sendPositionSong = ViewModelProviders.of((FragmentActivity) getContext()).get(SendPositionSong.class);
        sendPositionSong.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                listSongs = new ArrayList<>();
                positionSong = integer;
                if (positionSong != -1) {
                    Log.d(getTAG(), "Get position Running....");
                    setPositionSong(positionSong);
                    setBackPress(false);
                    setUpControl();
                    setUriSong(listSongs.get(positionSong).getData());
                } else
                    TOOL.setToast(getContext(), "An occurred...");
            }
        });
    }

    private String formattedTime(String time) {
        String timeOld = "";
        String timeNew = "";
        int timereal = Integer.parseInt(time);
        int minute = timereal / 60;
        int second = timereal % 60;
        timeNew = minute + ":" + second;
        timeOld = minute + ":" + "0" + second;
        if (second >= 10) {
            return timeNew;
        } else return timeOld;
    }

    //Thread control song
    private void setSeekBarMove() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getBackPress() && !sharedPreferences.getBoolean("onCreate", false)) {
                    if (mediaPlayer != null) {
                        try {
                            mediaPlayer.pause();
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        } catch (Exception e) {
                            Log.d(getTAG(), "Exception release music: " + e.getMessage());
                        }

                    }
                    return;
                } else if (sharedPreferences.getBoolean("onCreate", true)) ;
                {
                    int currentTimeSong = mediaPlayer.getCurrentPosition() / 1000;
                    setCurrentPositionTimeSong(currentTimeSong);
                    setCurrentPositionTimeSong(currentTimeSong);
                    if (currentTimeSong != durationSong.getMax()) {
                        sharedPreferences.edit().putInt("currentPositionTimeSong",
                                currentTimeSong).apply();
//                    Log.d(getTAG(), "Time Song in UI Thread: " + sharedPreferences.getInt("currentPositionTimeSong", 0));
                        durationSong.setProgress(currentTimeSong);
                        startTime.setText(formattedTime(String.valueOf(currentTimeSong)));
                    } else {
                        Log.d(getTAG(), "NEXT SONG....");
                        controlChoiceFromUser();
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    //Shuffle songs when user choice
    private void controlChoiceFromUser() {
        int position = -1;
        if(getCheckShuffle()){
            do {
                Random random = new Random();
                position = random.nextInt(listSongs.size() - 1);
            }while(position==getPositionSong());
            setPositionSong(position);
            setUpControl();
        }else {
            playNextSong();
        }
    }

    //Init MediaPlayer
    private void initMediaPlayer(MusicsFile musicsFile) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(musicsFile.getData()));
            mediaPlayer.start();
        } else {
            if (sharedPreferences.getBoolean("backPress", false)) {
                Log.d(getTAG(), "MediaPlayer Create Saved UI");
                currentTimeSong = sharedPreferences.getInt("currentPositionTimeSong", 0);
                Log.d(getTAG(), "Current time song in Init Media:" + currentTimeSong);
                mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(musicsFile.getData()));
                mediaPlayer.start();
                mediaPlayer.pause();
                mediaPlayer.seekTo(currentTimeSong * 1000);

            } else {
                mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(musicsFile.getData()));
                mediaPlayer.start();
            }
        }
        durationSong.setMax(mediaPlayer.getDuration() / 1000);
    }

    //Set control music
    private void setUpControl() {
        Log.d("PlayMusicFragment", "backPress: " + getBackPress() + "");
        listSongs = new ArrayList<>();
        positionSong = getPositionSong();
        listSongs.addAll(ListMusicFragment.listMusics);
        setUpViewSongs(listSongs.get(positionSong));
        initMediaPlayer(listSongs.get(positionSong));
        setSeekBarMove();
        if (getBackPress()) {
            currentTimeSong = getCurrntPositionTimeSong();
            Log.d(getTAG(), "Time Song Init Create New: " + currentTimeSong + "");
            durationSong.setProgress(currentTimeSong);
        }
    }

    //Update view Music
    private void setUpViewSongs(MusicsFile musicsFile) {
        byte[] image = TOOL.getAlbumArt(musicsFile.getData());
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        TOOL.setBlurForImageView(getContext(), imageBlur, bitmap, 1);
        imageSong.setImageBitmap(bitmap);
        nameSong.setText(musicsFile.getTitle());
        artistSong.setText(musicsFile.getArtist());
        endTime.setText(formattedTime(String.valueOf(Long.parseLong(musicsFile.getDuration()) / 1000)));
        Log.d(getTAG(), "UriSong: " + musicsFile.getData());
        Log.d(getTAG(), "Values backPress in update Views Song :" + getBackPress());
        if (getBackPress())
            bt_play.setImageResource(R.drawable.ic_play);
        else bt_play.setImageResource(R.drawable.ic_play_song);
        if (getCheckLooping())
            bt_looping.setImageResource(R.drawable.ic_looping_checked);
         else
            bt_looping.setImageResource(R.drawable.ic_looping_song);
        if (getCheckShuffle())
            bt_shuffle.setImageResource(R.drawable.ic_shuffle_checked);
        else
            bt_shuffle.setImageResource(R.drawable.ic_shuffle);


    }

    //Set CLick buttons
    private void playPrevSong() {
        Log.d(getTAG(), "Position song before next: " + positionSong);
        try {
            if(positionSong!=-1){
            int position = positionSong - 1;
            if (positionSong - 1 < 0) position = listSongs.size() - 1;
            Log.d(getTAG(), "Position song current: " + position);
            sharedPreferences.edit().putInt("positionSong", position).apply();
            setUpControl();
            bt_play.setImageResource(R.drawable.ic_play_song);
            }
        } catch (Exception e) {
            Log.d(getTAG(), e.getMessage());
        }
    }

    private void playNextSong() {
        Log.d(getTAG(), "Position song before next: " + positionSong);
        try {
            if (positionSong != -1) {
                int position = (positionSong + 1) % (listSongs.size());
                sharedPreferences.edit().putInt("positionSong", position).apply();
                setUpControl();
                bt_play.setImageResource(R.drawable.ic_play_song);
            }
        } catch (Exception e) {
            Log.d(getTAG(), "Error next song: " + e.getMessage());
        }
    }

    private void buttonPlayCLick() {
        try {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                bt_play.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                bt_play.setImageResource(R.drawable.ic_play_song);
            }
        } catch (Exception e) {
            Log.d(getTAG(), "Error button play: " + e.getMessage());
        }
    }

    private void shuffleButtonClick() {
        bt_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCheckShuffle()) {
                    bt_shuffle.setImageResource(R.drawable.ic_shuffle);
                    setCheckShuffle(false);
                } else {
                    bt_shuffle.setImageResource(R.drawable.ic_shuffle_checked);
                    setCheckShuffle(true);
                }
            }
        });
    }

    private void loopingButtonClick()
    {
        try {
            bt_looping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCheckLooping()) {
                        bt_looping.setImageResource(R.drawable.ic_looping_song);
                        setCheckLooping(false);
                        if(mediaPlayer!=null)
                        mediaPlayer.setLooping(false);
                    } else {
                        bt_looping.setImageResource(R.drawable.ic_looping_checked);
                        setCheckLooping(true);
                        if(mediaPlayer!=null)
                        mediaPlayer.setLooping(true);
                    }
                }
            });
        }catch (Exception e)
        {

        }

    }

    //Set get SharedPreferences
    private void initSharedPreferences() {
        sharedPreferences = getActivity().getSharedPreferences("POSITION_SONG", Context.MODE_PRIVATE);
    }

    private void setPositionSong(int positionSong) {
        sharedPreferences.edit().putInt("positionSong", positionSong).apply();
    }

    private int getPositionSong() {
        return sharedPreferences.getInt("positionSong", -1);
    }

    private Boolean getCheckShuffle()
    {
        return sharedPreferences.getBoolean("checkShuffle", false);
    }

    private void setCheckShuffle(boolean check)
    {
        sharedPreferences.edit().putBoolean("checkShuffle",check).apply();
    }

    private Boolean getCheckLooping()
    {
        return sharedPreferences.getBoolean("checkLooping", false);
    }

    private void setCheckLooping(boolean check)
    {
        sharedPreferences.edit().putBoolean("checkLooping",check).apply();
    }

    private void setBackPress(boolean item) {
        sharedPreferences.edit().putBoolean("backPress", false).apply();
    }

    private boolean getBackPress(){
        return sharedPreferences.getBoolean("backPress", false);
    }

    private void setUriSong(String s) {
        sharedPreferences.edit().putString("uriSong", s).apply();
    }

    private String getUriSong(){return sharedPreferences.getString("uriSong", "");}

    private void setCurrentPositionTimeSong(int currentTimeSong) {
        sharedPreferences.edit().putInt("currentPositionTimeSong",currentTimeSong).apply();
    }

    private int getCurrntPositionTimeSong(){return sharedPreferences.getInt("currentPositionTimeSong", 0);}
    // set ID widgets
    private void mapping(View view) {
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

}
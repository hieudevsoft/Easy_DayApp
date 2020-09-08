package com.example.easyday.SERVICE;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.easyday.ACTIVITY.AlarmActivity;
import com.example.easyday.R;

public class AlarmService extends Service {
    MediaPlayer alarmMusic;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alarmMusic != null) {
            alarmMusic.release();
            alarmMusic = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("alarmService", intent.getBooleanExtra("checkMusic", false) + "");
        if (intent.getBooleanExtra("checkMusic", false)) {
            alarmMusic = MediaPlayer.create(getApplicationContext(), intent.getIntExtra("music", R.raw.done));
            alarmMusic.setLooping(true);
            alarmMusic.start();
            NotificationCompat.Builder nb = new NotificationCompat.Builder(getApplicationContext());
            nb.setSmallIcon(R.drawable.ic_bell);
            nb.setContentTitle("Wake up...");
            nb.setContentText(intent.getStringExtra("contentText"));
            nb.setAutoCancel(true);
            nb.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 1, new Intent(getApplicationContext(), AlarmActivity.class), 0));
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.notify(PendingIntent.FLAG_UPDATE_CURRENT, nb.build());
        } else {
            if (alarmMusic != null)
                alarmMusic.stop();
            alarmMusic.release();
        }
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}


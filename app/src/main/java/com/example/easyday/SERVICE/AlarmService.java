package com.example.easyday.SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.easyday.ACTIVITY.AlarmActivity;
import com.example.easyday.CLASS.App;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.R;

public class AlarmService extends Service {
    MediaPlayer alarmMusic;
    Boolean isServiceRunning = false;
    NotificationManagerCompat notificationManager;
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
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("alarmService", intent.getBooleanExtra("checkMusic", false) + "");
        if (intent!=null && intent.getAction().equals(TOOL.ACTION_START_SERVICE) && intent.getBooleanExtra("checkMusic", false)) {
            startNotificationAlarm(intent);
        } else {
            stopMyService();
        }
        return START_STICKY;
    }

    private void startNotificationAlarm(Intent intent)
    {
        Log.d("AlarmService","Service Running: " +isServiceRunning+"");
        Log.d("AlarmService","Music Running: "+intent.getBooleanExtra("checkAlarm", false)+"");

        if(intent.getBooleanExtra("checkAlarm", false)) {
           stopMyService();
           return;
        }
        else {
            if(isServiceRunning) return;
            isServiceRunning = true;

            if(intent.getBooleanExtra("checkMusic", false))
            {
            alarmMusic = MediaPlayer.create(getApplicationContext(), intent.getIntExtra("music", R.raw.done));
            alarmMusic.setLooping(true);
            alarmMusic.start();
            Intent notificationIntent = new Intent(getApplicationContext(), AlarmActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.setAction(TOOL.ACTION_MAIN);


            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_bell);

            Notification nb = new NotificationCompat.Builder(this, App.notification_channel)
                    .setSmallIcon(R.drawable.ic_bell)
                    .setContentTitle("Wake up...")
                    .setContentText(intent.getStringExtra("contentText"))
                    .setAutoCancel(true)
                    .setTicker("Ticker")
                    .setOngoing(true)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setCategory(Notification.CATEGORY_ALARM)
                    .setOnlyAlertOnce(true)
                    .build();

            nb.flags = nb.flags | Notification.FLAG_NO_CLEAR;
            notificationManager.notify(1, nb);
            }
        }
    }

    private void stopMyService()
    {
        try {
            alarmMusic.release();
            alarmMusic = null;
        }
        catch (Exception e)
        {

        }
    stopForeground(true);
    stopSelf();
    isServiceRunning=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}


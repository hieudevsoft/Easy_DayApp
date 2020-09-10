package com.example.easyday.CLASS;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String notification_channel = "channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotifications();
    }

    private void createNotifications() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(notification_channel, "Easy Day", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Notification EasyDay");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

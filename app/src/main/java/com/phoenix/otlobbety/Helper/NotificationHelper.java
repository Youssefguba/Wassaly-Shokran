package com.phoenix.otlobbety.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.phoenix.otlobbety.R;


public class NotificationHelper extends ContextWrapper {

    private static final String OTLOB_CHANNEL_ID = "com.phoenix.otlobbety.OTLOB";
    private static final String OTLOB_CHANNEL_NAME = "OTLOB BETY";

    private NotificationManager notificationManager;


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Work just for API 26 or Higher
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(OTLOB_CHANNEL_ID,
                OTLOB_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getMohsenAppNotificationChannel(String title, String body,
                                                                            PendingIntent pendingIntent, Uri soundUri) {

        return new Notification.Builder(getApplicationContext(), OTLOB_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(soundUri)
                .setAutoCancel(true);


    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getMohsenAppNotificationChannel(String title, String body, Uri soundUri) {

        return new Notification.Builder(getApplicationContext(), OTLOB_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(soundUri)
                .setAutoCancel(true);


    }
}

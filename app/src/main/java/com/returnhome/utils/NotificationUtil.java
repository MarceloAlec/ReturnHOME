package com.returnhome.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.returnhome.R;

public class NotificationUtil extends ContextWrapper {

    private static final String CHANNEL_ID = "com.returnhome";
    private static final String CHANNEL_NAME = "ReturnHOME";

    private NotificationManager manager;

    public NotificationUtil(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(){
        NotificationChannel notificationChannel = new
                NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    //PARA CREAR NOTIFICACIONES EN VERSIONES 26 O SUPERIOR DE ANDROID
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotificationPetMissing(String title, String body, Uri soundUri, Notification.Action showPetMissingAction ) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setShowWhen(true)
                .addAction(showPetMissingAction)
                .setSmallIcon(R.drawable.ic_app_notification)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(body).setBigContentTitle(title));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotificationPetFound(String title, String body, Uri soundUri, Notification.Action showPetMissingAction, Notification.Action cancelAction) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setShowWhen(true)
                .addAction(showPetMissingAction)
                .addAction(cancelAction)
                .setSmallIcon(R.drawable.ic_app_notification)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(body).setBigContentTitle(title));
    }


    public NotificationCompat.Builder getNotificationPetMissing(String title, String body, Uri soundUri, NotificationCompat.Action showPetFoundAction) {

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_app_notification)
                .addAction(showPetFoundAction)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

    public NotificationCompat.Builder getNotificationPetFound(String title, String body, Uri soundUri, NotificationCompat.Action showPetFoundAction, NotificationCompat.Action contactAction) {

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_app_notification)
                .addAction(showPetFoundAction)
                .addAction(contactAction)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }
}

package com.returnhome.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.returnhome.R;
import com.returnhome.receivers.ContactReceiver;
import com.returnhome.receivers.PetFoundReceiver;
import com.returnhome.receivers.PetMissingReceiver;
import com.returnhome.utils.AppConfig;
import com.returnhome.utils.NotificationUtil;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private AppConfig mAppConfig;

    private static final int NOTIFICATION_CODE = 100;



    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        mAppConfig = new AppConfig(getApplicationContext());

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if(title.contains("Mascota encontrada")){

                    int  idClient = Integer.valueOf(data.get("idClient"));
                    String petName = data.get("pet_name");
                    String phoneNumber = data.get("phoneNumber");
                    double pet_lat = Double.parseDouble(data.get("pet_lat"));
                    double pet_lng  = Double.parseDouble(data.get("pet_lng"));

                    LatLng petLatLng = new LatLng(pet_lat, pet_lng);
                    showNotificationApiOreoPetFound(title, body, idClient, phoneNumber, petName, petLatLng);

                }
                else{

                    int  idPet = Integer.valueOf(data.get("idPet"));
                    String petName = data.get("pet_name");
                    double pet_lat = Double.parseDouble(data.get("pet_lat"));
                    double pet_lng  = Double.parseDouble(data.get("pet_lng"));

                    LatLng petLatLng = new LatLng(pet_lat, pet_lng);
                    showNotificationApiOreoPetMissing(title, body, idPet, petName, petLatLng);
                }

            }
            else{
                if(title.contains("Mascota encontrada")){

                    int  idClient = Integer.valueOf(data.get("idClient"));
                    String petName = data.get("pet_name");
                    String phoneNumber = data.get("phoneNumber");
                    double pet_lat = Double.parseDouble(data.get("pet_lat"));
                    double pet_lng  = Double.parseDouble(data.get("pet_lng"));

                    LatLng petLatLng = new LatLng(pet_lat, pet_lng);
                    showNotificationPetFound(title, body, idClient, phoneNumber, petName, petLatLng);
                }
                else{

                    int  idPet = Integer.valueOf(data.get("idPet"));
                    String petName = data.get("pet_name");
                    double pet_lat = Double.parseDouble(data.get("pet_lat"));
                    double pet_lng  = Double.parseDouble(data.get("pet_lng"));

                    LatLng petLatLng = new LatLng(pet_lat, pet_lng);
                    showNotificationPetMissing(title, body, idPet, petName, petLatLng);
                }
            }

        }

    }

    private void showNotificationPetFound(String title, String body, int idClient, String phoneNumber, String petName, LatLng petLatLng) {

        Intent showPetFoundIntent = new Intent(this, PetFoundReceiver.class);
        showPetFoundIntent.putExtra("idClient", idClient);
        showPetFoundIntent.putExtra("pet_name", petName);
        showPetFoundIntent.putExtra("pet_lat", petLatLng.latitude);
        showPetFoundIntent.putExtra("pet_lng", petLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, showPetFoundIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action showPetFoundAction= new NotificationCompat.Action.Builder(
                R.drawable.ic_app,
                "Mostrar en mapa",
                viewPendingIntent
        ).build();

        Intent contactIntent = new Intent(this, ContactReceiver.class);
        contactIntent.putExtra("phoneNumber", phoneNumber);
        PendingIntent contactPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, contactIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action contactAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_app,
                "Contactar",
                contactPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationUtil notificationUtil = new NotificationUtil(getBaseContext());
        NotificationCompat.Builder builder = notificationUtil.getNotificationOldAPIPetFound(title, body, sound, showPetFoundAction, contactAction);
        notificationUtil.getManager().notify(1, builder.build());
    }

    private void showNotificationPetMissing(String title, String body, int idPet, String petName, LatLng petLatLng) {

        Intent showPetMissingIntent = new Intent(this, PetMissingReceiver.class);
        showPetMissingIntent.putExtra("idPet", idPet);
        showPetMissingIntent.putExtra("pet_name", petName);
        showPetMissingIntent.putExtra("pet_lat", petLatLng.latitude);
        showPetMissingIntent.putExtra("pet_lng", petLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, showPetMissingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action showPetMissingAction= new NotificationCompat.Action.Builder(
                R.drawable.ic_app,
                "Mostrar en mapa",
                viewPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationUtil notificationUtil = new NotificationUtil(getBaseContext());
        NotificationCompat.Builder builder = notificationUtil.getNotificationOldAPIPetMissing(title, body, sound, showPetMissingAction);
        notificationUtil.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreoPetFound(String title, String body, int idClient, String phoneNumber, String petName, LatLng petLatLng) {

        Intent showPetFoundIntent = new Intent(this, PetFoundReceiver.class);
        showPetFoundIntent.putExtra("idClient", idClient);
        showPetFoundIntent.putExtra("pet_name", petName);
        showPetFoundIntent.putExtra("pet_lat", petLatLng.latitude);
        showPetFoundIntent.putExtra("pet_lng", petLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, showPetFoundIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action showPetFoundAction= new Notification.Action.Builder(
                R.drawable.ic_app,
                "Mostrar en mapa",
                viewPendingIntent
        ).build();

        Intent contactIntent = new Intent(this, ContactReceiver.class);
        contactIntent.putExtra("phoneNumber", phoneNumber);
        PendingIntent contactPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, contactIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action contactAction = new Notification.Action.Builder(
                R.drawable.ic_app,
                "Contactar",
                contactPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationUtil notificationUtil = new NotificationUtil(getBaseContext());
        Notification.Builder builder = notificationUtil.getNotificationPetFound(title, body, sound, showPetFoundAction, contactAction);
        notificationUtil.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreoPetMissing(String title, String body, int idPet, String petName, LatLng petLatLng) {

        Intent showPetMissingIntent = new Intent(this, PetMissingReceiver.class);
        showPetMissingIntent.putExtra("idPet", idPet);
        showPetMissingIntent.putExtra("pet_name", petName);
        showPetMissingIntent.putExtra("pet_lat", petLatLng.latitude);
        showPetMissingIntent.putExtra("pet_lng", petLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, showPetMissingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action showPetMissingAction= new Notification.Action.Builder(
                R.drawable.ic_app,
                "Mostrar en mapa",
                viewPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationUtil notificationUtil = new NotificationUtil(getBaseContext());
        Notification.Builder builder = notificationUtil.getNotificationPetMissing(title, body, sound, showPetMissingAction);
        notificationUtil.getManager().notify(1, builder.build());

    }
}

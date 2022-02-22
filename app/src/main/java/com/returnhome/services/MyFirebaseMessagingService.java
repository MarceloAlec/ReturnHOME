package com.returnhome.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.returnhome.R;
import com.returnhome.receivers.ContactoReceiver;
import com.returnhome.receivers.MascotaEncontradaReceiver;
import com.returnhome.receivers.MascotaDesaparecidaReceiver;
import com.returnhome.utils.CanalNotificacion;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_CODE = 100;


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notificacion = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if(title.contains("Mascota encontrada")){

                    int  idCliente = Integer.valueOf(data.get("idCliente"));
                    String nombreMascota = data.get("nombreMascota");
                    String numeroCelular = data.get("numeroCelular");
                    double mascotaLat = Double.parseDouble(data.get("mascotaLat"));
                    double mascotaLng  = Double.parseDouble(data.get("mascotaLng"));

                    LatLng petLatLng = new LatLng(mascotaLat, mascotaLng);
                    mostrarNotificacionApiOreoMascotaEncontrada(title, body, idCliente, numeroCelular, nombreMascota, petLatLng);

                }
                else{

                    int  idMascota = Integer.valueOf(data.get("idMascota"));
                    String nombreMascota = data.get("nombreMascota");
                    double mascotaLat = Double.parseDouble(data.get("mascotaLat"));
                    double mascotaLng  = Double.parseDouble(data.get("mascotaLng"));

                    LatLng mascotaLatLng = new LatLng(mascotaLat, mascotaLng);
                    mostrarNotificacionApiOreoMascotaDesaparecida(title, body, idMascota, nombreMascota, mascotaLatLng);
                }

            }
            else{
                if(title.contains("Mascota encontrada")){

                    int  idCliente = Integer.valueOf(data.get("idCliente"));
                    String nombreMascota = data.get("nombreMascota");
                    String numeroCelular = data.get("numeroCelular");
                    double mascotaLat = Double.parseDouble(data.get("mascotaLat"));
                    double mascotaLng  = Double.parseDouble(data.get("mascotaLng"));

                    LatLng petLatLng = new LatLng(mascotaLat, mascotaLng);
                    mostrarNotificacionMascotaEncontrada(title, body, idCliente, numeroCelular, nombreMascota, petLatLng);
                }
                else{

                    int  idMascota = Integer.valueOf(data.get("idMascota"));
                    String nombreMascota = data.get("nombreMascota");
                    double mascotaLat = Double.parseDouble(data.get("mascotaLat"));
                    double mascotaLng  = Double.parseDouble(data.get("mascotaLng"));

                    LatLng mascotaLatLng = new LatLng(mascotaLat, mascotaLng);
                    mostrarNotificacionMascotaDesaparecida(title, body, idMascota, nombreMascota, mascotaLatLng);
                }
            }

        }

    }

    private void mostrarNotificacionMascotaEncontrada(String title, String body, int idCliente, String numeroCelular, String nombreMascota, LatLng mascotaLatLng) {

        Intent mascotaEncontradaIntent = new Intent(this, MascotaEncontradaReceiver.class);
        mascotaEncontradaIntent.putExtra("idCliente", idCliente);
        mascotaEncontradaIntent.putExtra("nombreMascota", nombreMascota);
        mascotaEncontradaIntent.putExtra("mascotaLat", mascotaLatLng.latitude);
        mascotaEncontradaIntent.putExtra("mascotaLng", mascotaLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, mascotaEncontradaIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action mascotaEncontradaAction= new NotificationCompat.Action.Builder(
                R.drawable.ic_app,
                "Mostrar en mapa",
                viewPendingIntent
        ).build();

        Intent contactoIntent = new Intent(this, ContactoReceiver.class);
        contactoIntent.putExtra("numeroCelular", numeroCelular);
        PendingIntent contactoPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, contactoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action contactoAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_app,
                "Contactar",
                contactoPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CanalNotificacion canalNotificacion = new CanalNotificacion(getBaseContext());
        NotificationCompat.Builder builder = canalNotificacion.getNotificationPetFound(title, body, sound, mascotaEncontradaAction, contactoAction);
        canalNotificacion.getManager().notify(1, builder.build());
    }

    private void mostrarNotificacionMascotaDesaparecida(String title, String body, int idMascota, String nombreMascota, LatLng mascotaLatLng) {

        Intent mostrarMascotaDesaparecidaIntent = new Intent(this, MascotaDesaparecidaReceiver.class);
        mostrarMascotaDesaparecidaIntent.putExtra("idMascota", idMascota);
        mostrarMascotaDesaparecidaIntent.putExtra("nombreMascota", nombreMascota);
        mostrarMascotaDesaparecidaIntent.putExtra("mascotaLat", mascotaLatLng.latitude);
        mostrarMascotaDesaparecidaIntent.putExtra("mascotaLng", mascotaLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, mostrarMascotaDesaparecidaIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action mostrarMascotaDesaparecidaAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_app,
                "Mostrar en mapa",
                viewPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CanalNotificacion canalNotificacion = new CanalNotificacion(getBaseContext());
        NotificationCompat.Builder builder = canalNotificacion.getNotificationPetMissing(title, body, sound, mostrarMascotaDesaparecidaAction );
        canalNotificacion.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void mostrarNotificacionApiOreoMascotaEncontrada(String title, String body, int idCliente, String numeroCelular, String nombreMascota, LatLng mascotaLatLng) {

        Intent showPetFoundIntent = new Intent(this, MascotaEncontradaReceiver.class);
        showPetFoundIntent.putExtra("idCliente", idCliente);
        showPetFoundIntent.putExtra("nombreMascota", nombreMascota);
        showPetFoundIntent.putExtra("mascotaLat", mascotaLatLng.latitude);
        showPetFoundIntent.putExtra("mascotaLng", mascotaLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, showPetFoundIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action showPetFoundAction= new Notification.Action.Builder(
                R.drawable.ic_app,
                "Mostrar en mapa",
                viewPendingIntent
        ).build();

        Intent contactoIntent = new Intent(this, ContactoReceiver.class);
        contactoIntent.putExtra("numeroCelular", numeroCelular);
        PendingIntent contactoPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, contactoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action contactAction = new Notification.Action.Builder(
                R.drawable.ic_app,
                "Contactar",
                contactoPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CanalNotificacion canalNotificacion = new CanalNotificacion(getBaseContext());
        Notification.Builder builder = canalNotificacion.getNotificationPetFound(title, body, sound, showPetFoundAction, contactAction);
        canalNotificacion.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void mostrarNotificacionApiOreoMascotaDesaparecida(String title, String body, int idMascota, String nombreMascota, LatLng mascotaLatLng) {

        Intent mascotaDesaparecidaIntent = new Intent(this, MascotaDesaparecidaReceiver.class);
        mascotaDesaparecidaIntent.putExtra("idMascota", idMascota);
        mascotaDesaparecidaIntent.putExtra("nombreMascota", nombreMascota);
        mascotaDesaparecidaIntent.putExtra("mascotaLat", mascotaLatLng.latitude);
        mascotaDesaparecidaIntent.putExtra("mascotaLng", mascotaLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, mascotaDesaparecidaIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action showPetMissingAction= new Notification.Action.Builder(
                R.drawable.ic_app,
                "Mostrar en mapa",
                viewPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CanalNotificacion canalNotificacion = new CanalNotificacion(getBaseContext());
        Notification.Builder builder = canalNotificacion.getNotificationPetMissing(title, body, sound, showPetMissingAction);
        canalNotificacion.getManager().notify(1, builder.build());

    }
}

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
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");

        if (title != null) {

            if(title.contains("Mascota encontrada")){

                int  idCliente = Integer.valueOf(data.get("idCliente"));
                String nombreMascota = data.get("nombreMascota");
                String numeroCelular = data.get("numeroCelular");
                double mascotaLat = Double.parseDouble(data.get("mascotaLat"));
                double mascotaLng  = Double.parseDouble(data.get("mascotaLng"));

                LatLng petLatLng = new LatLng(mascotaLat, mascotaLng);
                mostrarNotificacionMascotaEncontrada(title, body, idCliente, numeroCelular, nombreMascota, petLatLng);
            }
            else {

                int idMascota = Integer.valueOf(data.get("idMascota"));
                String nombreMascota = data.get("nombreMascota");
                double mascotaLat = Double.parseDouble(data.get("mascotaLat"));
                double mascotaLng = Double.parseDouble(data.get("mascotaLng"));

                LatLng mascotaLatLng = new LatLng(mascotaLat, mascotaLng);
                mostrarNotificacionMascotaDesaparecida(title, body, idMascota, nombreMascota, mascotaLatLng);
            }
        }
    }

    private void mostrarNotificacionMascotaEncontrada(String titulo, String mensaje, int idCliente, String numeroCelular, String nombreMascota, LatLng mascotaLatLng) {

        /*
         SE CREA UN INTENT QUE INICIA UN BROADCAST RECEIVER, ESTE REALIZA UN TRABAJO EN SEGUNDO
         PLANO PARA QUE LA ACCION NO INTERRUMPA UNA APLICACION ABIERTA
         */
        Intent mascotaEncontradaIntent = new Intent(this, MascotaEncontradaReceiver.class);
        mascotaEncontradaIntent.putExtra("idCliente", idCliente);
        mascotaEncontradaIntent.putExtra("nombreMascota", nombreMascota);
        mascotaEncontradaIntent.putExtra("mascotaLat", mascotaLatLng.latitude);
        mascotaEncontradaIntent.putExtra("mascotaLng", mascotaLatLng.longitude);
        //SE CREA UN PENDING INTENT QUE ENCAPSULA UN INTENT, EL CUAL SERA LANZADO AL OPRIMIR UNA OPCION EN LA NOTIFICACION
        //LA BANDERA UPDATE CURRENT INDICA QUE, SI EL PENDING INTENT YA EXISTE, SE CONSERVE PERO REEMPLAZE EL INTENT QUE CONTIENE.
        PendingIntent mascotaEncontradaPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, mascotaEncontradaIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent contactoIntent = new Intent(this, ContactoReceiver.class);
        contactoIntent.putExtra("numeroCelular", numeroCelular);
        PendingIntent contactoPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, contactoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CanalNotificacion canalNotificacion = new CanalNotificacion(getBaseContext());
        NotificationCompat.Builder notificacion = canalNotificacion.crearNotificationMascotaEncontrada(titulo, mensaje, sonido, mascotaEncontradaPendingIntent, contactoPendingIntent);
        //SE MUESTRA LA NOTIFICACION, RECIBE UN ID EL CUAL SIRVE PARA ACTUALIZAR O ELIMINAR LA NOTIFICACION POSTERIORMENTE
        canalNotificacion.getManager().notify(0, notificacion.build());
    }

    private void mostrarNotificacionMascotaDesaparecida(String titulo, String mensaje, int idMascota, String nombreMascota, LatLng mascotaLatLng) {

        Intent mostrarMascotaDesaparecidaIntent = new Intent(this, MascotaDesaparecidaReceiver.class);
        mostrarMascotaDesaparecidaIntent.putExtra("idMascota", idMascota);
        mostrarMascotaDesaparecidaIntent.putExtra("nombreMascota", nombreMascota);
        mostrarMascotaDesaparecidaIntent.putExtra("mascotaLat", mascotaLatLng.latitude);
        mostrarMascotaDesaparecidaIntent.putExtra("mascotaLng", mascotaLatLng.longitude);
        PendingIntent mascotaEncontradaPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, mostrarMascotaDesaparecidaIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CanalNotificacion canalNotificacion = new CanalNotificacion(getBaseContext());
        NotificationCompat.Builder notificacion = canalNotificacion.crearNotificationMascotaDesaparecida(titulo, mensaje, sonido, mascotaEncontradaPendingIntent );
        canalNotificacion.getManager().notify(1, notificacion.build());
    }

}

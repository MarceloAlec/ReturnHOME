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

public class CanalNotificacion extends ContextWrapper {

    private static final String CHANNEL_ID = "com.returnhome";
    private static final String CHANNEL_NAME = "ReturnHOME";

    private NotificationManager manager;

    public CanalNotificacion(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*
            ANTES DE ENVIAR NOTIFICACIONES SE DEBE CREAR UN CANAL DE NOTIFICACIONES DE LA
            APLICACION PARA ENVIAR NOTIFICACIONES EN ANDROID 8.0 Y VERSIONES POSTERIORES
             */
            crearCanal();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearCanal(){

        /*
        EL CONTRUCTOR DE NOTIFICATION CHANNEL REQUIERE DE UN IMPORTANCE, ESTE PARAMETRO INDICA
        COMO INTERRUMPIR AL USUARIO PARA CUALQUIER NOTIFICACION QUE PERTENEZCA AL CANAL
         */
        NotificationChannel notificationChannel = new
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        //PROPIEDADES DEL CANAL QUE SE ESTABLECEN A TODAS LAS NOTIFICACIONES QUE PERTENEZCAN A ESTE
        notificationChannel.enableLights(true);
        //LAS NOTIFICACIONES DE ESTE CANAL VIBRARÁN
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        //LAS NOTIFCACIONES DE ESTE CANAL NO APARECEN EN LA PANTALLA DE BLOQUEO
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        //SE REGISTRA EL CANAL CON EL SISTEMA
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder crearNotificationMascotaEncontrada(String title, String body, Uri soundUri, PendingIntent mascotaEncontrada, PendingIntent contacto) {

        //SE CONFIGURA LA NOTIFICACION MEDIANTE LA CLASE NOTIFICATIONCOMPAT.BUILDER
        //RECIBE COMO PARAMETRO EL CONTEXTO DE LA APLICACION Y UN IDENTIFICADOR DE CANAL
        //EL ID DE CANAL ES NECESARIO PARA LAS VERSIONES 8.0 Y SUPERIORES, LAS VERSIONES ANTERIORES LO IGNORAN
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                //SE ESPECIFICA EL NIVEL DE INTRUSION DE LA NOTIFICACION PARA LA VERSION 7.1 Y ANTERIORES
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                //SE ESTABLECE LA HORA EN QUE OCURRE LA NOTIFICACION
                .setShowWhen(true)
                //ICONO QUE IDENTIFICA A QUE APP PERTENECE LA NOTIFICACION
                .setSmallIcon(R.drawable.ic_app_notificacion)
                //SE ESTABLECEN LAS ACCIONES QUE CONTENDRA LA NOTIFICACION
                .addAction( R.drawable.ic_app_notificacion, "Mostrar en mapa", mascotaEncontrada)
                .addAction(  R.drawable.ic_app_notificacion, "Contactar", contacto)
                //SE AJUSTA LA NOTIFICACION PARA QUE ABARQUE EL MENSAJE POR COMPLETO
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

    public NotificationCompat.Builder crearNotificationMascotaDesaparecida(String title, String body, Uri soundUri, PendingIntent mascotaDesaparecida) {

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_app_notificacion)
                .addAction( R.drawable.ic_app_notificacion, "Mostrar en mapa", mascotaDesaparecida)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

}

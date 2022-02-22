package com.returnhome.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.returnhome.ui.activities.mascota.MapaMascotaDesaparecidaActivity;

public class MascotaDesaparecidaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int idMascota = intent.getExtras().getInt("idMascota");
        String nombreMascota = intent.getExtras().getString("nombreMascota");
        double mascotaLat = intent.getExtras().getDouble("mascotaLat");
        double mascotaLng = intent.getExtras().getDouble("mascotaLng");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);

        Intent intent1 = new Intent(context, MapaMascotaDesaparecidaActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idMascota", idMascota);
        intent1.putExtra("nombreMascota", nombreMascota);
        intent1.putExtra("mascotaLat", mascotaLat);
        intent1.putExtra("mascotaLng", mascotaLng);
        context.startActivity(intent1);
    }
}

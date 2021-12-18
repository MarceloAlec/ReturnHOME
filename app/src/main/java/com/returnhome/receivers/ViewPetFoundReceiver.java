package com.returnhome.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.returnhome.ui.activities.pet.MapPetFoundActivity;

public class ViewPetFoundReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int idPet = intent.getExtras().getInt("idPet");
        String petName = intent.getExtras().getString("pet_name");
        double petLat = intent.getExtras().getDouble("pet_lat");
        double petLng = intent.getExtras().getDouble("pet_lng");
        boolean isMissing = intent.getExtras().getBoolean("isMissing");


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);

        Intent intent1 = new Intent(context, MapPetFoundActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idPet", idPet);
        intent1.putExtra("pet_name", petName);
        intent1.putExtra("pet_lat", petLat);
        intent1.putExtra("pet_lng", petLng);
        intent1.putExtra("isMissing", isMissing);
        context.startActivity(intent1);
    }
}

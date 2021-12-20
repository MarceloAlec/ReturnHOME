package com.returnhome.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.returnhome.ui.activities.pet.MapPetFoundActivity;

public class ContactReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String phoneNumber = intent.getExtras().getString("phoneNumber");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);

        Intent intent1 = new Intent(Intent.ACTION_DIAL);
        intent1.setData(Uri.parse("tel:" + phoneNumber));
        if (intent1.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent1);
        }

    }
}

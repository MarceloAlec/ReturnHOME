package com.returnhome.providers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.returnhome.services.FCMBody;
import com.returnhome.services.FCMResponse;
import com.returnhome.utils.retrofit.IFCMApi;
import com.returnhome.utils.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private static String BASE_URL = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public static Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(BASE_URL).create(IFCMApi.class).send(body);
    }

    public static void suscribeMissingPet() {
        FirebaseMessaging.getInstance().subscribeToTopic("missing-pets")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()) {
                            Log.d("Error", task.getException().toString());
                        }

                    }
                });
    }

    public static void unsuscribeMissingPet() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("missing-pets")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()) {
                            Log.d("Error", task.getException().toString());
                        }

                    }
                });
    }




}

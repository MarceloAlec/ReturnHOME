package com.returnhome.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.returnhome.models.FCMCuerpo;
import com.returnhome.models.FCMRespuesta;
import com.returnhome.utils.retrofit.IFCMApi;
import com.returnhome.utils.retrofit.ClienteRetrofit;

import retrofit2.Call;

public class NotificacionController {

    private static String BASE_URL = "https://fcm.googleapis.com";



    public static Call<FCMRespuesta> enviarNotificacion(FCMCuerpo body) {
        return ClienteRetrofit.getClient(BASE_URL).create(IFCMApi.class).send(body);
    }

    public static void suscribirMascotaDesaparecida() {
        FirebaseMessaging.getInstance().subscribeToTopic("mascotas-desaparecidas")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()) {
                            Log.d("Error", task.getException().toString());
                        }

                    }
                });
    }

    public static void desuscribirMascotaDesaparecida() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("mascotas-desaparecidas")
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

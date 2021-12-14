package com.returnhome.providers;

import com.returnhome.models.FCMBody;
import com.returnhome.models.FCMResponse;
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
}

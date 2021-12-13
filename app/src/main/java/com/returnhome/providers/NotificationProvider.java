package com.returnhome.providers;

import com.returnhome.models.FCMBody;
import com.returnhome.models.FCMResponse;
import com.returnhome.utils.retrofit.IFCMApi;
import com.returnhome.utils.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClientForFCM(url).create(IFCMApi.class).send(body);
    }
}

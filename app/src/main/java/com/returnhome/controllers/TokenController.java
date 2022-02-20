package com.returnhome.controllers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.returnhome.models.RHRespuesta;
import com.returnhome.utils.retrofit.ClienteRetrofit;
import com.returnhome.utils.retrofit.IClienteApi;

import java.util.Map;

import retrofit2.Call;

public class TokenController {

    private static final String BASE_URL = "http://192.168.0.5:82/api.returnhome.com/v1/";

    public static Call<RHRespuesta> actualizar(Map<String,String> tokenInfo){
        return ClienteRetrofit.getClient(BASE_URL).create(IClienteApi.class).updateToken(tokenInfo);
    }

    public static Task<Void> eliminar(){
        return FirebaseMessaging.getInstance().deleteToken();
    }

    public static Task<String> crearToken(){

        return FirebaseMessaging.getInstance().getToken();
    }
}

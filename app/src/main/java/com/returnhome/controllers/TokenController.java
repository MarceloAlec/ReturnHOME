package com.returnhome.controllers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.returnhome.models.RHRespuesta;
import com.returnhome.utils.retrofit.ClienteRetrofit;
import com.returnhome.utils.retrofit.IClienteApi;
import com.returnhome.utils.retrofit.ITokenApi;

import java.util.Map;

import retrofit2.Call;

public class TokenController {

    private static final String BASE_URL = "http://192.168.0.2:82/api.returnhome.com/v1/";

    public static Call<Void> registrarTokenDB(Map<String,String> tokenInfo){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(ITokenApi.class).registrar(tokenInfo);
    }

    public static Call<Void> eliminarTokenDB(Map<String,String> tokenInfo){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(ITokenApi.class).eliminar(tokenInfo);
    }

    public static Task<Void> eliminar(){
        return FirebaseMessaging.getInstance().deleteToken();
    }

    public static Task<String> crearToken(){

        return FirebaseMessaging.getInstance().getToken();
    }
}

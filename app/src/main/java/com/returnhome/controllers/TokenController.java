package com.returnhome.controllers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.returnhome.utils.retrofit.RHRespuesta;
import com.returnhome.utils.retrofit.ClienteRetrofit;
import com.returnhome.utils.retrofit.ITokenApi;

import java.util.Map;

import retrofit2.Call;

public class TokenController {

    private static final String BASE_URL = "http://192.168.0.4:82/api.returnhome.com/v1/";

    //SE EJECUTAN LAS LLAMADAS AL SERVICIO WEB CON SUS RESPECTIVOS METODOS HTTP

    public static Call<Void> registrarTokenDB(Map<String,String> tokenInfo){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(ITokenApi.class).registrar(tokenInfo);
    }

    public static Call<RHRespuesta> obtener(int idCliente){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(ITokenApi.class).obtener(idCliente);
    }

    public static Task<Void> eliminarToken(){
        return FirebaseMessaging.getInstance().deleteToken();
    }


    /*
        PARA RECIBIR Y ENVIAR MENSAJES SE OBTIENE UN TOKEN DE REGISTRO QUE IDENTIFICA DE FORMA UNICA
        LA INSTANCIA DE LA APLICACIÓN
        REGISTRO EL DISPOSITIVO PARA RECIBIR MENSAJES DE FCM
     */

    public static Task<String> obtenerToken(){

        return FirebaseMessaging.getInstance().getToken();
    }
}

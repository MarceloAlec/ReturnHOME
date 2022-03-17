package com.returnhome.utils.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ClienteRetrofit {


    public static Retrofit obtenerCliente(String url){

        //INSTANCIO EL OBJETO RETROFIT A LA ESPERA DE REALIZAR UNA PETICION
        Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
        return retrofit;
    }

}

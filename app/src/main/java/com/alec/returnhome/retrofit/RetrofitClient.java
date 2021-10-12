package com.alec.returnhome.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    private static Retrofit retrofit;

    //URL DONDE SE REALIZA LA PETICION
    public static Retrofit getClient(String url){
        retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

        return retrofit;
    }


}

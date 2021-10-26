package com.returnhome.providers;

import android.content.Context;

import com.returnhome.utils.retrofit.IPetApi;
import com.returnhome.utils.retrofit.ResponseApi;
import com.returnhome.utils.retrofit.RetrofitClient;


import retrofit2.Call;


public class PetProvider  {


    private Context context;
    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private final String BASE_URL = "http://192.168.0.7:82/api.returnhome.com/v1/controllers/pet/";


    public PetProvider(Context context){
        this.context = context;
    }


    public Call<ResponseApi> getPets(int idClient){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).read(idClient);
    }












}

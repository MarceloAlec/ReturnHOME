package com.alec.returnhome.providers;

import android.content.Context;

import com.alec.returnhome.models.Pet;
import com.alec.returnhome.retrofit.IPetApi;
import com.alec.returnhome.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class PetProvider {

    private Context context;
    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private String baseUrl = "http://192.168.0.3:82/api.returnhome.com/v1/";

    public PetProvider(Context context){
        this.context = context;

    }

    //RETORNA UN CALL DEL TIPO STRING
    public Call<List<Pet>> getPets(int idClient){

        return RetrofitClient.getClient(baseUrl).create(IPetApi.class).read(idClient);
    }
}

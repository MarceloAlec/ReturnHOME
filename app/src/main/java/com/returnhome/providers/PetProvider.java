package com.returnhome.providers;

import android.content.Context;

import com.returnhome.models.Pet;
import com.returnhome.utils.retrofit.IPetApi;
import com.returnhome.models.RHResponse;
import com.returnhome.utils.retrofit.RetrofitClient;


import retrofit2.Call;


public class PetProvider  {


    private Context context;
    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private final String BASE_URL = "http://192.168.0.5:82/api.returnhome.com/v1/controllers/pet/";


    public PetProvider(Context context){
        this.context = context;
    }


    public Call<RHResponse> readPet(int id, boolean byIdClient){

        return RetrofitClient.getClientForRH(BASE_URL).create(IPetApi.class).read(id, byIdClient);
    }

    public Call<RHResponse> deletePet(int idPet){

        return RetrofitClient.getClientForRH(BASE_URL).create(IPetApi.class).delete(idPet);
    }

    public Call<RHResponse> updatePet(Pet pet){

        return RetrofitClient.getClientForRH(BASE_URL).create(IPetApi.class).update(pet);
    }

    public Call<RHResponse> createPet(Pet pet){

        return RetrofitClient.getClientForRH(BASE_URL).create(IPetApi.class).create(pet);
    }












}

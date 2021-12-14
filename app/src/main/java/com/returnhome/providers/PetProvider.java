package com.returnhome.providers;

import android.content.Context;

import com.returnhome.models.Pet;
import com.returnhome.utils.retrofit.IPetApi;
import com.returnhome.models.RHResponse;
import com.returnhome.utils.retrofit.RetrofitClient;


import retrofit2.Call;


public class PetProvider  {

    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private static final String BASE_URL = "http://192.168.0.5:82/api.returnhome.com/v1/controllers/pet/";

    public static Call<RHResponse> readPet(int id, boolean byIdClient){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).read(id, byIdClient);
    }

    public static Call<RHResponse> deletePet(int idPet){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).delete(idPet);
    }

    public static Call<RHResponse> updatePet(Pet pet){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).update(pet);
    }

    public static Call<RHResponse> createPet(Pet pet){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).create(pet);
    }












}

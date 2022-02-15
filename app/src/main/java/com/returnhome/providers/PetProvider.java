package com.returnhome.providers;

import com.returnhome.modelos.Mascota;
import com.returnhome.utils.retrofit.IPetApi;
import com.returnhome.utils.retrofit.RHResponse;
import com.returnhome.utils.retrofit.RetrofitClient;


import retrofit2.Call;


public class PetProvider  {

    private static final String BASE_URL = "http://192.168.0.5:82/api.returnhome.com/v1/controllers/pet/";

    public static Call<RHResponse> readPet(int id, int action){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).read(id, action);
    }

    public static Call<RHResponse> deletePet(int idPet){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).delete(idPet);
    }

    public static Call<RHResponse> updatePet(Mascota mascota){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).update(mascota);
    }

    public static Call<RHResponse> updateStatusMissingPet(Mascota mascota){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).updateStatusMissing(mascota);
    }

    public static Call<RHResponse> createPet(Mascota mascota){

        return RetrofitClient.getClient(BASE_URL).create(IPetApi.class).create(mascota);
    }












}

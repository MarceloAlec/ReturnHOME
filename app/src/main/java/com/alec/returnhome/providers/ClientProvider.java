package com.alec.returnhome.providers;

import android.content.Context;

import com.alec.returnhome.models.ApiResponse;
import com.alec.returnhome.models.Client;
import com.alec.returnhome.utils.retrofit.IClientApi;
import com.alec.returnhome.utils.retrofit.RetrofitClient;

import java.util.HashMap;

import retrofit2.Call;

public class ClientProvider {

    private Context context;
    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private final String BASE_URL = "http://192.168.0.3:82/api.returnhome.com/v1/controllers/client/";


    public ClientProvider(Context context){
        this.context = context;

    }


    public Call<ApiResponse> registerClient(Client client){

        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).create(client);
    }

    public Call<ApiResponse> authClient(HashMap<String,String> auth){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).authClient(auth);
    }

}

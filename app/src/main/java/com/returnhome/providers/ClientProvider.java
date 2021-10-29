package com.returnhome.providers;

import android.content.Context;

import com.returnhome.models.Pet;
import com.returnhome.utils.retrofit.IPetApi;
import com.returnhome.utils.retrofit.ResponseApi;
import com.returnhome.models.Client;
import com.returnhome.utils.retrofit.IClientApi;
import com.returnhome.utils.retrofit.RetrofitClient;

import java.util.HashMap;

import retrofit2.Call;

public class ClientProvider {

    private Context context;
    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private final String BASE_URL = "http://192.168.0.7:82/api.returnhome.com/v1/controllers/client/";


    public ClientProvider(Context context){
        this.context = context;

    }


    public Call<ResponseApi> registerClient(Client client){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).create(client);
    }

    public Call<ResponseApi> authClient(HashMap<String,String> auth){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).authClient(auth);
    }

    public Call<ResponseApi> updateClient(Client client){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).update(client);
    }

}

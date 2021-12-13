package com.returnhome.providers;

import android.content.Context;

import com.returnhome.models.RHResponse;
import com.returnhome.models.Client;
import com.returnhome.utils.retrofit.IClientApi;
import com.returnhome.utils.retrofit.RetrofitClient;

import java.util.Map;

import retrofit2.Call;

public class ClientProvider {

    private Context context;
    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private final String BASE_URL = "http://192.168.0.5:82/api.returnhome.com/v1/controllers/client/";


    public ClientProvider(Context context){
        this.context = context;
    }

    public Call<RHResponse> registerClient(Client client){
        return RetrofitClient.getClientForRH(BASE_URL).create(IClientApi.class).create(client);
    }

    public Call<RHResponse> getClient(int idClient){
        return RetrofitClient.getClientForRH(BASE_URL).create(IClientApi.class).read(idClient);
    }

    public Call<RHResponse> authClient(Map<String,String> auth){
        return RetrofitClient.getClientForRH(BASE_URL).create(IClientApi.class).authClient(auth);
    }

    public Call<RHResponse> updateClient(Client client){
        return RetrofitClient.getClientForRH(BASE_URL).create(IClientApi.class).update(client);
    }

    public Call<RHResponse> updateClient(Map<String, String> password){
        return RetrofitClient.getClientForRH(BASE_URL).create(IClientApi.class).update(password);
    }

    public Call<RHResponse> deleteAccount(int idClient){
        return RetrofitClient.getClientForRH(BASE_URL).create(IClientApi.class).delete(idClient);
    }



}

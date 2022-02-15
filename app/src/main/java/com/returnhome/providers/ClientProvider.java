package com.returnhome.providers;

import com.returnhome.utils.retrofit.RHResponse;
import com.returnhome.modelos.Cliente;
import com.returnhome.utils.retrofit.IClientApi;
import com.returnhome.utils.retrofit.RetrofitClient;

import java.util.Map;

import retrofit2.Call;

public class ClientProvider {


    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private static final String BASE_URL = "http://192.168.0.5:82/api.returnhome.com/v1/controllers/client/";


    public static Call<RHResponse> registerClient(Cliente cliente){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).create(cliente);
    }

    public static Call<RHResponse> getClient(int idClient){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).read(idClient);
    }

    public static Call<RHResponse> authClient(Map<String,String> auth){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).authClient(auth);
    }

    public static Call<RHResponse> updateClient(Cliente cliente){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).update(cliente);
    }

    public static Call<RHResponse> updatePassword(Map<String, String> password){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).update(password);
    }

    public static Call<RHResponse> updateToken(Map<String,String> tokenInfo){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).updateToken(tokenInfo);
    }

    public static Call<RHResponse> deleteAccount(int idClient){
        return RetrofitClient.getClient(BASE_URL).create(IClientApi.class).delete(idClient);
    }



}

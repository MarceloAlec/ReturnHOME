package com.alec.returnhome.providers;

import android.content.Context;

import com.alec.returnhome.models.Client;
import com.alec.returnhome.retrofit.IClientApi;
import com.alec.returnhome.retrofit.RetrofitClient;

import org.json.JSONArray;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ClientProvider {

    private Context context;
    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private String baseUrl = "http://192.168.0.3:82/api.returnhome.com/v1/";


    public ClientProvider(Context context){
        this.context = context;

    }

    //RETORNA UN CALL DEL TIPO STRING
    public Call<ResponseBody> registerClient(Client client){

        return RetrofitClient.getClient(baseUrl).create(IClientApi.class).register(client);
    }

}

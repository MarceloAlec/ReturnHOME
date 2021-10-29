package com.returnhome.utils.retrofit;

import com.returnhome.models.Client;
import com.returnhome.models.Pet;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface IClientApi {

    @Headers({
            "Content-Type:application/json"
    })

    @POST("create.php")
    Call<ResponseApi> create(@Body Client client);

    @POST("auth.php")
    Call<ResponseApi> authClient(@Body HashMap<String,String> auth);

    @PUT("update.php")
    Call<ResponseApi> update(@Body Client client);
}

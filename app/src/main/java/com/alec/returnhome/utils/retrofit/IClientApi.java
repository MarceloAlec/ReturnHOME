package com.alec.returnhome.utils.retrofit;

import com.alec.returnhome.models.ApiResponse;
import com.alec.returnhome.models.Client;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IClientApi {

    @Headers({
            "Content-Type:application/json"
    })

    @POST("create.php")
    Call<ApiResponse> create(@Body Client client);

    @POST("auth.php")
    Call<ApiResponse> authClient(@Body HashMap<String,String> auth);
}

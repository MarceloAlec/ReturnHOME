package com.alec.returnhome.retrofit;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface IPetApi {

    @Headers({
            "Content-Type:application/json"
    })

    @GET("pet/read.php")
    Call<String> read(@Query("id") int idClient);
}

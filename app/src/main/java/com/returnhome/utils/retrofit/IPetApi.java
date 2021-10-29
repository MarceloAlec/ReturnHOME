package com.returnhome.utils.retrofit;


import com.returnhome.models.Pet;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface IPetApi {

    @Headers({
            "Content-Type:application/json"
    })

    @POST("create.php")
    Call<ResponseApi> create(@Body Pet pet);

    @GET("read.php")
    Call<ResponseApi> read(@Query("id") int idClient);

    @PUT("update.php")
    Call<ResponseApi> update(@Body Pet pet);

    @DELETE("delete.php")
    Call<ResponseApi> delete(@Query("id") int idClient);



}

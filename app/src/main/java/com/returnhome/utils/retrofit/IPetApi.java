package com.returnhome.utils.retrofit;


import com.returnhome.models.Pet;
import com.returnhome.models.RHResponse;

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
    Call<RHResponse> create(@Body Pet pet);

    @GET("read.php")
    Call<RHResponse> read(@Query("id") int id, @Query("byIdClient") boolean byIdClient);

    @PUT("update.php")
    Call<RHResponse> update(@Body Pet pet);

    @DELETE("delete.php")
    Call<RHResponse> delete(@Query("id") int idPet);


}

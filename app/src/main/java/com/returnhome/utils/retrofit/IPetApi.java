package com.returnhome.utils.retrofit;


import com.returnhome.modelos.Mascota;

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
    Call<RHResponse> create(@Body Mascota mascota);

    @GET("read.php")
    Call<RHResponse> read(@Query("id") int id, @Query("action") int action);

    @PUT("update.php")
    Call<RHResponse> update(@Body Mascota mascota);

    @PUT("update-status-missing.php")
    Call<RHResponse> updateStatusMissing(@Body Mascota mascota);

    @DELETE("delete.php")
    Call<RHResponse> delete(@Query("id") int idPet);


}

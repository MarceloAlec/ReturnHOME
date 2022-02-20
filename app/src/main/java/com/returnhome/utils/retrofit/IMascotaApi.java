package com.returnhome.utils.retrofit;


import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface IMascotaApi {

    @Headers({
            "Content-Type:application/json"
    })

    @POST("create.php")
    Call<RHRespuesta> create(@Body Mascota mascota);

    @GET("read.php")
    Call<RHRespuesta> read(@Query("id") int id, @Query("action") int action);

    @PUT("update.php")
    Call<RHRespuesta> update(@Body Mascota mascota);

    @PUT("update-status-missing.php")
    Call<RHRespuesta> updateStatusMissing(@Body Mascota mascota);

    @DELETE("delete.php")
    Call<RHRespuesta> delete(@Query("id") int idPet);


}

package com.returnhome.utils.retrofit;


import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface IPetApi {

    @Headers({
            "Content-Type:application/json"
    })

    @GET("read.php")
    Call<ResponseApi> read(@Query("id") int idClient);

    @DELETE("delete.php")
    Call<ResponseApi> delete(@Query("id") int idClient);

}

package com.returnhome.utils.retrofit;

import com.returnhome.models.Client;
import com.returnhome.models.Pet;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface IClientApi {

    @Headers({
            "Content-Type:application/json"
    })

    @POST("create.php")
    Call<ResponseApi> create(@Body Client client);

    @GET("read.php")
    Call<ResponseApi> read(@Query("id") int idClient);

    @POST("auth.php")
    Call<ResponseApi> authClient(@Body Map<String,String> auth);

    @PUT("update-profile.php")
    Call<ResponseApi> update(@Body Client client);

    @PUT("change-password.php")
    Call<ResponseApi> update(@Body Map<String, String> password);

    @DELETE("delete.php")
    Call<ResponseApi> delete(@Query("id") int idClient);


}

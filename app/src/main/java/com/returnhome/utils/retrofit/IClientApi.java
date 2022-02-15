package com.returnhome.utils.retrofit;

import com.returnhome.modelos.Cliente;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    Call<RHResponse> create(@Body Cliente cliente);

    @GET("read.php")
    Call<RHResponse> read(@Query("id") int idClient);

    @POST("auth.php")
    Call<RHResponse> authClient(@Body Map<String,String> auth);

    @PUT("update-profile.php")
    Call<RHResponse> update(@Body Cliente cliente);

    @PUT("update-token.php")
    Call<RHResponse> updateToken(@Body Map<String,String> tokenInfo);

    @PUT("update-password.php")
    Call<RHResponse> update(@Body Map<String, String> password);

    @DELETE("delete.php")
    Call<RHResponse> delete(@Query("id") int idClient);


}

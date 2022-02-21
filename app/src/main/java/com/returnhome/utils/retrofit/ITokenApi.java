package com.returnhome.utils.retrofit;

import com.returnhome.models.Cliente;
import com.returnhome.models.RHRespuesta;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ITokenApi {

    @Headers({
            "Content-Type:application/json"
    })

    @PUT("ws_registrarToken.php")
    Call<Void> registrar(@Body Map<String,String> tokenInfo);

    @DELETE("ws_eliminarToken.php")
    Call<Void> eliminar(@Body Map<String,String> tokenInfo);

}

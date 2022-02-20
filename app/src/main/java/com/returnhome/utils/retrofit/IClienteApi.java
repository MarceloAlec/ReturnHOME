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

public interface IClienteApi {

    @Headers({
            "Content-Type:application/json"
    })

    @POST("ws_registrarCliente.php")
    Call<RHRespuesta> registrar(@Body Cliente cliente);

    @GET("ws_obtenerCliente.php")
    Call<RHRespuesta> obtener(@Query("id") int idClient);

    @POST("ws_autenticarCliente.php")
    Call<RHRespuesta> autenticar(@Body Map<String,String> auth);

    @PUT("ws_actualizarCliente.php")
    Call<RHRespuesta> actualizar(@Body Cliente cliente);

    @PUT("update-token.php")
    Call<RHRespuesta> updateToken(@Body Map<String,String> tokenInfo);

    @PUT("ws_actualizarPassword.php")
    Call<RHRespuesta> actualizarPassword(@Body Map<String, String> password);

    @DELETE("ws_eliminarCliente.php")
    Call<RHRespuesta> eliminar(@Query("id") int idClient);


}

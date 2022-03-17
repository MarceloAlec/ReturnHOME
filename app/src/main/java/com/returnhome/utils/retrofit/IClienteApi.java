package com.returnhome.utils.retrofit;

import com.returnhome.models.Cliente;

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

    //ENDPOINTS
    //SE DEFINE LOS METODOS QUE GENERAN LAS PETICIONES HTTP

    //EN LA CLASE CALL SE ESPECIFICA LA RESPUESTA QUE TENDRA CADA PETICION
    //SE GENERA UN MODELO DE RESPUESTA DENOMINADO RHRESPUESTA SI LA API DEVUELVE INFORMACION
    //DE LO CONTRARIO, SE INDICA CON VOID QUE ESA PETICION NO TENDRA RESPUESTA EN SU CUERPO.

    @POST("ws_registrarCliente.php")
    Call<RHRespuesta> registrar(@Body Cliente cliente);

    @GET("ws_obtenerCliente.php")
    Call<RHRespuesta> obtener(@Query("idCliente") int idCliente);

    @POST("ws_autenticarCliente.php")
    Call<RHRespuesta> autenticar(@Body Map<String,String> auth);

    @PUT("ws_actualizarCliente.php")
    Call<Void> actualizar(@Body Cliente cliente);

    @PUT("ws_actualizarPassword.php")
    Call<Void> actualizarPassword(@Body Map<String, String> password);

    @DELETE("ws_eliminarCliente.php")
    Call<Void> eliminar(@Query("idCliente") int idCliente);


}

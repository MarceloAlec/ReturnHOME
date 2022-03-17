package com.returnhome.utils.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ITokenApi {

    @Headers({
            "Content-Type:application/json"
    })

    //ENDPOINTS
    //SE DEFINE LOS METODOS QUE GENERAN LAS PETICIONES HTTP

    //EN LA CLASE CALL SE ESPECIFICA LA RESPUESTA QUE TENDRA CADA PETICION
    //SE GENERA UN MODELO DE RESPUESTA DENOMINADO RHRESPUESTA SI LA API DEVUELVE INFORMACION
    //DE LO CONTRARIO, SE INDICA CON VOID QUE ESA PETICION NO TENDRA RESPUESTA EN SU CUERPO.

    @PUT("ws_registrarToken.php")
    Call<Void> registrar(@Body Map<String,String> tokenInfo);

    @GET("ws_obtenerToken.php")
    Call<RHRespuesta> obtener(@Query("idCliente") int idCliente);

    @HTTP(method = "DELETE", path = "ws_eliminarToken.php", hasBody = true)
    Call<Void> eliminar(@Body Map<String,String> tokenInfo);

}

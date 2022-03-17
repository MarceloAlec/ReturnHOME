package com.returnhome.utils.retrofit;


import com.returnhome.models.Mascota;

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

    //ENDPOINTS
    //SE DEFINE LOS METODOS QUE GENERAN LAS PETICIONES HTTP

    //EN LA CLASE CALL SE ESPECIFICA LA RESPUESTA QUE TENDRA CADA PETICION
    //SE GENERA UN MODELO DE RESPUESTA DENOMINADO RHRESPUESTA SI LA API DEVUELVE INFORMACION
    //DE LO CONTRARIO, SE INDICA CON VOID QUE ESA PETICION NO TENDRA RESPUESTA EN SU CUERPO.

    @POST("ws_registrarMascota.php")
    Call<RHRespuesta> registrar(@Body Mascota mascota);

    @GET("ws_obtenerMascota.php")
    Call<RHRespuesta> obtener(@Query("id") int id, @Query("opcion") int opcion);

    @PUT("ws_actualizarMascota.php")
    Call<Void> actualizar(@Body Mascota mascota);

    @PUT("ws_actualizarMascotaDesaparecida.php")
    Call<Void> actualizarEstadoDesaparecida(@Body Mascota mascota);

    @DELETE("ws_eliminarMascota.php")
    Call<Void> eliminar(@Query("idMascota") int idMascota);


}

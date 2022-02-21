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

    @POST("ws_registrarMascota.php")
    Call<RHRespuesta> registrar(@Body Mascota mascota);

    @GET("ws_obtenerMascota.php")
    Call<RHRespuesta> obtener(@Query("id") int id, @Query("opcion") int opcion);

    @PUT("ws_actualizarMascota.php")
    Call<RHRespuesta> actualizar(@Body Mascota mascota);

    @PUT("ws_actualizarMascotaDesaparecida.php")
    Call<RHRespuesta> actualizarEstadoDesaparecida(@Body Mascota mascota);

    @DELETE("ws_eliminarMascota.php")
    Call<RHRespuesta> eliminar(@Query("idMascota") int idMascota);


}

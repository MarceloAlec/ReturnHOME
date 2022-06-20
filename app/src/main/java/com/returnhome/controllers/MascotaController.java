package com.returnhome.controllers;

import com.returnhome.models.Mascota;
import com.returnhome.utils.retrofit.RHRespuesta;
import com.returnhome.utils.retrofit.ClienteRetrofit;
import com.returnhome.utils.retrofit.IMascotaApi;

import retrofit2.Call;

public class MascotaController {

    private static final String BASE_URL = "http://192.168.0.4:82/api.returnhome.com/v1/";

    //SE EJECUTAN LAS LLAMADAS AL SERVICIO WEB CON SUS RESPECTIVOS METODOS HTTP

    public static Call<RHRespuesta> obtener(int id, int opcion){

        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IMascotaApi.class).obtener(id, opcion);
    }

    public static Call<Void> eliminar(int idPet){

        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IMascotaApi.class).eliminar(idPet);
    }

    public static Call<Void> actualizar(Mascota mascota){

        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IMascotaApi.class).actualizar(mascota);
    }

    public static Call<Void> actualizarMascotaDesaparecida(Mascota mascota){

        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IMascotaApi.class).actualizarEstadoDesaparecida(mascota);
    }

    public static Call<RHRespuesta> registrar(Mascota mascota){

        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IMascotaApi.class).registrar(mascota);
    }

}

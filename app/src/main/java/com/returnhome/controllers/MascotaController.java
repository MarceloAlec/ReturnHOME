package com.returnhome.controllers;

import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;
import com.returnhome.utils.retrofit.ClienteRetrofit;
import com.returnhome.utils.retrofit.IMascotaApi;

import retrofit2.Call;

public class MascotaController {

    private static final String BASE_URL = "http://192.168.0.5:82/api.returnhome.com/v1/";


    public static Call<RHRespuesta> obtener(int id, int opcion){

        return ClienteRetrofit.getClient(BASE_URL).create(IMascotaApi.class).read(id, opcion);
    }

    public static Call<RHRespuesta> eliminar(int idPet){

        return ClienteRetrofit.getClient(BASE_URL).create(IMascotaApi.class).delete(idPet);
    }

    public static Call<RHRespuesta> actualizar(Mascota mascota){

        return ClienteRetrofit.getClient(BASE_URL).create(IMascotaApi.class).update(mascota);
    }

    public static Call<RHRespuesta> actualizarMascotaDesaparecida(Mascota mascota){

        return ClienteRetrofit.getClient(BASE_URL).create(IMascotaApi.class).updateStatusMissing(mascota);
    }

    public static Call<RHRespuesta> registrar(Mascota mascota){

        return ClienteRetrofit.getClient(BASE_URL).create(IMascotaApi.class).create(mascota);
    }

}

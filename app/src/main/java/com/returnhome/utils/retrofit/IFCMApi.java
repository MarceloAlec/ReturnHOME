package com.returnhome.utils.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAQRXO5oY:APA91bHwbB8eWMklNgRi-qIk9BSYC5K3vbc7PlzQCSbwynyHa1KEuG3t1PdSAJfIl1mAhTEBiGe4ELi_pFsDafJ-9so1wnUB2CYNqwlWbjfNf1JvELmJCYfZ7iARC71l5vWblsNRFplj"
    })

    //ENDPOINT
    //SE DEFINE EL METODO QUE GENERA LA PETCIÓN HTTP

    //EN LA CLASE CALL SE ESPECIFICA LA RESPUESTA QUE TENDRA CADA PETICION
    //SE GENERA UN MODELO DE RESPUESTA DENOMINADO FCMRespuesta

    @POST("fcm/send")
    Call<FCMRespuesta> enviarNotificacion(@Body FCMCuerpo body);
}

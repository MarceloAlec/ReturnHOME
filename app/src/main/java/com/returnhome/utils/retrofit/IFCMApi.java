package com.returnhome.utils.retrofit;

import com.returnhome.models.FCMCuerpo;
import com.returnhome.models.FCMRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAQRXO5oY:APA91bHwbB8eWMklNgRi-qIk9BSYC5K3vbc7PlzQCSbwynyHa1KEuG3t1PdSAJfIl1mAhTEBiGe4ELi_pFsDafJ-9so1wnUB2CYNqwlWbjfNf1JvELmJCYfZ7iARC71l5vWblsNRFplj"
    })
    @POST("fcm/send")
    Call<FCMRespuesta> send(@Body FCMCuerpo body);
}

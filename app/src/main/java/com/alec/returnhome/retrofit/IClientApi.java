package com.alec.returnhome.retrofit;

import com.alec.returnhome.models.Client;

import org.json.JSONArray;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IClientApi {

    @Headers({
            "Content-Type:application/json"
    })

    @POST("returnhome/register.php")
    Call<ResponseBody> register(@Body Client client);
}

package com.alec.returnhome.retrofit;


import com.alec.returnhome.models.Pet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface IPetApi {

    @Headers({
            "Content-Type:application/json"
    })

    @GET("pet/read.php/{id}")
    Call<List<Pet>> read(@Path("id") int idClient);
}

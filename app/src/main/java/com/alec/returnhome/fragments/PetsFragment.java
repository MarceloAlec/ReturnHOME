package com.alec.returnhome.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alec.returnhome.R;
import com.alec.returnhome.models.Pet;
import com.alec.returnhome.providers.PetProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PetsFragment extends Fragment {

    PetProvider petProvider;
    RecyclerView mRecyclerViewPets;
    ArrayList<Pet> mPetList;
    PetProvider mPetProvider;
    int idClient=1;

    public PetsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pets, container, false);

        mPetProvider = new PetProvider(getContext());
        mRecyclerViewPets = view.findViewById(R.id.recyclerView);

        getPets(1);
       // showList();

        return view;
    }

    private void getPets(int idClient) {
        mPetProvider.getPets(idClient).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("pets");


                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Pet>>(){}.getType();
                    mPetList = gson.fromJson(String.valueOf(jsonArray), type);

                    showList();

                } catch (JSONException e) {
                    Log.d("Error", "Error encontrado" + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void showList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //MOSTRAR LOS VIEWS DE LA LISTA DE MANERA LINEAL
        mRecyclerViewPets.setLayoutManager(linearLayoutManager);
        //SE ENVIA LA LISTA DE MASCOTAS A PETPROVIDER
        petProvider = new PetProvider(getContext(), mPetList);
        mRecyclerViewPets.setAdapter(petProvider);
    }
}
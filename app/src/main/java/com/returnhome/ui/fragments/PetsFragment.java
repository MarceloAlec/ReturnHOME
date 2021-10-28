package com.returnhome.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.ui.activities.LoginActivity;
import com.returnhome.ui.adapters.PetAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.returnhome.utils.AppConfig;
import com.returnhome.utils.retrofit.ResponseApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PetsFragment extends Fragment implements View.OnClickListener {

    private PetAdapter mPetAdapter;
    private RecyclerView mRecyclerViewPets;
    private PetProvider mPetProvider;
    private AppConfig mAppConfig;
    private FloatingActionButton mButtonAdd;

    public PetsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pets, container, false);

        mPetProvider = new PetProvider(container.getContext());
        mRecyclerViewPets = view.findViewById(R.id.recyclerView);
        mAppConfig = new AppConfig(container.getContext());
        mButtonAdd = view.findViewById(R.id.fab_addPet);

        mButtonAdd.setOnClickListener(this);

        getPets();

        return view;
    }

    private void getPets() {
        int idClient = mAppConfig.getUserId();

        mPetProvider.getPets(idClient).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if(response.isSuccessful()){
                   showList(response.body().getPets());
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {

            }
        });
    }

    private void showList(ArrayList<Pet> pets) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //MOSTRAR LOS VIEWS DE LA LISTA DE MANERA LINEAL
        mRecyclerViewPets.setLayoutManager(linearLayoutManager);
        //SE ENVIA LA LISTA DE MASCOTAS A PETPROVIDER
        mPetAdapter = new PetAdapter(getContext(), pets);
        mRecyclerViewPets.setAdapter(mPetAdapter);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "Ingreso fallido", Toast.LENGTH_SHORT).show();
    }
}
package com.returnhome.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.returnhome.R;
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;
import com.returnhome.ui.adapters.MascotaDesaparecidaAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MascotaDesaparecidaFragment extends Fragment {


    private MascotaDesaparecidaAdapter mPetAdapter;
    private RecyclerView mRecyclerViewMissingPets;
    private ArrayList<Mascota> mascotaArrayList;

    public MascotaDesaparecidaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_pet, container, false);

        mascotaArrayList = new ArrayList<>();
        mRecyclerViewMissingPets = view.findViewById(R.id.recyclerViewMissingPets);

        return view;
    }

    private void getMissingPets() {

        MascotaController.obtener(0, 3).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    mascotaArrayList = response.body().getPets();
                    showList(mascotaArrayList);
                }
                else{
                    mRecyclerViewMissingPets.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {

            }
        });
    }

    private void showList(ArrayList<Mascota> mascotas) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerViewMissingPets.setLayoutManager(linearLayoutManager);
        mPetAdapter = new MascotaDesaparecidaAdapter(getContext(), mascotas);
        mRecyclerViewMissingPets.setAdapter(mPetAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        getMissingPets();
    }
}
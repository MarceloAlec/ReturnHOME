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


    private MascotaDesaparecidaAdapter mMascotaDesaparecidaAdapter;
    private RecyclerView mRecyclerViewMascotasDesaparecidas;
    private ArrayList<Mascota> mascotaArrayList;

    public MascotaDesaparecidaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mascota_desaparecida, container, false);

        mascotaArrayList = new ArrayList<>();
        mRecyclerViewMascotasDesaparecidas = view.findViewById(R.id.recyclerViewMascotasDesaparecidas);

        return view;
    }

    private void obtenerMascotasDesaparecidas() {

        MascotaController.obtener(0, 3).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    mascotaArrayList = response.body().getMascotas();
                    mostrarLista(mascotaArrayList);
                }
                else{
                    mRecyclerViewMascotasDesaparecidas.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {

            }
        });
    }

    private void mostrarLista(ArrayList<Mascota> mascotas) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerViewMascotasDesaparecidas.setLayoutManager(linearLayoutManager);
        mMascotaDesaparecidaAdapter = new MascotaDesaparecidaAdapter(getContext(), mascotas);
        mRecyclerViewMascotasDesaparecidas.setAdapter(mMascotaDesaparecidaAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerMascotasDesaparecidas();
    }
}
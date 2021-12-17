package com.returnhome.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.models.RHResponse;
import com.returnhome.providers.PetProvider;
import com.returnhome.ui.adapters.MissingPetAdapter;
import com.returnhome.ui.adapters.PetAdapter;
import com.returnhome.utils.AppConfig;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MissingPetFragment extends Fragment {


    private MissingPetAdapter mPetAdapter;
    private RecyclerView mRecyclerViewMissingPets;
    private ArrayList<Pet> petArrayList;

    public MissingPetFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missing_pet, container, false);

        petArrayList = new ArrayList<>();
        mRecyclerViewMissingPets = view.findViewById(R.id.recyclerViewMissingPets);

        return view;
    }

    private void getMissingPets() {


        PetProvider.readPet(0, 3).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                    petArrayList = response.body().getPets();
                    showList(petArrayList);
                }
                else{
                    mRecyclerViewMissingPets.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {

            }
        });
    }

    private void showList(ArrayList<Pet> pets) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerViewMissingPets.setLayoutManager(linearLayoutManager);
        mPetAdapter = new MissingPetAdapter(getContext(), pets);
        mRecyclerViewMissingPets.setAdapter(mPetAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        getMissingPets();
    }
}
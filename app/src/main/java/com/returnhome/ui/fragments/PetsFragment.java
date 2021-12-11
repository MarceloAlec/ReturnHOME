package com.returnhome.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.ui.adapters.PetAdapter;
import com.returnhome.utils.AppConfig;
import com.returnhome.utils.retrofit.ResponseApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PetsFragment extends Fragment  {

    private PetAdapter mPetAdapter;
    private RecyclerView mRecyclerViewPets;
    private PetProvider mPetProvider;
    private AppConfig mAppConfig;
    private FloatingActionButton mFloatingButtonAdd;
    private BottomSheetDialog mBottomSheetDialog;
    private Button mButtonAdd;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInputBreed;
    private TextInputEditText mTextInputDescription;
    private RadioButton mRadioButtonMalePet;
    private ArrayList<Pet> petArrayList;

    public PetsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pets, container, false);

        mPetProvider = new PetProvider(container.getContext());
        petArrayList = new ArrayList<>();
        mRecyclerViewPets = view.findViewById(R.id.recyclerView);
        mAppConfig = new AppConfig(container.getContext());
        mFloatingButtonAdd = view.findViewById(R.id.fab_addPet);
        mBottomSheetDialog = new BottomSheetDialog(container.getContext());
        mBottomSheetDialog.setContentView(R.layout.popup_update);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);

        mButtonAdd = mBottomSheetDialog.findViewById(R.id.btnUpdateAdd);
        mTextInputName = mBottomSheetDialog.findViewById(R.id.textInputNamePet);
        mTextInputBreed = mBottomSheetDialog.findViewById(R.id.textInputBreed);
        mTextInputDescription = mBottomSheetDialog.findViewById(R.id.textInputDescription);
        mRadioButtonMalePet = mBottomSheetDialog.findViewById(R.id.radioButtonMalePet);

        mFloatingButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonAdd.setText(R.string.btn_addPet);
                mBottomSheetDialog.show();
            }
        });

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAdd();
            }
        });

        getPets();

        return view;
    }

    private void clickAdd() {
        String name = mTextInputName.getText().toString();
        String breed = mTextInputBreed.getText().toString();
        String description = mTextInputDescription.getText().toString();
        char gender = ((mRadioButtonMalePet.isChecked() ? 'M' : 'F'));


        if(!name.isEmpty() && !breed.isEmpty()){
            //DATOS INGRESADOS CORRECTAMENTE
            createPet(new Pet(name,breed,gender,description, mAppConfig.getUserId()));
        }
        else{
            Toast.makeText(getContext(), "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPet(Pet pet) {
        mPetProvider.createPet(pet).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if(response.isSuccessful()){
                    pet.setId(response.body().getPet().getId());
                    petArrayList.add(pet);
                    showList(petArrayList);
                    mBottomSheetDialog.dismiss();
                    mTextInputName.getText().clear();
                    mTextInputName.clearFocus();
                    mTextInputBreed.getText().clear();
                    mTextInputBreed.clearFocus();
                    mTextInputDescription.getText().clear();
                    mTextInputDescription.clearFocus();
                    mRadioButtonMalePet.setChecked(true);
                }
                else{
                    Toast.makeText(getContext(), "Mascota añadida con éxito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Toast.makeText(getContext(), "Ocurrio un problema al agregar", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getPets() {
        int idClient = mAppConfig.getUserId();

        mPetProvider.readPet(idClient, true).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if(response.isSuccessful()){
                    petArrayList = response.body().getPets();
                    showList(petArrayList);
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


}
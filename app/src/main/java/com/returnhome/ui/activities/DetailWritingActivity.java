package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.utils.AppConfig;
import com.returnhome.utils.retrofit.ResponseApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailWritingActivity extends AppCompatActivity {

    private ArrayAdapter mArrayAdapterPets;
    private ArrayList<Pet> petArrayList;
    private AppConfig mAppConfig;
    private PetProvider mPetProvider;
    private Spinner mSpinner;

    private TextView mTextViewBreed;
    private TextView mTextViewGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_writing);

        mTextViewBreed = findViewById(R.id.textViewBreed);
        mTextViewGender = findViewById(R.id.textViewGender);

        mSpinner = findViewById(R.id.spinner_pets);
        mPetProvider = new PetProvider(DetailWritingActivity.this);
        mAppConfig = new AppConfig(DetailWritingActivity.this);
        getPets();
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Pet pet = (Pet) mArrayAdapterPets.getItem(position);
                mTextViewBreed.setText(pet.getBreed());
                if(pet.getGender() == 'M'){
                    mTextViewGender.setText("Macho");
                }
                else{
                    mTextViewGender.setText("Hembra");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getPets() {
        int idClient = mAppConfig.getUserId();

        mPetProvider.getPets(idClient).enqueue(new Callback<ResponseApi>() {
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
        mArrayAdapterPets = new ArrayAdapter(DetailWritingActivity.this, R.layout.support_simple_spinner_dropdown_item, petArrayList);
        mSpinner.setAdapter(mArrayAdapterPets);
    }

}
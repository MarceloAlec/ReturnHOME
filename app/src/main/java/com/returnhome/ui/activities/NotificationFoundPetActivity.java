package com.returnhome.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.utils.retrofit.ResponseApi;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFoundPetActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraPetLat;
    private double mExtraPetLng;
    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;

    private int mExtraIdPet;
    private String mExtraPhoneNumber;

    private LatLng mPetLatLng;
    private LatLng mPetHomeLatLng;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;

    private TextView mTextViewPetName;
    private TextView mTextViewBreed;
    private TextView mTextViewGender;
    private TextView mTextViewPhoneNumber;

    private CircleImageView mCircleImageGoToSelectOptionNfc;

    private PetProvider mPetProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_found_pet);

        mExtraPetLat = getIntent().getDoubleExtra("pet_lat", 0);
        mExtraPetLng = getIntent().getDoubleExtra("pet_lng", 0);
        mExtraPetHomeLat = getIntent().getDoubleExtra("pet_home_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("pet_home_lng", 0);
        mExtraIdPet = getIntent().getIntExtra("idPet", 0);
        mExtraPhoneNumber = getIntent().getStringExtra("phone_number");

        mTextViewPetName = findViewById(R.id.textViewNamePetNotification);
        mTextViewBreed = findViewById(R.id.textViewBreedNotification);
        mTextViewGender = findViewById(R.id.textViewGenderNotification);
        mTextViewPhoneNumber = findViewById(R.id.textViewPhoneNumberNotification);
        mCircleImageGoToSelectOptionNfc = findViewById(R.id.btnGoToMapPetHome);




        mPetLatLng = new LatLng(mExtraPetLat, mExtraPetLng);
        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        mPetProvider = new PetProvider(this);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mTextViewPhoneNumber.setText(mExtraPhoneNumber);

        getPet();

        mCircleImageGoToSelectOptionNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(5);

        mMap.addMarker(new MarkerOptions().position(mPetLatLng).title("Mascota encontrada").icon(BitmapDescriptorFactory.fromResource(R.drawable.dog_sit)));
        mMap.addMarker(new MarkerOptions().position(mPetHomeLatLng).title("Hogar").icon(BitmapDescriptorFactory.fromResource(R.drawable.pet_home)));


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetHomeLatLng)
                        .zoom(15f)
                        .build()
        ));

    }

    private void getPet() {
        mPetProvider.readPet(mExtraIdPet, false).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if(response.isSuccessful()){
                    Pet pet = response.body().getPet();
                    mTextViewPetName.setText(pet.getName());
                    mTextViewBreed.setText(pet.getBreed());
                    mTextViewGender.setText(String.valueOf(pet.getGender()));



                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {

            }
        });


    }
}
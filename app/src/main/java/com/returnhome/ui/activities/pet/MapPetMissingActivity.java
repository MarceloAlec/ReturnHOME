package com.returnhome.ui.activities.pet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
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
import com.returnhome.models.RHResponse;
import com.returnhome.providers.ClientProvider;
import com.returnhome.providers.PetProvider;
import com.returnhome.ui.activities.client.HomeActivity;
import com.returnhome.ui.activities.nfc.ReadTagActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPetMissingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraPetLat;
    private double mExtraPetLng;
    private String mExtraPetName;
    private int mExtraIdPet;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;

    private LatLng mPetMissingLatLng;

    private CircleImageView mCircleImageGoToHome;

    private TextView mTextViewPetName;
    private TextView mTextViewBreed;
    private TextView mTextViewGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pet_missing);

        initializeComponents();

        mExtraIdPet = getIntent().getIntExtra("idPet", 0);
        mExtraPetName = getIntent().getStringExtra("pet_name");
        mExtraPetLat = getIntent().getDoubleExtra("pet_lat", 0);
        mExtraPetLng = getIntent().getDoubleExtra("pet_lng", 0);

        mPetMissingLatLng = new LatLng(mExtraPetLat, mExtraPetLng);

        mMapFragment.getMapAsync(this);

        getPet(mExtraIdPet);

        mCircleImageGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapPetMissingActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void initializeComponents(){
        mTextViewPetName = findViewById(R.id.textViewPetNameMissing);
        mTextViewBreed = findViewById(R.id.textViewPetBreedMissing);
        mTextViewGender = findViewById(R.id.textViewPetGenderMissing);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mCircleImageGoToHome = findViewById(R.id.btnGoToHomeFromPetMissing);
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

        mMap.addMarker(new MarkerOptions().position(mPetMissingLatLng)
                .title(mExtraPetName+" fue vista por ultima vez aqui")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_pet_location))).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetMissingLatLng)
                        .zoom(15f)
                        .build()
        ));
    }

    private void getPet(int idPet) {
        PetProvider.readPet(idPet, 2).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                    Pet pet = response.body().getPet();
                    mTextViewPetName.setText(pet.getName());
                    mTextViewBreed.setText(pet.getBreed());
                    mTextViewGender.setText(String.valueOf(pet.getGender()));
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {

            }
        });
    }


}
package com.returnhome.ui.activities.mascota;

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
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;
import com.returnhome.ui.activities.cliente.HomeActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaMascotaDesaparecidaActivity extends AppCompatActivity implements OnMapReadyCallback {

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
                Intent intent = new Intent(MapaMascotaDesaparecidaActivity.this, HomeActivity.class);
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
                .title(mExtraPetName+" fue visto/a por ultima vez aqui")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_ubicacion_mascota))).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetMissingLatLng)
                        .zoom(15f)
                        .build()
        ));
    }

    private void getPet(int idPet) {
        MascotaController.obtener(idPet, 2).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    Mascota mascota = response.body().getMascota();
                    mTextViewPetName.setText(mascota.getNombre());
                    mTextViewBreed.setText(mascota.getRaza());
                    mTextViewGender.setText(String.valueOf(mascota.getGenero()));
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {

            }
        });
    }


}
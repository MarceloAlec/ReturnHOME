package com.returnhome.ui.activities.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class DetalleInfoLecturaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;
    private String mExtraPhoneNumber;
    private int  mExtraIdPet;

    private LatLng mPetHomeLatLng;

    private Mascota mascota;


    private CircleImageView mCircleImageGoToSelectOptionNfc;

    private TextView mTextViewPetName;
    private TextView mTextViewBreed;
    private TextView mTextViewGender;
    private TextView mTextViewPhoneNumber;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reading);

        initializeComponents();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mExtraPetHomeLat = getIntent().getDoubleExtra("pet_home_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("pet_home_lng", 0);
        mExtraPhoneNumber = getIntent().getStringExtra("phone_number");
        mExtraIdPet = getIntent().getIntExtra("idPet", 0);

        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        mTextViewPhoneNumber.setText(mExtraPhoneNumber);

        mCircleImageGoToSelectOptionNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleInfoLecturaActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        getPet(mExtraIdPet);
    }

    private void initializeComponents(){
        mTextViewPetName = findViewById(R.id.textViewNamePetReading);
        mTextViewBreed = findViewById(R.id.textViewBreedReading);
        mTextViewGender = findViewById(R.id.textViewGenderReading);
        mTextViewPhoneNumber = findViewById(R.id.textViewPhoneNumberReading);
        mCircleImageGoToSelectOptionNfc = findViewById(R.id.btnGoToHomeFromDetailReading);
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


        mMap.addMarker(new MarkerOptions().position(mPetHomeLatLng).title("Hogar de la mascota").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetHomeLatLng)
                        .zoom(15f)
                        .build()
        ));

    }

    private void getPet(int idPet) {
        MascotaController.obtener(idPet, 2).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    mascota = response.body().getMascota();
                    mTextViewPetName.setText(mascota.getNombre());
                    mTextViewBreed.setText(mascota.getRaza());
                    mTextViewGender.setText(String.valueOf(mascota.getGenero()));
                }
                else{
                    Toast.makeText(DetalleInfoLecturaActivity.this, "Los datos de la mascota no se pudieron cargar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(DetalleInfoLecturaActivity.this, "Los datos de la mascota no se pudieron cargar", Toast.LENGTH_LONG).show();

            }
        });
    }


}
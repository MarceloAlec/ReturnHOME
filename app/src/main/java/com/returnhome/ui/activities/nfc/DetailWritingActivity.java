package com.returnhome.ui.activities.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.returnhome.modelos.Mascota;
import com.returnhome.utils.AppConfig;


import de.hdodenhof.circleimageview.CircleImageView;

public class DetailWritingActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    private AppConfig mAppConfig;

    private TextView mTextViewNamePet;
    private TextView mTextViewBreed;
    private TextView mTextViewGender;
    private TextView mTextViewPhoneNumber;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private LocationRequest mLocationRequest;

    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;

    private Mascota mMascota;

    private LatLng mPetHomeLatLng;

    private Button mButtonWriteTagNow;
    private CircleImageView mGoToMapPetHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_writing);

        initializeComponents();

        mMapFragment.getMapAsync(this);
        mAppConfig = new AppConfig(this);

        mExtraPetHomeLat = getIntent().getDoubleExtra("petHome_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("petHome_lng", 0);
        mMascota = (Mascota)getIntent().getSerializableExtra("pet");

        mTextViewNamePet.setText(mMascota.getNombre());
        mTextViewBreed.setText(mMascota.getRaza());
        mTextViewGender.setText(String.valueOf(mMascota.getGenero()));
        mTextViewPhoneNumber.setText(mAppConfig.getPhoneNumber());

        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        mGoToMapPetHome.setOnClickListener(this);

        mButtonWriteTagNow.setOnClickListener(this);
    }



    private void initializeComponents() {
        mTextViewNamePet = findViewById(R.id.textViewNamePet);
        mTextViewBreed = findViewById(R.id.textViewBreed);
        mTextViewGender = findViewById(R.id.textViewGender);
        mTextViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        mButtonWriteTagNow = findViewById(R.id.btnWriteTagNow);
        mGoToMapPetHome = findViewById(R.id.btnGoToMapPetHome);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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

        mMap.addMarker(new MarkerOptions().position(mPetHomeLatLng).title("Hogar de "+ mMascota.getNombre()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetHomeLatLng)
                        .zoom(15f)
                        .build()
        ));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnWriteTagNow:
                Intent intent = new Intent(DetailWritingActivity.this, WriteTagActivity.class);
                intent.putExtra("idPet", mMascota.getIdMascota());
                intent.putExtra("pet_home_lat",mExtraPetHomeLat);
                intent.putExtra("pet_home_lng",mExtraPetHomeLng);
                startActivity(intent);
                break;

            case R.id.btnGoToMapPetHome:
                finish();
                break;
        }
    }
}
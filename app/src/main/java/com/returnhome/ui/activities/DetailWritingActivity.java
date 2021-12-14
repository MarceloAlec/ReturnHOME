package com.returnhome.ui.activities;

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
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.utils.AppConfig;


import de.hdodenhof.circleimageview.CircleImageView;

public class DetailWritingActivity extends AppCompatActivity implements OnMapReadyCallback {


    private AppConfig mAppConfig;

    private TextView mTextViewNamePet;
    private TextView mTextViewBreed;
    private TextView mTextViewGender;
    private TextView mTextViewPhoneNumber;
    //private TextView mTextViewHomePet;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private LocationRequest mLocationRequest;

    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;
    private String mExtraPetHome;
    private Pet mPet;

    private LatLng mPetHomeLatLng;

    private Button mButtonWriteTagNow;
    private CircleImageView mCircleImageReturnMapPetHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_writing);

        mTextViewNamePet = findViewById(R.id.textViewNamePet);
        mTextViewBreed = findViewById(R.id.textViewBreed);
        mTextViewGender = findViewById(R.id.textViewGender);
        mTextViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        //mTextViewHomePet = findViewById(R.id.textViewHomePet);
        mButtonWriteTagNow = findViewById(R.id.btnWriteTagNow);
        mCircleImageReturnMapPetHome = findViewById(R.id.btnReturnMapPetHome);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mAppConfig = new AppConfig(DetailWritingActivity.this);

        mExtraPetHomeLat = getIntent().getDoubleExtra("petHome_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("petHome_lng", 0);
        mExtraPetHome = getIntent().getStringExtra("petHome");
        mPet = (Pet)getIntent().getSerializableExtra("pet");

        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        mTextViewNamePet.setText(mPet.getName());
        mTextViewBreed.setText(mPet.getBreed());
        mTextViewGender.setText(String.valueOf(mPet.getGender()));
        mTextViewPhoneNumber.setText(mAppConfig.getPhoneNumber());
//        mTextViewHomePet.setText(mExtraPetHome);

        mCircleImageReturnMapPetHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonWriteTagNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailWritingActivity.this, WriteTagActivity.class);
                intent.putExtra("idPet",mPet.getId());
                intent.putExtra("pet_home_lat",mExtraPetHomeLat);
                intent.putExtra("pet_home_lng",mExtraPetHomeLng);
                startActivity(intent);
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

        mMap.addMarker(new MarkerOptions().position(mPetHomeLatLng).title("Hogar de "+mPet.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pet_home))).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetHomeLatLng)
                        .zoom(15f)
                        .build()
        ));

    }

}
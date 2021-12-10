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

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailReadingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;
    private String mExtraPetName;
    private String mExtraPhoneNumber;
    private String mExtraBreed;
    private String mExtraGender;


    private LatLng mPetHomeLatLng;

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

        mTextViewPetName = findViewById(R.id.textViewNamePetReading);
        mTextViewBreed = findViewById(R.id.textViewBreedReading);
        mTextViewGender = findViewById(R.id.textViewGenderReading);
        mTextViewPhoneNumber = findViewById(R.id.textViewPhoneNumberReading);

        mCircleImageGoToSelectOptionNfc = findViewById(R.id.btnGoToSelectOpcionNfc);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mExtraPetHomeLat = getIntent().getDoubleExtra("pet_home_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("pet_home_lng", 0);
        mExtraPetName = getIntent().getStringExtra("name");
        mExtraGender = getIntent().getStringExtra("gender");
        mExtraBreed = getIntent().getStringExtra("breed");
        mExtraPhoneNumber = getIntent().getStringExtra("phone_number");

        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        mTextViewPetName.setText(mExtraPetName);
        mTextViewBreed.setText(mExtraBreed);
        mTextViewGender.setText(mExtraGender);
        mTextViewPhoneNumber.setText(mExtraPhoneNumber);

        mCircleImageGoToSelectOptionNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailReadingActivity.this, SelectOptionNfcActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        mMap.addMarker(new MarkerOptions().position(mPetHomeLatLng).title("Hogar de "+mExtraPetName).icon(BitmapDescriptorFactory.fromResource(R.drawable.pet_home))).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetHomeLatLng)
                        .zoom(15f)
                        .build()
        ));

    }
}
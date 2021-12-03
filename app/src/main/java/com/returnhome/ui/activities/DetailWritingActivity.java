package com.returnhome.ui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.utils.AppConfig;
import com.returnhome.utils.retrofit.ResponseApi;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailWritingActivity extends AppCompatActivity implements OnMapReadyCallback {


    private AppConfig mAppConfig;
    private PetProvider mPetProvider;


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

        mPetProvider = new PetProvider(DetailWritingActivity.this);
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
                intent.putExtra("breed",mTextViewBreed.getText().toString());
                intent.putExtra("gender",mTextViewGender.getText().toString());
                intent.putExtra("pet_name",mTextViewNamePet.getText().toString());
                //intent.putExtra("pet_home",mExtraPetHome);
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
package com.returnhome.ui.activities.pet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.returnhome.ui.activities.client.HomeActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapPetFoundActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraPetLat;
    private double mExtraPetLng;
    private String mExtraPetName;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;

    private LatLng mPetLatLng;

    private CircleImageView mCircleImageGoToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pet_found);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mCircleImageGoToHome = findViewById(R.id.btnGoToHome);

        mExtraPetName = getIntent().getStringExtra("pet_name");
        mExtraPetLat = getIntent().getDoubleExtra("pet_lat", 0);
        mExtraPetLng = getIntent().getDoubleExtra("pet_lng", 0);

        mPetLatLng = new LatLng(mExtraPetLat, mExtraPetLng);

        mCircleImageGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapPetFoundActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

        mMap.addMarker(new MarkerOptions().position(mPetLatLng).title(mExtraPetName+" se encuentra aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.dog_sit))).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetLatLng)
                        .zoom(15f)
                        .build()
        ));
    }
}
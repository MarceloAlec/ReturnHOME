package com.returnhome.ui.activities.nfc;

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
import com.returnhome.models.RHResponse;
import com.returnhome.ui.activities.client.HomeActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailReadingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;
    private String mExtraPhoneNumber;
    private Pet mExtraPet;

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

        initializeComponents();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mExtraPetHomeLat = getIntent().getDoubleExtra("pet_home_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("pet_home_lng", 0);
        mExtraPhoneNumber = getIntent().getStringExtra("phone_number");
        mExtraPet = (Pet)getIntent().getSerializableExtra("pet");

        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        mTextViewPetName.setText(mExtraPet.getName());
        mTextViewBreed.setText(mExtraPet.getBreed());
        mTextViewGender.setText(String.valueOf(mExtraPet.getGender()));
        mTextViewPhoneNumber.setText(mExtraPhoneNumber);

        mCircleImageGoToSelectOptionNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailReadingActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
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


        mMap.addMarker(new MarkerOptions().position(mPetHomeLatLng).title("Hogar de "+mExtraPet.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))).showInfoWindow();


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetHomeLatLng)
                        .zoom(15f)
                        .build()
        ));

    }


}
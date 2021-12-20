package com.returnhome.ui.activities.pet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.returnhome.models.Client;
import com.returnhome.models.RHResponse;
import com.returnhome.providers.ClientProvider;
import com.returnhome.ui.activities.client.HomeActivity;
import com.returnhome.utils.AppConfig;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPetFoundActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraPetLat;
    private double mExtraPetLng;
    private String mExtraPetName;
    private int mExtraIdClient;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationRequest mLocationRequest;

    private LatLng mPetFoundLatLng;

    private CircleImageView mCircleImageGoToHome;

    private TextView mTextViewClientName;
    private TextView mTextViewEmail;
    private TextView mTextViewPhoneNumber;
    private ImageView mImageViewCallUser;
    private String phoneNumber;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pet_found);

        initializeComponents();

        mMapFragment.getMapAsync(this);

        mExtraIdClient = getIntent().getIntExtra("idClient", 0);
        mExtraPetName = getIntent().getStringExtra("pet_name");
        mExtraPetLat = getIntent().getDoubleExtra("pet_lat", 0);
        mExtraPetLng = getIntent().getDoubleExtra("pet_lng", 0);

        mPetFoundLatLng = new LatLng(mExtraPetLat, mExtraPetLng);

        getClient(mExtraIdClient);

        mCircleImageGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapPetFoundActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mImageViewCallUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(phoneNumber);
            }
        });
    }

    private void initializeComponents(){
        mTextViewClientName = findViewById(R.id.textViewClientName);
        mTextViewEmail = findViewById(R.id.textViewClientEmail);
        mTextViewPhoneNumber = findViewById(R.id.textViewClientPhoneNumber);
        mImageViewCallUser = findViewById(R.id.imageViewCallUser);
        mCircleImageGoToHome = findViewById(R.id.btnGoToHomeFromPetFound);
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

        mMap.addMarker(new MarkerOptions().position(mPetFoundLatLng)
                .title(mExtraPetName+" se encuentra aquí")
                .icon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_pet_location))).showInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mPetFoundLatLng)
                        .zoom(15f)
                        .build()
        ));
    }



    private void getClient(int mExtraIdClient) {
        ClientProvider.getClient(mExtraIdClient).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                    Client client = response.body().getClient();
                    mTextViewClientName.setText(client.getName());
                    mTextViewEmail.setText(client.getEmail());
                    mTextViewPhoneNumber.setText(client.getPhoneNumber());
                    phoneNumber = response.body().getClient().getPhoneNumber();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {

            }
        });
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}
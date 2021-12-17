package com.returnhome.ui.activities.pet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
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
import com.returnhome.R;
import com.returnhome.includes.Toolbar;
import com.returnhome.models.Pet;
import com.returnhome.models.RHResponse;
import com.returnhome.providers.PetProvider;
import com.returnhome.ui.activities.SendNotificationActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPetActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private LatLng mPetLatLng;
    private LatLng mPetHomeLatLng;
    private Marker mMarker;

    private Button mButtonGoToSendNotification;

    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;

    private String mExtraPhoneNumber;
    private Pet mExtraPet;



    private TextView mTextViewPetName;
    private TextView mTextViewBreed;
    private TextView mTextViewGender;
    private TextView mTextViewPhoneNumber;


    //ESCUCHA CUANDO EL USARIO ESTE EN MOVIMIENTO
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mPetLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if(mMarker != null){
                        mMarker.remove();
                    }

                    mMarker = mMap.addMarker(new MarkerOptions().position(mPetLatLng).title("Mascota encontrada").icon(BitmapDescriptorFactory.fromResource(R.drawable.dog_sit)));
                    mMarker.showInfoWindow();

                    //OBTIENE LA UBICACION EN TIEMPO REAL
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(mPetLatLng)
                            .zoom(15f)
                            .build()
                    ));

                    }

                }
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pet);

        mTextViewPetName = findViewById(R.id.textViewNamePetNotification);
        mTextViewBreed = findViewById(R.id.textViewBreedNotification);
        mTextViewGender = findViewById(R.id.textViewGenderNotification);
        mTextViewPhoneNumber = findViewById(R.id.textViewPhoneNumberNotification);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mExtraPetHomeLat = getIntent().getDoubleExtra("pet_home_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("pet_home_lng", 0);
        mExtraPhoneNumber = getIntent().getStringExtra("phone_number");
        mExtraPet = (Pet)getIntent().getSerializableExtra("pet");

        mTextViewPhoneNumber.setText(mExtraPhoneNumber);
        mTextViewPetName.setText(mExtraPet.getName());
        mTextViewBreed.setText(mExtraPet.getBreed());
        mTextViewGender.setText(String.valueOf(mExtraPet.getGender()));

        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        mButtonGoToSendNotification = findViewById(R.id.btnGoToSendNotification);

        Toolbar.show(this, "Ubicación de la mascota", true);

        mButtonGoToSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapPetActivity.this, SendNotificationActivity.class);
                intent.putExtra("idClient",mExtraPet.getId_client());
                intent.putExtra("pet_name",mExtraPet.getName());
                intent.putExtra("pet_lat",mPetLatLng.latitude);
                intent.putExtra("pet_lng",mPetLatLng.longitude);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ElIMINA LA ACTUALIZACION DEL GPS
        if(mLocationCallback !=null && mFusedLocation != null){
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnCameraIdleListener(mCameraListener);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        mMap.addMarker(new MarkerOptions().position(mPetHomeLatLng).title("Hogar de "+mExtraPet.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pet_home)));

        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }
                    else {
                        showAlertDialogNOGPS();
                    }
                }
                else{
                    //EN CASO DE QUE EL USUARIO NO ACEPTE LOS PERMISOS, SE MOSTRARA EL ALERTDIALOG INDICANDO QUE LOS DEBE ACEPTAR
                    checkLocationPermissions();
                }
            }
            else{
                //EN CASO DE QUE EL USUARIO NO ACEPTE LOS PERMISOS, SE MOSTRARA EL ALERTDIALOG INDICANDO QUE LOS DEBE ACEPTAR
                checkLocationPermissions();
            }
        }
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //AL EJECTUTARSE EL EVENTO REQUESTLOCALTIONUPDATES, SE EJECUTA EL EVENTO LOCATIONCALLBACK
                if (gpsActived()) {
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                } else {
                    showAlertDialogNOGPS();
                }
            } else {
                checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

            } else {
                showAlertDialogNOGPS();
            }
        }
    }

    private boolean gpsActived() {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //SI EL GPS ESTA ACTIVADO
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return isActive;
    }

    private void showAlertDialogNOGPS() {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setCanceledOnTouchOutside(false);
        builder.setMessage("Por favor activa tu ubicacion para continuar");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "Configuraciones", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ESPERA Y ESCUCHA HASTA QUE EL USUARIO ACTIVE EL GPS
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        }
        else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActived()){
            showAlertDialogNOGPS();
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog builder = new AlertDialog.Builder(this).create();
                builder.setCanceledOnTouchOutside(false);
                builder.setTitle("Proporciona los permisos para continuar");
                builder.setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse");
                builder.setButton( AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //HABILITA LOS PERMISOS PARA USAR LA UBICACION
                        ActivityCompat.requestPermissions(MapPetActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //ANTES DE AGREGAR ESTE EVENTO, SI EL USARIO PRESIONABA EL BOTON DE IR HACIA ATRAS DE LA UI DEL SISTEMA EL MENSAJE QUE
                        //SOLICITABA QUE EL USARIO ACTIVE LOS PERMISOS SE OCULTABA MOSTRANDO EL MAPA DEL MUNDO SIN REALIZAR NINGUNA ACCIÓN POR LO TANTO
                        //SE AGREGA ESTE EVENTO DE TAL MANERA QUE SI EL USUARIO PRESIONA EL BOTON ANTES MENCIONADO FINALIZARA LA ACTIVIDAD
                        finish();
                    }
                });
                builder.show();
            }
            else {
                ActivityCompat.requestPermissions(MapPetActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }




}
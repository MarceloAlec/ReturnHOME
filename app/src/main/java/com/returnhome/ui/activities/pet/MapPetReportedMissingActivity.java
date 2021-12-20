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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.SphericalUtil;
import com.returnhome.R;
import com.returnhome.includes.Toolbar;
import com.returnhome.models.FCMBody;
import com.returnhome.models.FCMResponse;
import com.returnhome.models.Pet;
import com.returnhome.models.RHResponse;
import com.returnhome.providers.NotificationProvider;
import com.returnhome.providers.PetProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPetReportedMissingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Button mButtonSendNotification;

    private String mExtraPetName;
    private int mExtraIdPet;
    private LatLng mCurrentLatLng;

    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutoComplete;
    private LatLng mPetLastLocationLatLng;
    private String mPetLastLocation;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {


                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //OBTIENE LA UBICACION EN TIEMPO REAL
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(15f)
                            .build()
                    ));

                    limitSearch();
                    stopLocation();

                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pet_reported_missing);

        initializeComponents();

        mMapFragment.getMapAsync(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        Toolbar.show(this, "Vista por ultima vez en", true);

        mExtraIdPet = getIntent().getIntExtra("idPet", 0);
        mExtraPetName = getIntent().getStringExtra("pet_name");

        mButtonSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pet pet = new Pet(mExtraIdPet, true);
                updateStatusMissingPet(pet);
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        mPlaces = Places.createClient(this);
        instanceAutoCompletePetHome();
        onCameraMove();
    }

    private void initializeComponents(){
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mButtonSendNotification = findViewById(R.id.btnSendNotification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ElIMINA LA ACTUALIZACION DEL GPS
        stopLocation();
    }

    private void sendNotification(){

        Map<String, String> map = new HashMap<>();
        map.put("title","Mascota desaparecida");
        map.put("body","Vista por ultima vez en: "+mPetLastLocation);
        map.put("idPet", String.valueOf(mExtraIdPet));
        map.put("pet_name", mExtraPetName);
        map.put("pet_lat",String.valueOf(mPetLastLocationLatLng.latitude));
        map.put("pet_lng",String.valueOf(mPetLastLocationLatLng.longitude));

        FCMBody fcmBody = new FCMBody("/topics/missing-pets", "high", map);
        NotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if(response.body() != null) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MapPetReportedMissingActivity.this, "Mascota reportada como desaparecida", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(MapPetReportedMissingActivity.this, "No se pudo reportar a la mascota", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Toast.makeText(MapPetReportedMissingActivity.this,"No se pudo reportar a la mascota",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateStatusMissingPet(Pet pet){

        PetProvider.updateStatusMissingPet(pet).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {

                if(response.isSuccessful()){
                    sendNotification();
                }
                else{
                    Toast.makeText(MapPetReportedMissingActivity.this, "La mascota ya fue reportada como desaparecida", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {

            }
        });
    }


    private void limitSearch() {
        //DISTANCIA PARA LIMITAR LAS BUSQUEDAS EN M
        LatLng northSide = SphericalUtil.computeOffset(mCurrentLatLng, 5000, 0);
        LatLng southSide = SphericalUtil.computeOffset(mCurrentLatLng, 5000, 180);
        mAutoComplete.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
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
                        ActivityCompat.requestPermissions(MapPetReportedMissingActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
                ActivityCompat.requestPermissions(MapPetReportedMissingActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void onCameraMove() {

        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    //CUANDO EL USARIO CAMBIA LA POSICION DE LA CAMARA EN EL MAPA
                    Geocoder geocoder = new Geocoder(MapPetReportedMissingActivity.this);
                    mPetLastLocationLatLng = mMap.getCameraPosition().target;
                    List<Address> addressList = geocoder.getFromLocation(mPetLastLocationLatLng.latitude, mPetLastLocationLatLng.longitude, 1);
                    String city = addressList.get(0).getLocality();
                    String address = addressList.get(0).getAddressLine(0);
                    mPetLastLocation = address + " " + city;
                    mAutoComplete.setText(address + " " + city);

                } catch (Exception e) {
                    Log.d("Error: ", "Mensaje error: " + e.getMessage());
                }
            }
        };

    }

    private void instanceAutoCompletePetHome() {
        mAutoComplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutocompletePetHome);
        mAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {

                mPetLastLocationLatLng = place.getLatLng();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(mPetLastLocationLatLng.latitude, mPetLastLocationLatLng.longitude))
                        .zoom(15f)
                        .build()
                ));

            }
        });
    }

    private void stopLocation(){
        if(mLocationCallback !=null && mFusedLocation != null){
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }
    }
}
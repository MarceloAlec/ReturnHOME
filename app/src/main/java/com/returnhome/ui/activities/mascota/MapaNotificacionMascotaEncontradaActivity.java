package com.returnhome.ui.activities.mascota;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.returnhome.R;
import com.returnhome.controllers.ClienteController;
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.Cliente;
import com.returnhome.models.FCMCuerpo;
import com.returnhome.models.FCMRespuesta;
import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;
import com.returnhome.controllers.NotificacionController;
import com.returnhome.utils.AppConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaNotificacionMascotaEncontradaActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private LatLng mPetLatLng;
    private LatLng mPetHomeLatLng;
    private Marker mMarkerPet;
    private Marker mMarkerHomePet;

    private Button mButtonGoToSendNotification;
    private CircleImageView mGoToHome;

    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;

    private String mExtraPhoneNumber;
    private int mExtraIdPet;
    private Cliente cliente;
    private Mascota mascota;
    private String mPetLocation;



    private TextView mTextViewPetName;
    private TextView mTextViewBreed;
    private TextView mTextViewGender;
    private TextView mTextViewPhoneNumber;

    private AppConfig mAppConfig;
    private ImageView mImageViewCallUser;


    //ESCUCHA CUANDO EL USARIO ESTE EN MOVIMIENTO
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mPetLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMarkerPet =  mMap.addMarker(new MarkerOptions().position(mPetLatLng).title("Mascota encontrada").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ubicacion_mascota)));
                    mMarkerHomePet= mMap.addMarker(new MarkerOptions().position(mPetHomeLatLng).title("Hogar de la mascota").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home)));

                    mMarkerHomePet.showInfoWindow();

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(mMarkerPet.getPosition());
                    builder.include(mMarkerHomePet.getPosition());

                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 180);
                    mMap.animateCamera(cu);

                    stopLocation();

                    }

                }
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pet_reported_found);

        initializeComponents();

        mMapFragment.getMapAsync(this);

        mAppConfig = new AppConfig(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mExtraPetHomeLat = getIntent().getDoubleExtra("pet_home_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("pet_home_lng", 0);
        mExtraPhoneNumber = getIntent().getStringExtra("phone_number");
        mExtraIdPet = getIntent().getIntExtra("idPet", 0);

        mTextViewPhoneNumber.setText(mExtraPhoneNumber);


        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        getPet(mExtraIdPet);

        mButtonGoToSendNotification.setOnClickListener(this);

        mGoToHome.setOnClickListener(this);

        mImageViewCallUser.setOnClickListener(this);
    }


    private void initializeComponents(){
        mTextViewPetName = findViewById(R.id.textViewNamePetNotification);
        mTextViewBreed = findViewById(R.id.textViewBreedNotification);
        mTextViewGender = findViewById(R.id.textViewGenderNotification);
        mTextViewPhoneNumber = findViewById(R.id.textViewPhoneNumberNotification);
        mGoToHome = findViewById(R.id.btnGoToHomeFromReportedFound);
        mImageViewCallUser = findViewById(R.id.imageViewCallUserReportedFound);
        mButtonGoToSendNotification = findViewById(R.id.btnGoToSendNotification);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGoToSendNotification:
                getClientToSendNotification();
                break;

            case R.id.btnGoToHomeFromReportedFound:
                finish();
                break;

            case R.id.imageViewCallUserReportedFound:
                dialPhoneNumber(mExtraPhoneNumber);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ElIMINA LA ACTUALIZACION DEL GPS
        stopLocation();
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
                        ActivityCompat.requestPermissions(MapaNotificacionMascotaEncontradaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
                ActivityCompat.requestPermissions(MapaNotificacionMascotaEncontradaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void getClientToSendNotification() {

        try{
            ClienteController.obtener(mascota.getIdCliente()).enqueue(new Callback<RHRespuesta>() {
                @Override
                public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                    if(response.isSuccessful()){
                        cliente = response.body().getCliente();
                        sendNotification();
                    }
                }

                @Override
                public void onFailure(Call<RHRespuesta> call, Throwable t) {
                    Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e){
            Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificación",Toast.LENGTH_SHORT).show();
        }

    }

    private void sendNotification(){

        try {
            Geocoder geocoder = new Geocoder(MapaNotificacionMascotaEncontradaActivity.this);
            List<Address> addressList = geocoder.getFromLocation(mPetLatLng.latitude, mPetLatLng.longitude, 1);
            String city = addressList.get(0).getLocality();
            String address = addressList.get(0).getAddressLine(0);
            mPetLocation = address + " " + city;

        } catch (IOException e) {
            Log.d("Error: ", "Se ha producido un error: " + e.getMessage());
        }

        String token = cliente.getNombre();
        if(!token.equals("")){
            Map<String, String> map = new HashMap<>();
            map.put("title","Mascota encontrada");
            map.put("body", mascota.getNombre()+" fue encontrada en: " +mPetLocation);
            map.put("idClient",String.valueOf(mAppConfig.obtenerIdCliente()));
            map.put("phoneNumber",mAppConfig.obtenerNumeroCelular());
            map.put("pet_name", mascota.getNombre());
            map.put("pet_lat",String.valueOf(mPetLatLng.latitude));
            map.put("pet_lng",String.valueOf(mPetLatLng.longitude));
            FCMCuerpo fcmCuerpo = new FCMCuerpo(token, "high", map);
            NotificacionController.enviarNotificacion(fcmCuerpo).enqueue(new Callback<FCMRespuesta>() {
                @Override
                public void onResponse(Call<FCMRespuesta> call, Response<FCMRespuesta> response) {
                    if(response.body() != null){
                        if(response.body().getSuccess() == 1){
                            Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"CanalNotificacion enviada con exito",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<FCMRespuesta> call, Throwable t) {
                    Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocation(){
        if(mLocationCallback !=null && mFusedLocation != null){
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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
                    Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this, "Los datos de la mascota no se pudieron cargar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {

                Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this, "Los datos de la mascota no se pudieron cargar", Toast.LENGTH_LONG).show();

            }
        });
    }
}
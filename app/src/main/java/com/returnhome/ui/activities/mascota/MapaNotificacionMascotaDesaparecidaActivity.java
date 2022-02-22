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
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.FCMCuerpo;
import com.returnhome.models.FCMRespuesta;
import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;
import com.returnhome.controllers.NotificacionController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaNotificacionMascotaDesaparecidaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMapa;
    private SupportMapFragment mMapaFragment;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Button mButtonSeleccionarLugarDesaparecida;

    private String mExtraNombreMascota;
    private int mExtraIdMascota;
    private LatLng mActualUbicacionLatLng;

    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutoCompletar;
    private LatLng mUltimaUbicacionMascotaLatLng;
    private String mUltimoLugarMascota;
    private androidx.appcompat.widget.Toolbar mToolbar;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {


                    mActualUbicacionLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    //OBTIENE LA UBICACION EN TIEMPO REAL
                    mMapa.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(15f)
                            .build()
                    ));

                    limitarBusqueda();
                    detenerLocalizacion();

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_notificacion_mascota_desaparecida);

        inicializarComponentes();

        mMapaFragment.getMapAsync(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Vista por ultima vez en:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mExtraIdMascota = getIntent().getIntExtra("idMascota", 0);
        mExtraNombreMascota = getIntent().getStringExtra("nombreMascota");

        mButtonSeleccionarLugarDesaparecida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mascota mascota = new Mascota(mExtraIdMascota, true);
                actualizarEstadoMascotaDesaparecida(mascota);
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        mPlaces = Places.createClient(this);
        instanciarAutoCompletar();
        movimientoCamara();
    }

    private void inicializarComponentes(){
        mMapaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mButtonSeleccionarLugarDesaparecida = findViewById(R.id.btnSeleccionarLugarMascotaDesaparecida);
        mToolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ElIMINA LA ACTUALIZACION DEL GPS
        detenerLocalizacion();
    }

    private void enviarNotificacion(){

        Map<String, String> map = new HashMap<>();
        map.put("title","Mascota desaparecida");
        map.put("body","Vista por ultima vez en: "+ mUltimoLugarMascota);
        map.put("idMascota", String.valueOf(mExtraIdMascota));
        map.put("nombreMascota", mExtraNombreMascota);
        map.put("mascotaLat",String.valueOf(mUltimaUbicacionMascotaLatLng.latitude));
        map.put("mascotaLng",String.valueOf(mUltimaUbicacionMascotaLatLng.longitude));

        FCMCuerpo fcmCuerpo = new FCMCuerpo("/topics/mascotas-desaparecidas", "high", map);
        NotificacionController.enviarNotificacion(fcmCuerpo).enqueue(new Callback<FCMRespuesta>() {
            @Override
            public void onResponse(Call<FCMRespuesta> call, Response<FCMRespuesta> response) {
                if(response.body() != null) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MapaNotificacionMascotaDesaparecidaActivity.this, "Mascota reportada como desaparecida", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(MapaNotificacionMascotaDesaparecidaActivity.this, "No se pudo reportar a la mascota", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<FCMRespuesta> call, Throwable t) {
                Toast.makeText(MapaNotificacionMascotaDesaparecidaActivity.this,"No se pudo reportar a la mascota",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void actualizarEstadoMascotaDesaparecida(Mascota mascota){

        MascotaController.actualizarMascotaDesaparecida(mascota).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){
                    enviarNotificacion();
                }
                else{
                    Toast.makeText(MapaNotificacionMascotaDesaparecidaActivity.this, "La mascota ya fue reportada como desaparecida", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MapaNotificacionMascotaDesaparecidaActivity.this, "No se pudo reportar a la mascota", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void limitarBusqueda() {
        //DISTANCIA PARA LIMITAR LAS BUSQUEDAS EN M
        LatLng norte = SphericalUtil.computeOffset(mActualUbicacionLatLng, 5000, 0);
        LatLng sur = SphericalUtil.computeOffset(mActualUbicacionLatLng, 5000, 180);
        mAutoCompletar.setLocationBias(RectangularBounds.newInstance(sur, norte));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMapa = googleMap;
        mMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapa.setOnCameraIdleListener(mCameraListener);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        iniciarLocalizacion();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActivado()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                    }
                    else {
                        mostrarCuadroDialogoActivarGPS();
                    }
                }
                else{
                    //EN CASO DE QUE EL USUARIO NO ACEPTE LOS PERMISOS, SE MOSTRARA EL ALERTDIALOG INDICANDO QUE LOS DEBE ACEPTAR
                    verificarPermisosUbicacion();
                }
            }
            else{
                //EN CASO DE QUE EL USUARIO NO ACEPTE LOS PERMISOS, SE MOSTRARA EL ALERTDIALOG INDICANDO QUE LOS DEBE ACEPTAR
                verificarPermisosUbicacion();
            }
        }
    }

    private void iniciarLocalizacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //AL EJECTUTARSE EL EVENTO REQUESTLOCALTIONUPDATES, SE EJECUTA EL EVENTO LOCATIONCALLBACK
                if (gpsActivado()) {
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                } else {
                    mostrarCuadroDialogoActivarGPS();
                }
            } else {
                verificarPermisosUbicacion();
            }
        } else {
            if (gpsActivado()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                mostrarCuadroDialogoActivarGPS();
            }
        }
    }

    private boolean gpsActivado() {
        boolean activo = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //SI EL GPS ESTA ACTIVADO
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            activo = true;
        }
        return activo;
    }

    private void mostrarCuadroDialogoActivarGPS() {
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
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActivado()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActivado()){
            mostrarCuadroDialogoActivarGPS();
        }
    }

    private void verificarPermisosUbicacion() {
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
                        ActivityCompat.requestPermissions(MapaNotificacionMascotaDesaparecidaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
                ActivityCompat.requestPermissions(MapaNotificacionMascotaDesaparecidaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void movimientoCamara() {

        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    //CUANDO EL USARIO CAMBIA LA POSICION DE LA CAMARA EN EL MAPA
                    Geocoder geocoder = new Geocoder(MapaNotificacionMascotaDesaparecidaActivity.this);
                    mUltimaUbicacionMascotaLatLng = mMapa.getCameraPosition().target;
                    List<Address> listaDirecciones = geocoder.getFromLocation(mUltimaUbicacionMascotaLatLng.latitude, mUltimaUbicacionMascotaLatLng.longitude, 1);
                    String ciudad = listaDirecciones.get(0).getLocality();
                    String direccion = listaDirecciones.get(0).getAddressLine(0);
                    mUltimoLugarMascota = direccion + " " + ciudad;
                    mAutoCompletar.setText(direccion + " " + ciudad);

                } catch (Exception e) {
                    Log.d("Error: ", "Mensaje error: " + e.getMessage());
                }
            }
        };

    }

    private void instanciarAutoCompletar() {
        mAutoCompletar = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.lugarAutocompletarLugarMascotaDesaparecida);
        mAutoCompletar.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoCompletar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {

                mUltimaUbicacionMascotaLatLng = place.getLatLng();
                mMapa.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(mUltimaUbicacionMascotaLatLng.latitude, mUltimaUbicacionMascotaLatLng.longitude))
                        .zoom(15f)
                        .build()
                ));

            }
        });
    }

    private void detenerLocalizacion(){
        if(mLocationCallback !=null && mFusedLocation != null){
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }
    }
}
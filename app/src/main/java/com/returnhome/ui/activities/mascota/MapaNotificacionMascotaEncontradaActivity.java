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
import com.returnhome.controllers.MascotaController;
import com.returnhome.controllers.TokenController;
import com.returnhome.utils.retrofit.FCMCuerpo;
import com.returnhome.utils.retrofit.FCMRespuesta;
import com.returnhome.models.Mascota;
import com.returnhome.utils.retrofit.RHRespuesta;
import com.returnhome.controllers.NotificacionController;
import com.returnhome.models.Token;
import com.returnhome.utils.AppSharedPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaNotificacionMascotaEncontradaActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMapa;
    private SupportMapFragment mMapaFragment;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private LatLng mMascotaEncontradaLatLng;
    private LatLng mHogarMascotaEncontradaLatLng;
    private Marker mMarkerMascotaEncontrada;
    private Marker mMarkerHogarMascotaEncontrada;

    private Button mButtonNotificarMascotaEncontrada;
    private CircleImageView mIrAHome;

    private double mExtraHogarMascotaLat;
    private double mExtraHogarMascotaLng;

    private String mExtraNumeroCelular;
    private int mExtraIdMascota;
    private ArrayList<Token> tokens;
    private Mascota mascota;
    private String mMascotaUbicacion;


    private TextView mTextViewNombreMascota;
    private TextView mTextViewRaza;
    private TextView mTextViewGenero;
    private TextView mTextViewNumeroCelular;
    private TextView mTextViewDescripcion;

    private AppSharedPreferences mAppSharedPreferences;
    private ImageView mImageViewLlamarPropietarioMascota;


    //ESCUCHA CUANDO EL USARIO ESTE EN MOVIMIENTO
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mMascotaEncontradaLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    mMarkerMascotaEncontrada =  mMapa.addMarker(new MarkerOptions().position(mMascotaEncontradaLatLng).title("Mascota encontrada").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ubicacion_mascota)));
                    mMarkerHogarMascotaEncontrada = mMapa.addMarker(new MarkerOptions().position(mHogarMascotaEncontradaLatLng).title("Hogar de la mascota").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home)));

                    mMarkerHogarMascotaEncontrada.showInfoWindow();

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(mMarkerMascotaEncontrada.getPosition());
                    builder.include(mMarkerHogarMascotaEncontrada.getPosition());

                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 180);
                    mMapa.animateCamera(cu);

                    detenerLocalizacion();

                    }

                }
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_notificacion_mascota_encontrada);

        inicializarComponentes();

        mMapaFragment.getMapAsync(this);

        mAppSharedPreferences = new AppSharedPreferences(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        mExtraHogarMascotaLat = getIntent().getDoubleExtra("hogarMascotaLat", 0);
        mExtraHogarMascotaLng = getIntent().getDoubleExtra("hogarMascotaLng", 0);
        mExtraNumeroCelular = getIntent().getStringExtra("numeroContacto");
        mExtraIdMascota = getIntent().getIntExtra("idMascota", 0);

        mTextViewNumeroCelular.setText(mExtraNumeroCelular);


        mHogarMascotaEncontradaLatLng = new LatLng(mExtraHogarMascotaLat, mExtraHogarMascotaLng);

        obtenerMascota(mExtraIdMascota);

        mButtonNotificarMascotaEncontrada.setOnClickListener(this);

        mIrAHome.setOnClickListener(this);

        mImageViewLlamarPropietarioMascota.setOnClickListener(this);
    }


    private void inicializarComponentes(){
        mTextViewNombreMascota = findViewById(R.id.textViewNombreMascotaNotificacion);
        mTextViewRaza = findViewById(R.id.textViewRazaMascotaNotificacion);
        mTextViewGenero = findViewById(R.id.textViewGeneroMascotaNotificacion);
        mTextViewNumeroCelular = findViewById(R.id.textViewNumeroCelularNotificacion);
        mTextViewDescripcion = findViewById(R.id.textViewDescripcionMascotaNotificacion);
        mIrAHome = findViewById(R.id.btnIrAHomeDesdeNotificacionMascotaEncontrada);
        mImageViewLlamarPropietarioMascota = findViewById(R.id.btnContactarPropietarioMascota);
        mButtonNotificarMascotaEncontrada = findViewById(R.id.btnNotificarPropietarioMascota);
        mMapaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNotificarPropietarioMascota:
                obtenerPropietarioMascota();
                break;

            case R.id.btnIrAHomeDesdeNotificacionMascotaEncontrada:
                finish();
                break;

            case R.id.btnContactarPropietarioMascota:
                llamarNumeroCelular(mExtraNumeroCelular);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ElIMINA LA ACTUALIZACION DEL GPS
        detenerLocalizacion();
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

    private void obtenerPropietarioMascota() {

        try{
            TokenController.obtener(mascota.getIdCliente()).enqueue(new Callback<RHRespuesta>() {
                @Override
                public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                    if(response.isSuccessful()){
                        tokens = response.body().getTokens();
                        enviarNotificacion();
                    }
                    else{
                        Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<RHRespuesta> call, Throwable t) {
                    Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificacion",Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
        catch(Exception e){
            Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"No se pudo enviar la notificación",Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private void enviarNotificacion(){

        try {
            Geocoder geocoder = new Geocoder(MapaNotificacionMascotaEncontradaActivity.this);
            List<Address> listaDirecciones = geocoder.getFromLocation(mMascotaEncontradaLatLng.latitude, mMascotaEncontradaLatLng.longitude, 1);
            String ciudad = listaDirecciones.get(0).getLocality();
            String direccion = listaDirecciones.get(0).getAddressLine(0);
            mMascotaUbicacion = direccion + " " + ciudad;

        } catch (IOException e) {
            Log.d("Error: ", "Se ha producido un error: " + e.getMessage());
        }

        ArrayList<String> numTokens = new ArrayList<>();

        for (Token token: tokens) {
            numTokens.add(token.getToken());
        }

        Map<String, String> map = new HashMap<>();
        map.put("title","Mascota encontrada");
        map.put("body", mascota.getNombre()+" fue encontrada en: " + mMascotaUbicacion);
        map.put("idCliente",String.valueOf(mAppSharedPreferences.obtenerIdCliente()));
        map.put("numeroCelular", mAppSharedPreferences.obtenerNumeroCelular());
        map.put("nombreMascota", mascota.getNombre());
        map.put("mascotaLat",String.valueOf(mMascotaEncontradaLatLng.latitude));
        map.put("mascotaLng",String.valueOf(mMascotaEncontradaLatLng.longitude));
        FCMCuerpo fcmCuerpo = new FCMCuerpo(numTokens, "high", map);
        NotificacionController.enviarNotificacion(fcmCuerpo).enqueue(new Callback<FCMRespuesta>() {
            @Override
            public void onResponse(Call<FCMRespuesta> call, Response<FCMRespuesta> response) {

                if(response.body().getSuccess()>0){
                    Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this,"Notificacion enviada con exito",Toast.LENGTH_SHORT).show();
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

    private void detenerLocalizacion(){
        if(mLocationCallback !=null && mFusedLocation != null){
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }
    }

    public void llamarNumeroCelular(String numeroCelular) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + numeroCelular));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void obtenerMascota(int idPet) {
        MascotaController.obtener(idPet, 2).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    mascota = response.body().getMascota();
                    mTextViewNombreMascota.setText(mascota.getNombre());
                    mTextViewRaza.setText(mascota.getRaza());
                    if(mascota.getGenero()=='M'){
                        mTextViewGenero.setText("Macho");
                    }
                    else{
                        mTextViewGenero.setText("Hembra");
                    }
                    mTextViewDescripcion.setText(mascota.getDescripcion());
                }
                else{
                    Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this, "Los datos de la mascota no se pudieron cargar",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {

                Toast.makeText(MapaNotificacionMascotaEncontradaActivity.this, "Los datos de la mascota no se pudieron cargar",
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}
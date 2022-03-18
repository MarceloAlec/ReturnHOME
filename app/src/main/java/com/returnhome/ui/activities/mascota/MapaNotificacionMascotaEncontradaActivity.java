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
import com.google.android.gms.tasks.OnSuccessListener;
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

    private FusedLocationProviderClient mFusedLocation;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_notificacion_mascota_encontrada);

        inicializarComponentes();

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

        iniciarLocalizacion();
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
        mMapaFragment.getMapAsync(this);
    }

    private void iniciarLocalizacion() {
        //APARTIR DE LA VERSION 6.0 SE SOLICITA LA ACTIVACION DE PERMISOS EN TIEMPO DE EJECUCION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //SE USA LA CLASE ACTIVITYCOMPAT PARA CHECKEAR LOS PERMISOS, EN ESTE CASO EL DE UBICACIÓN
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActivado()) {
                    obtenerUbicacionActual();
                } else {
                    mostrarCuadroDialogoActivarGPS();
                }
            } else {
                solicitarPermisoUbicacion();
            }
        } else {
            if (gpsActivado()) {
                obtenerUbicacionActual();
            } else {
                mostrarCuadroDialogoActivarGPS();
            }
        }
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


    //METODO QUE SE EJECUTA CUANDO EL MAPA SE HA AGREGADO EN EL FRAGMENT CORRESPONDIENTE
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMapa = googleMap;
        //ESTABLECE EL TIPO DE MAPA COMO NORMAL
        mMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    private void obtenerUbicacionActual() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //MEDIANTE LA CONSTANTE HIGH ACCURACY SE OBTIENE LA MAYOR PRECISION POSIBLE YA QUE HACE USO DEL GPS DEL DISPOSITIVO
        mFusedLocation.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(MapaNotificacionMascotaEncontradaActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mMascotaEncontradaLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                //SE AGREGAN LOS MARCADORES QUE IDENTIFICAN LA UBICACION DEL HOGAR DE UNA MASCOTA QUE ACABA DE SER ENCONTRADA
                //Y EL HOGAR DE LA MISMA
                mMarkerMascotaEncontrada =  mMapa.addMarker(new MarkerOptions().position(mMascotaEncontradaLatLng).title("Mascota encontrada").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ubicacion_mascota)));
                mMarkerHogarMascotaEncontrada = mMapa.addMarker(new MarkerOptions().position(mHogarMascotaEncontradaLatLng).title("Hogar de la mascota").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home)));

                mMarkerHogarMascotaEncontrada.showInfoWindow();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(mMarkerMascotaEncontrada.getPosition());
                builder.include(mMarkerHogarMascotaEncontrada.getPosition());

                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 180);
                mMapa.animateCamera(cu);

            }
        });
    }

    //METODO QUE SE EJECUTA CUANDO EL USUARIO SELECCIONA LA OPCION DE PERMITIR O RECHAZAR LOS PERMISOS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActivado()) {
                        obtenerUbicacionActual();

                    } else {
                        mostrarCuadroDialogoActivarGPS();
                    }
                } else {
                    //EN CASO DE QUE EL USUARIO NO ACEPTE LOS PERMISOS, SE MOSTRARA EL ALERTDIALOG INDICANDO QUE LOS DEBE ACEPTAR
                    solicitarPermisoUbicacion();
                }
            } else {
                solicitarPermisoUbicacion();
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
        builder.setMessage("Por favor activa tu ubicación para continuar");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "Configuraciones", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ESPERA Y ESCUCHA HASTA QUE EL USUARIO ACTIVE EL GPS
                //ESTE METODO RECIBE UN CODIGO DE SOLICITUD QUE IDENTIFICA A LA PETICION
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

    //METODO QUE SE EJECUTA AL ABRIR UNA APLICACION EXTERNA QUE DEVUELVE ALGUNA INFORMACION
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE && gpsActivado()) {
            obtenerUbicacionActual();
        }
        else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActivado()){
            mostrarCuadroDialogoActivarGPS();
        }
    }

    private void solicitarPermisoUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog builder = new AlertDialog.Builder(this).create();
                builder.setCanceledOnTouchOutside(false);
                builder.setTitle("Proporciona los permisos para continuar");
                builder.setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse");
                builder.setButton( AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //MUESTRA UN CUADRO DE DIALOGO SOLICITANDO QUE SE CONCEDAN LOS PERMISOS
                        ActivityCompat.requestPermissions(MapaNotificacionMascotaEncontradaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
            //SE USA LA CLASE GEOCODER PARA TRANSFORMAR UNA COORDENADA LATITUD Y LONGITUD EN UNA DIRECCION DE CALLE
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

    public void llamarNumeroCelular(String numeroCelular) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + numeroCelular));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void obtenerMascota(int idMascota) {
        MascotaController.obtener(idMascota, 2).enqueue(new Callback<RHRespuesta>() {
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
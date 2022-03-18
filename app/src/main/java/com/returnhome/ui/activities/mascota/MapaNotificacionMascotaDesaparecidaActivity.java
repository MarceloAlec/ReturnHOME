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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.SphericalUtil;
import com.returnhome.R;
import com.returnhome.controllers.MascotaController;
import com.returnhome.utils.retrofit.FCMCuerpo;
import com.returnhome.utils.retrofit.FCMRespuesta;
import com.returnhome.models.Mascota;
import com.returnhome.controllers.NotificacionController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaNotificacionMascotaDesaparecidaActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMapa;
    private SupportMapFragment mMapaFragment;

    private FusedLocationProviderClient mFusedLocation;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_notificacion_mascota_desaparecida);

        inicializarComponentes();

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

        iniciarLocalizacion();

    }

    private void inicializarComponentes(){
        mMapaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mMapaFragment.getMapAsync(this);
        mButtonSeleccionarLugarDesaparecida = findViewById(R.id.btnSeleccionarLugarMascotaDesaparecida);
        mToolbar = findViewById(R.id.toolbar);
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

    private void instanciarAutoCompletar() {
        //SE INICIALIZA EL AUTOCOMPLETESUPPORTFRAGMENT
        mAutoCompletar = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.lugarAutocompletarHogarMascota);
        //SE ESPECIFICA LOS DATOS DEL LUGAR QUE DEVOLVERA LA API, EN ESTE CASO SU ID, LATITUD & LONGITUD Y NOMBRE DEL LUGAR
        mAutoCompletar.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        //TEXTO QUE SE MUESTRA EN EL COMPONENTE DE AUTOCOMPLETAR
        mAutoCompletar.setHint("Hogar de su mascota");
        //SE CONFIGURA UN ESCUCHADOR QUE MANEJA LA RESPUESTA
        mAutoCompletar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Log.e("ERROR:", status.toString());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {

                mUltimaUbicacionMascotaLatLng = place.getLatLng();

                //SE CAMBIA LA VISTA DEL MAPA DE ACUERDO CON EL LUGAR ESCOGIDO
                mMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(mUltimaUbicacionMascotaLatLng, 15f));
            }
        });
    }

    //METODO QUE SE EJECUTA CUANDO EL MAPA SE HA AGREGADO EN EL FRAGMENT CORRESPONDIENTE
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMapa = googleMap;
        //ESTABLECE EL TIPO DE MAPA COMO NORMAL
        mMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //SE AÑADE UN OYENTE SI EL USUARIO DESPLAZA EL MAPA
        mMapa.setOnCameraIdleListener(this);
    }

    @Override
    public void onCameraIdle() {
        try {
            //SE USA LA CLASE GEOCODER PARA TRANSFORMAR UNA COORDENADA LATITUD Y LONGITUD EN UNA DIRECCION DE CALLE
            Geocoder geocoder = new Geocoder(MapaNotificacionMascotaDesaparecidaActivity.this);
            mUltimaUbicacionMascotaLatLng = mMapa.getCameraPosition().target;
            //SE OBTIENE UNA LISTA DE DIRECCIONES CON LA INFORMACION DEL LUGAR SEGUN LA LATITUD Y LONGITUD ASIGNADA COMO PARAMETRO
            List<Address> listaDirecciones = geocoder.getFromLocation(mUltimaUbicacionMascotaLatLng.latitude, mUltimaUbicacionMascotaLatLng.longitude, 1);
            String ciudad = listaDirecciones.get(0).getLocality();
            String direccion = listaDirecciones.get(0).getAddressLine(0);
            mUltimoLugarMascota = direccion + " " + ciudad;

            mAutoCompletar.setText(mUltimoLugarMascota);

        } catch (Exception e) {
            Log.d("Error: ", "Mensaje error: " + e.getMessage());
        }
    }

    private void obtenerUbicacionActual() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //MEDIANTE LA CONSTANTE HIGH ACCURACY SE OBTIENE LA MAYOR PRECISION POSIBLE YA QUE HACE USO DEL GPS DEL DISPOSITIVO
        mFusedLocation.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(MapaNotificacionMascotaDesaparecidaActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mActualUbicacionLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(mActualUbicacionLatLng, 15f));

                limitarBusqueda();
            }
        });
    }

    //LIMITAR LAS BUSQUEDAS POR REGION
    private void limitarBusqueda() {
        //SE OBTIENE LAS COORDENADAS DE LATITUD Y LONGITUD AL MOVERSE UNA DISTANCIA DE 50000 METROS
        //POR DEFECTO EL DESPLAZAMIENTO ES CON RUMBO HACIA EL NORTE, ESTO SE MODIFICA ESPECIFICANDO EL NUMERO DE GRADOS DEL NUEVO DESPLAZAMIENTO EN EL SENTIDO DE
        //LAS AGUJAS DEL RELOJ
        LatLng noreste = SphericalUtil.computeOffset(mActualUbicacionLatLng, 50000, 45);
        LatLng sureste = SphericalUtil.computeOffset(mActualUbicacionLatLng, 50000, 225);

        //SE RESTRINGE LOS RESULTADOS DEL AUTOCOMPLETAR
        //SE ESTABLECEN LAS COORDENADAS DE NORESTE Y SURESTE PARA LIMITAR LAS BUSQUEDAS DENTRO DE UN CUADRO DELIMITADOR
        mAutoCompletar.setLocationRestriction(RectangularBounds.newInstance(sureste, noreste));
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
                        ActivityCompat.requestPermissions(MapaNotificacionMascotaDesaparecidaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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

                ActivityCompat.requestPermissions(MapaNotificacionMascotaDesaparecidaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
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
}
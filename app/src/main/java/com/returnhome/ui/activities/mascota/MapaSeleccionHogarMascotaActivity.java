package com.returnhome.ui.activities.mascota;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.returnhome.models.Mascota;
import com.returnhome.ui.activities.nfc.DetalleInfoEscrituraActivity;
import com.returnhome.utils.AppSharedPreferences;
import com.returnhome.utils.retrofit.RHRespuesta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaSeleccionHogarMascotaActivity extends AppCompatActivity implements OnMapReadyCallback {

    //PROPORCIONA ACCESO A LA VISTA Y LOS DATOS DEL MAPA
    private GoogleMap mMapa;
    //ADMINISTRA EL CICLO DE VIDA DEL MAPA
    private SupportMapFragment mMapaFragment;

    //VARIABLE QUE PERMITIRA ESTABLECER LA CALIDAD DE SERVICIO
    //PARA LAS PETICIONES DEL FUSEDLOCATIONPROVIDER
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private Spinner mSpinner;

    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutoCompletar;

    private LatLng mHogarMascotaLatLng;
    private boolean mascotaSeleccionada = false;

    private GoogleMap.OnCameraIdleListener mCameraListener;

    private AppSharedPreferences mAppSharedPreferences;
    private ArrayList<Mascota> mascotaArrayList;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    //ALMACENA LA LATITUD Y LONGITUD ACTUAL
    private LatLng mActualLatLng;

    private ArrayAdapter mArrayAdapterMascotas;
    private Button mButtonSeleccionHogarMascota;
    private androidx.appcompat.widget.Toolbar mToolbar;

    //OBTIENE LA UBICACION DEL USUARIO
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {

            mActualLatLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

            mMapa.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(mActualLatLng.latitude, mActualLatLng.longitude))
                    .zoom(15f)
                    .build()
            ));

            limitarBusqueda();
            detenerLocalizacion();
        }
    };

    //LIMITAR LAS BUSQUEDAS POR REGION
    private void limitarBusqueda() {
        //DISTANCIA PARA LIMITAR LAS BUSQUEDAS EN M
        LatLng norte = SphericalUtil.computeOffset(mActualLatLng, 5000, 0);
        LatLng sur = SphericalUtil.computeOffset(mActualLatLng, 5000, 180);
        mAutoCompletar.setLocationBias(RectangularBounds.newInstance(sur, norte));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_seleccion_hogar_mascota);

        inicializarComponentes();


        //INICIA O DETIENE LA UBICACION DEL USUARIO
        //INSTANCIO EL CLIENTE DE SERVICIOS DE UBICACION
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Hogar de la Mascota");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAppSharedPreferences = new AppSharedPreferences(this);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mascotaSeleccionada = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mButtonSeleccionHogarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mascotaSeleccionada) {
                    Intent intent = new Intent(MapaSeleccionHogarMascotaActivity.this, DetalleInfoEscrituraActivity.class);
                    intent.putExtra("hogarMascotaLat", mHogarMascotaLatLng.latitude);
                    intent.putExtra("hogarMascotaLng", mHogarMascotaLatLng.longitude);
                    intent.putExtra("mascota", (Mascota) mSpinner.getSelectedItem());
                    startActivity(intent);
                } else {
                    Toast.makeText(MapaSeleccionHogarMascotaActivity.this, "Debe seleccionar una mascota previamente registrada en la aplicación", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        mPlaces = Places.createClient(this);
        instanciarAutoCompletarHogarMascota();
        movimientoCamara();
    }

    private void inicializarComponentes() {

        //SE OBTIENE EL SUPPORTMAPFRAGMENT
        mMapaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        //LA ACTIVIDAD ACTUAL ADMINISTRARÁ EL MAPA
        mMapaFragment.getMapAsync(this);

        mSpinner = findViewById(R.id.spinnerMisMascotas);
        mButtonSeleccionHogarMascota = findViewById(R.id.btnSeleccionarHogarMascota);
        mToolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        obtenerMascotas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerLocalizacion();
    }

    private void detenerLocalizacion() {
        if (mLocationCallback != null && mFusedLocation != null) {
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }
    }

    private void obtenerMascotas() {
        int idCliente = mAppSharedPreferences.obtenerIdCliente();

        MascotaController.obtener(idCliente, 1).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if (response.isSuccessful()) {
                    mascotaArrayList = response.body().getMascotas();
                    mostrarLista(mascotaArrayList);
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(MapaSeleccionHogarMascotaActivity.this, "No se pudo cargar sus mascotas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarLista(ArrayList<Mascota> mascotas) {
        mArrayAdapterMascotas = new ArrayAdapter(MapaSeleccionHogarMascotaActivity.this, R.layout.lista_mis_mascotas, mascotaArrayList);
        mSpinner.setAdapter(mArrayAdapterMascotas);
    }

    private void movimientoCamara() {
        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    //CUANDO EL USARIO CAMBIA LA POSICION DE LA CAMARA EN EL MAPA
                    Geocoder geocoder = new Geocoder(MapaSeleccionHogarMascotaActivity.this);
                    mHogarMascotaLatLng = mMapa.getCameraPosition().target;
                    List<Address> addressList = geocoder.getFromLocation(mHogarMascotaLatLng.latitude, mHogarMascotaLatLng.longitude, 1);
                    String city = addressList.get(0).getLocality();
                    String address = addressList.get(0).getAddressLine(0);

                    mAutoCompletar.setText(address + " " + city);

                } catch (Exception e) {
                    Log.d("Error: ", "Mensaje error: " + e.getMessage());
                }
            }
        };
    }

    private void instanciarAutoCompletarHogarMascota() {
        mAutoCompletar = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.lugarAutocompletarHogarMascota);
        mAutoCompletar.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoCompletar.setHint("Hogar de su mascota");
        mAutoCompletar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {

                mHogarMascotaLatLng = place.getLatLng();
                mMapa.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(mHogarMascotaLatLng.latitude, mHogarMascotaLatLng.longitude))
                        .zoom(15f)
                        .build()
                ));
            }
        });
    }

    //METODO QUE SE EJECUTA CUANDO EL MAPA SE HA AGREGADO EN EL FRAGMENT CORRESPONDIENTE
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMapa = googleMap;
        //ESTABLECE EL TIPO DE MAPA COMO NORMAL
        mMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //SE AÑADE UN OYENTE CUANDO LA CAMARA SE MUEVA
        mMapa.setOnCameraIdleListener(mCameraListener);

        //CONFIGURACION DE LA UBICACION
        mLocationRequest = LocationRequest.create();
        //SE ESTABLECE LA FRECUENCIA EN ms CON LA QUE LLEGARAN LAS ACTUALIZACIONES DE UBICACIÓN
        mLocationRequest.setInterval(1000);
        //SE ESTABLECE UNA FRECUENCIA MENOR A SET INTERVAL PARA GANAR PRIORIDAD CON OTRAS APLICACIONES
        mLocationRequest.setFastestInterval(1000);
        //PARA OBTENER LA UBICACION CON LA MAYOR PRECISION POSIBLE YA QUE HACE USO DEL GPS
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        iniciarLocalizacion();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActivado()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

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

    private void iniciarLocalizacion() {
        //APARTIR DE LA VERSION 6.0 SE SOLICITA LA ACTIVACION DE PERMISOS EN TIEMPO DE EJECUCION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //SE USA LA CLASE ACTIVITYCOMPAT PARA CHECKEAR LOS PERMISOS, EN ESTE CASO EL DE UBICACIÓN
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActivado()) {
                    //AL LLAMAR AL EVENTO REQUESTLOCALTIONUPDATES, SE EJECUTA EL EVENTO LOCATIONCALLBACK
                    //EL TERCER PARAMETRO INDICA QUE LA PETICION DE UBICACION SE EJECUTARA EN UN HILO DIFERENTE
                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                } else {
                    mostrarCuadroDialogoActivarGPS();
                }
            } else {
                solicitarPermisoUbicacion();
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

        if (gpsActivado()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        else if (!gpsActivado()){
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
                        //HABILITA LOS PERMISOS PARA USAR LA UBICACION
                        ActivityCompat.requestPermissions(MapaSeleccionHogarMascotaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
                ActivityCompat.requestPermissions(MapaSeleccionHogarMascotaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }
}
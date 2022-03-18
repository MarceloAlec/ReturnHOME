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
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.returnhome.models.Mascota;
import com.returnhome.ui.activities.nfc.MapaDetalleInfoEscrituraActivity;
import com.returnhome.utils.AppSharedPreferences;
import com.returnhome.utils.retrofit.RHRespuesta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaSeleccionHogarMascotaActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    //PROPORCIONA ACCESO A LA VISTA Y LOS DATOS DEL MAPA
    private GoogleMap mMapa;
    //ADMINISTRA EL CICLO DE VIDA DEL MAPA
    private SupportMapFragment mMapaFragment;

    private FusedLocationProviderClient mFusedLocation;

    private Spinner mSpinner;

    private PlacesClient mPlaces;
    private AutocompleteSupportFragment mAutoCompletar;

    private LatLng mHogarMascotaLatLng;
    private boolean mascotaSeleccionada = false;

    private AppSharedPreferences mAppSharedPreferences;
    private ArrayList<Mascota> mascotaArrayList;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    //ALMACENA LA LATITUD Y LONGITUD ACTUAL
    private LatLng mActualLatLng;

    private ArrayAdapter mArrayAdapterMascotas;
    private Button mButtonSeleccionHogarMascota;
    private androidx.appcompat.widget.Toolbar mToolbar;


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
                    Intent intent = new Intent(MapaSeleccionHogarMascotaActivity.this, MapaDetalleInfoEscrituraActivity.class);
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
            //SE INICIALIZA PLACES SDK PARA ANDROID
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        mPlaces = Places.createClient(this);
        instanciarAutoCompletarHogarMascota();

        iniciarLocalizacion();

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
    protected void onStart() {
        super.onStart();
        obtenerMascotas();
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

    private void instanciarAutoCompletarHogarMascota() {

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

                mHogarMascotaLatLng = place.getLatLng();

                //SE CAMBIA LA VISTA DEL MAPA DE ACUERDO CON EL LUGAR ESCOGIDO
                mMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(mHogarMascotaLatLng, 15f));
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

    //CUANDO EL MAPA DEJA DE DESPLAZARSE SE EJECUTA EL SIGUIENTE METODO
    @Override
    public void onCameraIdle() {
        try {

            //SE USA LA CLASE GEOCODER PARA TRANSFORMAR UNA COORDENADA LATITUD Y LONGITUD EN UNA DIRECCION DE CALLE
            Geocoder geocoder = new Geocoder(MapaSeleccionHogarMascotaActivity.this);
            mHogarMascotaLatLng = mMapa.getCameraPosition().target;
            //SE OBTIENE UNA LISTA DE DIRECCIONES CON LA INFORMACION DEL LUGAR SEGUN LA LATITUD Y LONGITUD ASIGNADA COMO PARAMETRO
            List<Address> listaDirecciones = geocoder.getFromLocation(mHogarMascotaLatLng.latitude, mHogarMascotaLatLng.longitude, 1);
            String ciudad = listaDirecciones.get(0).getLocality();
            String direccion = listaDirecciones.get(0).getAddressLine(0);

            //SE AÑADE LA DIRECCION OBTENIDA EN EL COMPONENTE DEL AUTOCOMPLETAR
            mAutoCompletar.setText(direccion + " " + ciudad);

        } catch (Exception e) {
            Log.e("Error: ",e.getMessage());
        }
    }

    private void obtenerUbicacionActual() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //MEDIANTE LA CONSTANTE HIGH ACCURACY SE OBTIENE LA MAYOR PRECISION POSIBLE YA QUE HACE USO DEL GPS DEL DISPOSITIVO
        mFusedLocation.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(MapaSeleccionHogarMascotaActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mActualLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(mActualLatLng, 15f));

                limitarBusqueda();
            }
        });
    }

    //LIMITAR LAS BUSQUEDAS POR REGION
    private void limitarBusqueda() {
        //SE OBTIENE LAS COORDENADAS DE LATITUD Y LONGITUD AL MOVERSE UNA DISTANCIA DE 50000 METROS
        //POR DEFECTO EL DESPLAZAMIENTO ES CON RUMBO HACIA EL NORTE, ESTO SE MODIFICA ESPECIFICANDO EL NUMERO DE GRADOS DEL NUEVO DESPLAZAMIENTO EN EL SENTIDO DE
        //LAS AGUJAS DEL RELOJ
        LatLng noreste = SphericalUtil.computeOffset(mActualLatLng, 50000, 45);
        LatLng sureste = SphericalUtil.computeOffset(mActualLatLng, 50000, 225);

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
                        ActivityCompat.requestPermissions(MapaSeleccionHogarMascotaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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

                ActivityCompat.requestPermissions(MapaSeleccionHogarMascotaActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }
}
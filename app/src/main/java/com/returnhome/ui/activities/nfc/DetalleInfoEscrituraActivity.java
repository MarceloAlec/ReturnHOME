package com.returnhome.ui.activities.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.returnhome.models.Mascota;
import com.returnhome.utils.AppSharedPreferences;


import de.hdodenhof.circleimageview.CircleImageView;

public class DetalleInfoEscrituraActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    private AppSharedPreferences mAppSharedPreferences;

    private TextView mTextViewNombreMascota;
    private TextView mTextViewRaza;
    private TextView mTextViewGenero;
    private TextView mTextViewNumeroCelular;

    private GoogleMap mMapa;
    private SupportMapFragment mMapaFragment;

    private LocationRequest mLocationRequest;

    private double mExtraHogarMascotaLat;
    private double mExtraHogarMascotaLng;

    private Mascota mMascota;

    private LatLng mHogarMascotaLatLng;

    private Button mButtonEscribirEtiqueta;
    private CircleImageView mIrASeleccionarHogarMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_info_escritura);

        inicializarComponentes();

        mMapaFragment.getMapAsync(this);
        mAppSharedPreferences = new AppSharedPreferences(this);

        mExtraHogarMascotaLat = getIntent().getDoubleExtra("hogarMascotaLat", 0);
        mExtraHogarMascotaLng = getIntent().getDoubleExtra("hogarMascotaLng", 0);
        mMascota = (Mascota)getIntent().getSerializableExtra("pet");

        mTextViewNombreMascota.setText(mMascota.getNombre());
        mTextViewRaza.setText(mMascota.getRaza());
        mTextViewGenero.setText(String.valueOf(mMascota.getGenero()));
        mTextViewNumeroCelular.setText(mAppSharedPreferences.obtenerNumeroCelular());

        mHogarMascotaLatLng = new LatLng(mExtraHogarMascotaLat, mExtraHogarMascotaLng);

        mIrASeleccionarHogarMascota.setOnClickListener(this);

        mButtonEscribirEtiqueta.setOnClickListener(this);
    }



    private void inicializarComponentes() {
        mTextViewNombreMascota = findViewById(R.id.textViewNombreMascotaEscritura);
        mTextViewRaza = findViewById(R.id.textViewRazaMascotaEscritura);
        mTextViewGenero = findViewById(R.id.textViewGeneroMascotaEscritura);
        mTextViewNumeroCelular = findViewById(R.id.textViewNumeroCelularEscritura);
        mButtonEscribirEtiqueta = findViewById(R.id.btnEscribirEtiquetaAhora);
        mIrASeleccionarHogarMascota = findViewById(R.id.btnIrASeleccionHogarMascota);
        mMapaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMapa = googleMap;
        mMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(5);

        mMapa.addMarker(new MarkerOptions().position(mHogarMascotaLatLng).title("Hogar de "+ mMascota.getNombre()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))).showInfoWindow();

        mMapa.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mHogarMascotaLatLng)
                        .zoom(15f)
                        .build()
        ));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEscribirEtiquetaAhora:
                Intent intent = new Intent(DetalleInfoEscrituraActivity.this, EscrituraEtiquetaActivity.class);
                intent.putExtra("idMascota", mMascota.getIdMascota());
                intent.putExtra("hogarMascotaLat", mExtraHogarMascotaLat);
                intent.putExtra("hogarMascotaLng", mExtraHogarMascotaLng);
                startActivity(intent);
                break;

            case R.id.btnIrASeleccionHogarMascota:
                finish();
                break;
        }
    }
}
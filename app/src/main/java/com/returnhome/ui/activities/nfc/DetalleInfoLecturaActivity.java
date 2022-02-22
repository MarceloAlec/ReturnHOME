package com.returnhome.ui.activities.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;
import com.returnhome.ui.activities.cliente.HomeActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInfoLecturaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraHogarMascotaLat;
    private double mExtraHogarMascotaLng;
    private String mExtraNumeroCelular;
    private int mExtraIdMascota;

    private LatLng mHogarMascotaLatLng;

    private Mascota mascota;

    private CircleImageView mCircleImageIrASeleccionOpcionNFC;

    private TextView mTextViewNombreMascota;
    private TextView mTextViewRaza;
    private TextView mTextViewGenero;
    private TextView mTextViewNumeroCelular;

    private GoogleMap mMapa;
    private SupportMapFragment mMapaFragment;
    private LocationRequest mLocationRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_info_lectura);

        inicializarComponentes();

        mMapaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mMapaFragment.getMapAsync(this);

        mExtraHogarMascotaLat = getIntent().getDoubleExtra("hogarMascotaLat", 0);
        mExtraHogarMascotaLng = getIntent().getDoubleExtra("hogarMascotaLng", 0);
        mExtraNumeroCelular = getIntent().getStringExtra("numeroContacto");
        mExtraIdMascota = getIntent().getIntExtra("idMascota", 0);

        mHogarMascotaLatLng = new LatLng(mExtraHogarMascotaLat, mExtraHogarMascotaLng);

        mTextViewNumeroCelular.setText(mExtraNumeroCelular);

        mCircleImageIrASeleccionOpcionNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleInfoLecturaActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        obtenerMascota(mExtraIdMascota);
    }

    private void inicializarComponentes(){
        mTextViewNombreMascota = findViewById(R.id.textViewNombreMascotaLectura);
        mTextViewRaza = findViewById(R.id.textViewRazaMascotaLectura);
        mTextViewGenero = findViewById(R.id.textViewGeneroMascotaLectura);
        mTextViewNumeroCelular = findViewById(R.id.textViewNumeroCelularLectura);
        mCircleImageIrASeleccionOpcionNFC = findViewById(R.id.btnIrAHomeDesdeDetalleLectura);
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


        mMapa.addMarker(new MarkerOptions().position(mHogarMascotaLatLng).title("Hogar de la mascota").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home))).showInfoWindow();

        mMapa.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mHogarMascotaLatLng)
                        .zoom(15f)
                        .build()
        ));

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
                }
                else{
                    Toast.makeText(DetalleInfoLecturaActivity.this, "Los datos de la mascota no se pudieron cargar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(DetalleInfoLecturaActivity.this, "Los datos de la mascota no se pudieron cargar", Toast.LENGTH_LONG).show();

            }
        });
    }


}
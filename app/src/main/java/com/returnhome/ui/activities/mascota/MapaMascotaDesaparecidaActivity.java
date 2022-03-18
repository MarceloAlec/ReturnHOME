package com.returnhome.ui.activities.mascota;

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
import com.returnhome.utils.retrofit.RHRespuesta;
import com.returnhome.ui.activities.cliente.HomeActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaMascotaDesaparecidaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraMascotaLat;
    private double mExtraMascotaLng;
    private String mExtraNombreMascota;
    private int mExtraIdMascota;

    private GoogleMap mMapa;
    private SupportMapFragment mMapaFragment;

    private LatLng mMascotaDesaparecidaLatLng;

    private CircleImageView mCircleImageIrAHome;

    private TextView mTextViewNombreMascota;
    private TextView mTextViewRaza;
    private TextView mTextViewGenero;
    private TextView mTextViewDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_mascota_desaparecida);

        inicializarComponentes();

        mExtraIdMascota = getIntent().getIntExtra("idMascota", 0);
        mExtraNombreMascota = getIntent().getStringExtra("nombreMascota");
        mExtraMascotaLat = getIntent().getDoubleExtra("mascotaLat", 0);
        mExtraMascotaLng = getIntent().getDoubleExtra("mascotaLng", 0);

        mMascotaDesaparecidaLatLng = new LatLng(mExtraMascotaLat, mExtraMascotaLng);

        obtenerMascota(mExtraIdMascota);

        mCircleImageIrAHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapaMascotaDesaparecidaActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void inicializarComponentes(){
        mTextViewNombreMascota = findViewById(R.id.textViewNombreMascotaDesaparecida);
        mTextViewRaza = findViewById(R.id.textViewRazaMascotaDesaparecida);
        mTextViewGenero = findViewById(R.id.textViewGeneroMascotaDesaparecida);
        mTextViewDescripcion = findViewById(R.id.textViewDescripcionMascotaDesaparecida);
        mCircleImageIrAHome = findViewById(R.id.btnIrAHomeDesdeMascotaDesaparecida);

        mMapaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mMapaFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMapa = googleMap;
        mMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMapa.addMarker(new MarkerOptions().position(mMascotaDesaparecidaLatLng)
                .title(mExtraNombreMascota +" fue visto/a por ultima vez aqui")
                .icon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_ubicacion_mascota))).showInfoWindow();

        mMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(mMascotaDesaparecidaLatLng, 15f));
    }

    private void obtenerMascota(int idMascota) {
        MascotaController.obtener(idMascota, 2).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    Mascota mascota = response.body().getMascota();
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
                    Toast.makeText(MapaMascotaDesaparecidaActivity.this, "No se pudo cargar los datos de la mascota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(MapaMascotaDesaparecidaActivity.this, "No se pudo cargar los datos de la mascota", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
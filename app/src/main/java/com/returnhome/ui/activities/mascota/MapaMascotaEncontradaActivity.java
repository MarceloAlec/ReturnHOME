package com.returnhome.ui.activities.mascota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.returnhome.controllers.ClienteController;
import com.returnhome.models.Cliente;
import com.returnhome.models.RHRespuesta;
import com.returnhome.ui.activities.cliente.HomeActivity;
import com.returnhome.ui.activities.cliente.SeleccionOpcionAjustesActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaMascotaEncontradaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double mExtraMascotaEncontradaLat;
    private double mExtraMascotaEncontradLng;
    private String mExtraNombreMascotaEncontrada;
    private int mExtraIdCliente;

    private GoogleMap mMapa;
    private SupportMapFragment mMapaFragment;
    private LocationRequest mLocationRequest;

    private LatLng mMascotaEncontradaLatLng;

    private CircleImageView mCircleImageIrAHome;

    private TextView mTextViewNombreCliente;
    private TextView mTextViewEmail;
    private TextView mTextViewNumeroCelular;
    private ImageView mImageViewLLamarCliente;
    private String numeroCelular;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_mascota_encontrada);

        inicializarComponentes();

        mMapaFragment.getMapAsync(this);

        mExtraIdCliente = getIntent().getIntExtra("idCliente", 0);
        mExtraNombreMascotaEncontrada = getIntent().getStringExtra("nombreMascota");
        mExtraMascotaEncontradaLat = getIntent().getDoubleExtra("mascotaLat", 0);
        mExtraMascotaEncontradLng = getIntent().getDoubleExtra("mascotaLng", 0);

        mMascotaEncontradaLatLng = new LatLng(mExtraMascotaEncontradaLat, mExtraMascotaEncontradLng);

        obtenerCliente(mExtraIdCliente);

        mCircleImageIrAHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapaMascotaEncontradaActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mImageViewLLamarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarNumeroCelular(numeroCelular);
            }
        });
    }

    private void inicializarComponentes(){
        mTextViewNombreCliente = findViewById(R.id.textViewNombreClienteEncontroMascota);
        mTextViewEmail = findViewById(R.id.textViewEmailClienteEncontroMascota);
        mTextViewNumeroCelular = findViewById(R.id.textViewNumeroCelularClienteEncontroMascota);
        mImageViewLLamarCliente = findViewById(R.id.btnContactarClienteEncontroMascota);
        mCircleImageIrAHome = findViewById(R.id.btnIrAHomeDesdeMapaMascotaEncontrada);
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

        mMapa.addMarker(new MarkerOptions().position(mMascotaEncontradaLatLng)
                .title(mExtraNombreMascotaEncontrada +" se encuentra aquí")
                .icon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_ubicacion_mascota))).showInfoWindow();

        mMapa.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mMascotaEncontradaLatLng)
                        .zoom(15f)
                        .build()
        ));
    }



    private void obtenerCliente(int mExtraIdClient) {
        ClienteController.obtener(mExtraIdClient).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    Cliente cliente = response.body().getCliente();
                    mTextViewNombreCliente.setText(cliente.getNombre());
                    mTextViewEmail.setText(cliente.getEmail());
                    mTextViewNumeroCelular.setText(cliente.getNumeroCelular());
                    numeroCelular = response.body().getCliente().getNumeroCelular();
                }
                else{
                    Toast.makeText(MapaMascotaEncontradaActivity.this, "No se pudo cargar los datos del cliente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(MapaMascotaEncontradaActivity.this, "No se pudo cargar los datos del cliente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void llamarNumeroCelular(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}
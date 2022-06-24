package com.returnhome.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.Mascota;
import com.returnhome.ui.adapters.MiMascotaAdapter;
import com.returnhome.utils.AppSharedPreferences;
import com.returnhome.utils.retrofit.RHRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MiMascotaFragment extends Fragment implements View.OnClickListener {

    private MiMascotaAdapter mMiMascotaAdapter;
    private RecyclerView mRecyclerViewMascotas;
    private AppSharedPreferences mAppSharedPreferences;
    private FloatingActionButton mFloatingButtonAgregar;
    private BottomSheetDialog mBottomSheetDialog;
    private Button mButtonAgregarActualizarMascota;
    private TextInputEditText mTextInputNombre;
    private TextInputEditText mTextInputRaza;
    private TextInputEditText mTextInputDescripcion;
    private RadioButton mRadioButtonGeneroMacho;
    private ArrayList<Mascota> mascotaArrayList;

    public MiMascotaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mi_mascota, container, false);

        mascotaArrayList = new ArrayList<>();
        mRecyclerViewMascotas = view.findViewById(R.id.recyclerViewMisMascotas);
        mAppSharedPreferences = new AppSharedPreferences(getContext());
        mFloatingButtonAgregar = view.findViewById(R.id.fabAgregarMascota);

        inicializarComponentes();

        mFloatingButtonAgregar.setOnClickListener(this);

        mButtonAgregarActualizarMascota.setOnClickListener(this);

        obtenerMascotas();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabAgregarMascota:

                mButtonAgregarActualizarMascota.setText("Agregar");
                mBottomSheetDialog.show();
                break;

            case R.id.btnAgregarActualizarMascota:
                clicAgregarActualizar();
                break;
        }
    }

    private void inicializarComponentes(){
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mBottomSheetDialog.setContentView(R.layout.popup_agregar_actualizar);
        mBottomSheetDialog.setCanceledOnTouchOutside(true);

        mButtonAgregarActualizarMascota = mBottomSheetDialog.findViewById(R.id.btnAgregarActualizarMascota);
        mTextInputNombre = mBottomSheetDialog.findViewById(R.id.textInputNombreMascota);
        mTextInputRaza = mBottomSheetDialog.findViewById(R.id.textInputRaza);
        mTextInputDescripcion = mBottomSheetDialog.findViewById(R.id.textInputDescripcion);
        mRadioButtonGeneroMacho = mBottomSheetDialog.findViewById(R.id.radioButtonGeneroMacho);
    }

    private void clicAgregarActualizar() {
        String nombre = mTextInputNombre.getText().toString();
        String raza = mTextInputRaza.getText().toString();
        String descripcion = mTextInputDescripcion.getText().toString();
        char genero = ((mRadioButtonGeneroMacho.isChecked() ? 'M' : 'F'));

        if(!nombre.isEmpty() && !raza.isEmpty()){
            registrarMascota(new Mascota(nombre,raza,genero,descripcion, false, mAppSharedPreferences.obtenerIdCliente()));
        }
        else{
            Toast.makeText(getContext(), "Ingrese todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    private void registrarMascota(Mascota mascota) {
        MascotaController.registrar(mascota).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.code() == 201){
                    mascota.setIdMascota(response.body().getMascota().getIdMascota());
                    mascotaArrayList.add(mascota);
                    showList(mascotaArrayList);
                    mBottomSheetDialog.dismiss();
                    mTextInputNombre.getText().clear();
                    mTextInputNombre.clearFocus();
                    mTextInputRaza.getText().clear();
                    mTextInputRaza.clearFocus();
                    mTextInputDescripcion.getText().clear();
                    mTextInputDescripcion.clearFocus();
                    mRadioButtonGeneroMacho.setChecked(true);
                }
                else{
                    Toast.makeText(getContext(), "Mascota añadida con éxito", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(getContext(), "Ocurrio un problema al agregar", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void obtenerMascotas() {
        int idCliente = mAppSharedPreferences.obtenerIdCliente();

        MascotaController.obtener(idCliente, 1).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    mascotaArrayList = response.body().getMascotas();
                    showList(mascotaArrayList);
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {

            }
        });
    }

    private void showList(ArrayList<Mascota> mascotas) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //MOSTRAR LOS VIEWS DE LA LISTA DE MANERA LINEAL
        mRecyclerViewMascotas.setLayoutManager(linearLayoutManager);
        //SE ENVIA LA LISTA DE MASCOTAS A PETPROVIDER
        mMiMascotaAdapter = new MiMascotaAdapter(getContext(), mascotas);
        mRecyclerViewMascotas.setAdapter(mMiMascotaAdapter);
    }
}
package com.returnhome.ui.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.returnhome.R;
import com.returnhome.controllers.ClienteController;
import com.returnhome.models.Cliente;
import com.returnhome.utils.AppSharedPreferences;
import com.returnhome.utils.retrofit.RHRespuesta;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarInfoActivity extends AppCompatActivity {

    private Button mButtonActualizar;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputNombre;

    private CountryCodePicker mCountryCodePicker;
    private TextInputEditText mTextInputNumeroCelular;

    private String nombre;
    private String email;
    private String numeroCelular;

    private AppSharedPreferences mAppSharedPreferences;
    private Cliente mCliente;

    private CircleImageView mCircleImageIrASeleccioOpcionAjustes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_info);

        inicializarComponentes();

        mAppSharedPreferences = new AppSharedPreferences(ActualizarInfoActivity.this);

        obtenerCliente();

        mButtonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar();
            }
        });

        mCircleImageIrASeleccioOpcionAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void obtenerCliente() {

        ClienteController.obtener(mAppSharedPreferences.obtenerIdCliente()).enqueue(new Callback<RHRespuesta>() {
            //AÑADO EL OBJETO CALLBACK PARA CONTROLAR LOS EVENTOS DE LA PETICIÓN
            @Override
            //METODO QUE SE EJECUTA CUANDO LA PETICION TRAE DATOS
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {

                if(response.code() == 200){
                    mCliente = response.body().getCliente();
                    mTextInputNombre.setText(mCliente.getNombre());
                    mTextInputEmail.setText(mCliente.getEmail());
                    String[] codigonumeroCelular = mCliente.getNumeroCelular().split(" ");
                    mTextInputNumeroCelular.setText(codigonumeroCelular[1]);
                    mCountryCodePicker.setCountryForPhoneCode(Integer.valueOf(codigonumeroCelular[0]));
                }
                else{
                    mButtonActualizar.setEnabled(false);
                    Toast.makeText(ActualizarInfoActivity.this, "No se pudo cargar su información", Toast.LENGTH_SHORT).show();
                }
            }

            //METODO QUE SE EJECUTA CUANDO LA PETICIÓN FALLA
            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(ActualizarInfoActivity.this, "No se pudo cargar su información", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizar() {
        nombre = mTextInputNombre.getText().toString();
        email = mTextInputEmail.getText().toString();
        numeroCelular = mCountryCodePicker.getSelectedCountryCodeWithPlus()+" "+ mTextInputNumeroCelular.getText().toString();

        if(!nombre.isEmpty() && !email.isEmpty() && !numeroCelular.isEmpty()){

            ClienteController.actualizarInfo(new Cliente(mAppSharedPreferences.obtenerIdCliente(), nombre, email, numeroCelular)).enqueue(new Callback<Void>() {
                //AÑADO EL OBJETO CALLBACK PARA CONTROLAR LOS EVENTOS DE LA PETICIÓN
                @Override
                //METODO QUE SE EJECUTA CUANDO LA PETICION TRAE DATOS
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if(response.isSuccessful()){
                        Toast.makeText(ActualizarInfoActivity.this, "Información actualizada con exito", Toast.LENGTH_SHORT).show();
                        mAppSharedPreferences.guardarNombreCliente(nombre);
                        mAppSharedPreferences.guardarNumeroCelular(numeroCelular);
                        finish();
                    }
                    else{
                        Toast.makeText(ActualizarInfoActivity.this, "La información no se actualizado", Toast.LENGTH_SHORT).show();
                    }
                }

                //METODO QUE SE EJECUTA CUANDO LA PETICIÓN FALLA
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ActualizarInfoActivity.this, "No se pudo actualizar sus datos", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void inicializarComponentes() {
        mButtonActualizar = findViewById(R.id.btnActualizarCliente);
        mTextInputNombre = findViewById(R.id.textInputNombreClienteActualizar);
        mTextInputEmail = findViewById(R.id.textInputEmailClienteActualizar);
        mCountryCodePicker = findViewById(R.id.countryCodePickerActualizarPerfil);
        mTextInputNumeroCelular = findViewById(R.id.textInputNumeroCelularActualizar);
        mCircleImageIrASeleccioOpcionAjustes = findViewById(R.id.btnIrASeleccionOpcionAjustes);
    }
}
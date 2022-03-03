package com.returnhome.ui.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.returnhome.R;
import com.returnhome.controllers.ClienteController;
import com.returnhome.models.Cliente;
import com.returnhome.models.RHRespuesta;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private Button mButtonRegistrar;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputNombre;
    private TextInputEditText mTextInputPassword;
    private CountryCodePicker mCountryCodePicker;
    private TextInputEditText mTextInputNumeroCelular;
    private androidx.appcompat.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inicializarComponentes();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Registro de usuario");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });


    }

    private void inicializarComponentes() {
        mButtonRegistrar = findViewById(R.id.btnRegistrar);
        mTextInputNombre = findViewById(R.id.textInputNombreRegistro);
        mTextInputEmail = findViewById(R.id.textInputEmailRegistro);
        mTextInputPassword = findViewById(R.id.textInputPasswordRegistro);
        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mTextInputNumeroCelular = findViewById(R.id.textInputNumeroCelular);
        mToolbar = findViewById(R.id.toolbar);
    }

//////////////////
    private void registrar() {
        String nombre = mTextInputNombre.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String codigoPais = mCountryCodePicker.getSelectedCountryCodeWithPlus();
        String numeroCelular = mTextInputNumeroCelular.getText().toString();

        if(!nombre.isEmpty() && !email.isEmpty() && !password.isEmpty() && !numeroCelular.isEmpty()){
            ClienteController
                    .registrar(new Cliente(nombre,email,password,codigoPais+" "+numeroCelular))
                    .enqueue(new Callback<RHRespuesta>() {
                @Override
                public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(RegistroActivity.this, "La cuenta fue creada con exito", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(RegistroActivity.this, "El correo ya se encuentra registrado", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<RHRespuesta> call, Throwable t) {

                    Toast.makeText(RegistroActivity.this, "Registro fallido", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}
///////////////////////
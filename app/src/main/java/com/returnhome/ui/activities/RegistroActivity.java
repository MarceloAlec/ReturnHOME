package com.returnhome.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.returnhome.R;
import com.returnhome.controllers.ClienteController;
import com.returnhome.controllers.NotificacionController;
import com.returnhome.controllers.TokenController;
import com.returnhome.ui.activities.cliente.HomeActivity;
import com.returnhome.utils.AppConfig;
import com.returnhome.models.RHRespuesta;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {

    private Button mButtonRegister;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInputPassword;
    private CountryCodePicker mCountryCodePicker;
    private TextInputEditText mTextInputPhoneNumber;
    private androidx.appcompat.widget.Toolbar mToolbar;

    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();

        mAppConfig = new AppConfig(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Registro de usuario");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });


    }

    private void initializeComponents() {
        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputNameRegister);
        mTextInputEmail = findViewById(R.id.textInputEmailRegister);
        mTextInputPassword = findViewById(R.id.textInputPasswordRegister);
        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mTextInputPhoneNumber = findViewById(R.id.textInputNumberPhone);
        mToolbar = findViewById(R.id.toolbar);
    }


    private void clickRegister() {

        String name = mTextInputName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        String codeNumber = mCountryCodePicker.getSelectedCountryCodeWithPlus();
        String phoneNumber = mTextInputPhoneNumber.getText().toString();


        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneNumber.isEmpty()){
            if(password.length() >= 6 ){
                //DATOS INGRESADOS CORRECTAMENTE

                TokenController.crearToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if(task.isSuccessful()){

                            registerClient(new com.returnhome.models.Cliente(name,email,password,codeNumber+" "+phoneNumber, task.getResult()));
                        }
                        else{
                            Toast.makeText(RegistroActivity.this, "No se pudo obtener el token del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
            else{
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerClient(com.returnhome.models.Cliente cliente){

        ClienteController.registrar(cliente).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                //RECIBE LA RESPUESTA DEL SERVIDOR

                if(response.isSuccessful()){
                    NotificacionController.desuscribirMascotaDesaparecida();

                    mAppConfig.actualizarEstadoAuth(true);
                    mAppConfig.guardarNombreCliente(cliente.getNombre());
                    mAppConfig.guardarIdCliente(response.body().getCliente().getId());
                    mAppConfig.guardarNumeroCelular(cliente.getNumeroCelular());

                    Intent intent = new Intent(RegistroActivity.this, HomeActivity.class);
                    //SI EL USUARIO INGRESA AL NAVIGATION ACTIVITY NO PODRA REGRESAR AL REGISTER ACTIVITY
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
}
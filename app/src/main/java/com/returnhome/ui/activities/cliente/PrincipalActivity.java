package com.returnhome.ui.activities.cliente;

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
import com.returnhome.controllers.TokenController;
import com.returnhome.models.RHRespuesta;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.controllers.NotificacionController;
import com.returnhome.utils.AppSharedPreferences;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonIniciarSesion;
    private Button mButtonRegistrar;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputPassword;

    private AppSharedPreferences mAppSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        inicializarComponentes();

        mAppSharedPreferences = new AppSharedPreferences(this);

        mButtonIniciarSesion.setOnClickListener(this);

        mButtonRegistrar.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAppSharedPreferences.comprobarClienteAuth()){
            Intent intent = new Intent(PrincipalActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
    }

    private void inicializarComponentes() {
        mTextInputEmail = findViewById(R.id.textInputEmailLogin);
        mTextInputPassword = findViewById(R.id.textInputPasswordLogin);
        mButtonIniciarSesion = findViewById(R.id.btnIniciarSesion);
        mButtonRegistrar = findViewById(R.id.btnIrARegistrar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnIniciarSesion:
                iniciarSesion();
                break;

            case R.id.btnIrARegistrar:
                mostrarRegistroActivity();
                break;
        }
    }

    private void mostrarRegistroActivity(){
        Intent intent = new Intent(PrincipalActivity.this, RegistroActivity.class);
        startActivity(intent);
    }

    private void iniciarSesion(){
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        if(!email.isEmpty() && !password.isEmpty()) {
            Map<String, String> credenciales = new HashMap<>();
            credenciales.put("email", email);
            credenciales.put("password", password);
            TokenController.crearToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if(task.isSuccessful()){
                        autenticarCliente(credenciales, task.getResult());
                    }
                    else{
                        Toast.makeText(PrincipalActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "La contraseña y el email son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }
///////////////////
    private void autenticarCliente(Map<String,String> credenciales, String token){
        ClienteController.autenticar(credenciales).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {

                if(response.isSuccessful()){
                    Map<String, String> tokenInfo = new HashMap<>();
                    tokenInfo.put("idCliente", String.valueOf(response.body().getCliente().getId()));
                    tokenInfo.put("token", token);
                    String nombreCliente = response.body().getCliente().getNombre();
                    int idCliente = response.body().getCliente().getId();
                    String numeroCelular = response.body().getCliente().getNumeroCelular();

                    mAppSharedPreferences.actualizarEstadoAuth(true);
                    mAppSharedPreferences.guardarNombreCliente(nombreCliente);
                    mAppSharedPreferences.guardarIdCliente(idCliente);
                    mAppSharedPreferences.guardarNumeroCelular(numeroCelular);
                    mAppSharedPreferences.guardarToken(token);
                    registrarToken(tokenInfo);
                }
                else{
                    Toast.makeText(PrincipalActivity.this, "El email o la contraseña son incorrectos", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(PrincipalActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
            }
        });
    }
//////////////////////
    private void registrarToken(Map<String, String> tokenInfo){

        TokenController.registrarTokenDB(tokenInfo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){

                    NotificacionController.suscribirMascotaDesaparecida();

                    Intent intent = new Intent(PrincipalActivity.this, HomeActivity.class);
                    //SI EL USUARIO INGRESA AL NAVIGATION ACTIVITY NO PODRA REGRESAR AL REGISTER ACTIVITY
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(PrincipalActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PrincipalActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
            }
        });

    }
//////////////////////

}
package com.returnhome.ui.activities.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.returnhome.utils.retrofit.RHRespuesta;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.controllers.NotificacionController;
import com.returnhome.utils.AppSharedPreferences;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonIniciarSesion;
    private Button mButtonRegistrar;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputPassword;

    private AppSharedPreferences mAppSharedPreferences;
    private AlertDialog mDialog;

    private String nombreCliente;
    private int idCliente;
    private String numeroCelular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        inicializarComponentes();

        //Crea un mensaje de espera para el proceso de registro
        mDialog = new SpotsDialog.Builder().setContext(PrincipalActivity.this).setMessage("Espere un momento").build();

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
            TokenController.obtenerToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if(task.isSuccessful()){
                        autenticarCliente(credenciales, task.getResult());
                    }
                    else{
                        Toast.makeText(PrincipalActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "La contraseña y el email son obligatorios", Toast.LENGTH_LONG).show();
        }
    }

    private void autenticarCliente(Map<String,String> credenciales, String token){
        mDialog.show();
        ClienteController.autenticar(credenciales).enqueue(new Callback<RHRespuesta>() {
            //AÑADO EL OBJETO CALLBACK PARA CONTROLAR LOS EVENTOS DE LA PETICIÓN
            @Override
            //METODO QUE SE EJECUTA CUANDO LA PETICION TRAE DATOS
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {

                if(response.code() == 200){
                    Map<String, String> tokenInfo = new HashMap<>();
                    tokenInfo.put("idCliente", String.valueOf(response.body().getCliente().getId()));
                    tokenInfo.put("token", token);
                    nombreCliente = response.body().getCliente().getNombre();
                    idCliente = response.body().getCliente().getId();
                    numeroCelular = response.body().getCliente().getNumeroCelular();
                    registrarToken(tokenInfo);
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(PrincipalActivity.this, "El email o la contraseña son incorrectos", Toast.LENGTH_LONG)
                            .show();
                }
            }
            @Override
            //METODO QUE SE EJECUTA CUANDO LA PETICIÓN FALLA
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                mDialog.dismiss();
                //SI ALGO FALLA EN LA PETICION SE MUESTRA UN MENSAJE AMIGABLE AL USUARIO
                Toast.makeText(PrincipalActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_LONG).show();
            }
            });
    }

    private void registrarToken(Map<String, String> tokenInfo){

        TokenController.registrarTokenDB(tokenInfo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                mDialog.dismiss();
                if(response.code() == 201){

                    /*
                    EL USUARIO AUTENTICADO SE SUSBRIBE AL TEMA MASCOTAS DESAPARECIDAS PARA RECIBIR
                    NOTIFICACIONES DE LAS MASCOTAS REPORTADAS COMO DESAPARECIDAS
                     */

                    NotificacionController.suscribirMascotaDesaparecida();

                    /*
                    SE ALMACENAN DE MANERA LOCAL LOS DATOS DEL USUARIO AUTENTICADO ATRAVES DE LA
                    CLASE SHAREDPREFERENCES
                     */

                    mAppSharedPreferences.actualizarEstadoAuth(true);
                    mAppSharedPreferences.guardarNombreCliente(nombreCliente);
                    mAppSharedPreferences.guardarIdCliente(idCliente);
                    mAppSharedPreferences.guardarNumeroCelular(numeroCelular);

                    Intent intent = new Intent(PrincipalActivity.this,
                                                            HomeActivity.class);
                    //SI EL USUARIO INGRESA AL HOME ACTIVITY NO PODRA REGRESAR AL PRINCIPAL ACTIVITY
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{

                    Toast.makeText(PrincipalActivity.this,
                            "No se pudo iniciar sesion", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mDialog.dismiss();
                Toast.makeText(PrincipalActivity.this,
                        "No se pudo iniciar sesion", Toast.LENGTH_LONG).show();
            }
        });
    }
}
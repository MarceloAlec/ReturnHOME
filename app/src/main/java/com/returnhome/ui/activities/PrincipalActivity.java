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
import com.returnhome.controllers.TokenController;
import com.returnhome.models.Token;
import com.returnhome.models.RHRespuesta;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.controllers.NotificacionController;
import com.returnhome.ui.activities.cliente.HomeActivity;
import com.returnhome.utils.AppConfig;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonSignIn;
    private Button mButtonRegister;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputPassword;

    private AppConfig mAppConfig;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        mAppConfig = new AppConfig(this);

        mButtonSignIn.setOnClickListener(this);

    }

    private void initializeComponents() {
        mTextInputEmail = findViewById(R.id.textInputEmailLogin);
        mTextInputPassword = findViewById(R.id.textInputPasswordLogin);
        mButtonSignIn = findViewById(R.id.btnSignIn);
        mButtonRegister = findViewById(R.id.btnRegister);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignIn:
                login();
                break;

            case R.id.btnRegister:
                register();
                break;


        }
    }

    private void register(){
        Intent intent = new Intent(PrincipalActivity.this, RegistroActivity.class);
        startActivity(intent);
    }


    private void login(){
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                Map<String, String> auth = new HashMap<>();
                auth.put("email", email);
                auth.put("password", password);
                TokenController.crearToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            authClient(auth, task.getResult());
                        }
                        else{
                            Toast.makeText(PrincipalActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();

            }
        }
        else{
            Toast.makeText(this, "La contraseña y el email son obligatorios", Toast.LENGTH_SHORT).show();

        }

    }

    private void authClient(Map<String,String> auth, String token){
        ClienteController.autenticar(auth).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {

                if(response.isSuccessful()){
                    Map<String, String> tokenInfo = new HashMap<>();
                    tokenInfo.put("idClient", String.valueOf(response.body().getClient().getId()));
                    tokenInfo.put("token", token);
                    String name = response.body().getClient().getNombre();
                    int id = response.body().getClient().getId();
                    String phoneNumber = response.body().getClient().getNumeroCelular();

                    updateToken(tokenInfo, name, id, phoneNumber);

                }
                else{
                    Toast.makeText(PrincipalActivity.this, "El email o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {

                Toast.makeText(PrincipalActivity.this, "Ingreso fallido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToken(Map<String, String> tokenInfo, String name, int id, String phoneNumber){

        TokenController.actualizar(tokenInfo).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){

                    NotificacionController.suscribirMascotaDesaparecida();

                    mAppConfig.actualizarEstadoAuth(true);
                    mAppConfig.guardarNombreCliente(name);
                    mAppConfig.guardarIdCliente(id);
                    mAppConfig.guardarNumeroCelular(phoneNumber);

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
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(PrincipalActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
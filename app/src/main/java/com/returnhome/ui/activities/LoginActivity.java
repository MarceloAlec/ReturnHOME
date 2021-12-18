package com.returnhome.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.returnhome.R;
import com.returnhome.models.RHResponse;
import com.returnhome.providers.ClientProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.providers.NotificationProvider;
import com.returnhome.providers.TokenProvider;
import com.returnhome.ui.activities.client.HomeActivity;
import com.returnhome.utils.AppConfig;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button mButtonSignIn;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputPassword;

    private AppConfig mAppConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeComponents();

        mAppConfig = new AppConfig(this);


        mButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void initializeComponents() {
        mTextInputEmail = findViewById(R.id.textInputEmailLogin);
        mTextInputPassword = findViewById(R.id.textInputPasswordLogin);
        mButtonSignIn = findViewById(R.id.btnSignIn);
    }

    private void login(){
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                Map<String, String> auth = new HashMap<>();
                auth.put("email", email);
                auth.put("password", password);
                TokenProvider.create().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            authClient(auth, task.getResult());
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
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
        ClientProvider.authClient(auth).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {

                if(response.isSuccessful()){
                    Map<String, String> tokenInfo = new HashMap<>();
                    tokenInfo.put("idClient", String.valueOf(response.body().getClient().getId()));
                    tokenInfo.put("token", token);
                    String name = response.body().getClient().getName();
                    int id = response.body().getClient().getId();
                    String phoneNumber = response.body().getClient().getPhoneNumber();

                    updateToken(tokenInfo, name, id, phoneNumber);

                }
                else{
                    Toast.makeText(LoginActivity.this, "El email o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {

                Toast.makeText(LoginActivity.this, "Ingreso fallido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToken(Map<String, String> tokenInfo, String name, int id, String phoneNumber){

        ClientProvider.updateToken(tokenInfo).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){

                    NotificationProvider.suscribeMissingPet();

                    mAppConfig.updateLoginStatus(true);
                    mAppConfig.saveUserName(name);
                    mAppConfig.saveUserId(id);
                    mAppConfig.saveUserPhoneNumber(phoneNumber);

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    //SI EL USUARIO INGRESA AL NAVIGATION ACTIVITY NO PODRA REGRESAR AL REGISTER ACTIVITY
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
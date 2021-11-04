package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.returnhome.R;
import com.returnhome.utils.retrofit.ResponseApi;
import com.returnhome.providers.ClientProvider;
import com.google.android.material.textfield.TextInputEditText;
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

    private ClientProvider mClientProvider;
    private AppConfig mAppConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeComponents();

        mClientProvider = new ClientProvider(LoginActivity.this);
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

                mClientProvider.authClient(auth).enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {

                        if(response.isSuccessful()){

                            mAppConfig.updateLoginStatus(true);
                            mAppConfig.saveUserName(response.body().getClient().getName());
                            mAppConfig.saveUserId(response.body().getClient().getId());
                            mAppConfig.saveUserPhoneNumber(response.body().getClient().getPhoneNumber());

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            //SI EL USUARIO INGRESA AL NAVIGATION ACTIVITY NO PODRA REGRESAR AL REGISTER ACTIVITY
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "El email o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseApi> call, Throwable t) {

                        Toast.makeText(LoginActivity.this, "Ingreso fallido", Toast.LENGTH_SHORT).show();
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

}
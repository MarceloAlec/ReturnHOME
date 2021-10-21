package com.alec.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alec.returnhome.R;
import com.alec.returnhome.models.ApiResponse;
import com.alec.returnhome.providers.ClientProvider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button mButtonSignIn;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputPassword;

    private ClientProvider mClientProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInputEmail = findViewById(R.id.textInputEmailLogin);
        mTextInputPassword = findViewById(R.id.textInputPasswordLogin);
        mButtonSignIn = findViewById(R.id.btnSignIn);

        mClientProvider = new ClientProvider(LoginActivity.this);


        mButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {


                HashMap<String, String> auth = new HashMap<>();
                auth.put("email", email);
                auth.put("password", password);

                mClientProvider.authClient(auth).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                        if(response.isSuccessful()){

                            Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                            //SI EL USUARIO INGRESA AL NAVIGATION ACTIVITY NO PODRA REGRESAR AL REGISTER ACTIVITY
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "El email o la contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

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

    private void saveLoginSharedPreferences(int id, String name){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", id);
        editor.apply();
    }
}
package com.returnhome.ui.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.controllers.ClienteController;
import com.returnhome.utils.AppSharedPreferences;
import com.returnhome.models.RHRespuesta;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarPasswordActivity extends AppCompatActivity {

    private TextInputEditText mTextPasswordActual;
    private TextInputEditText mTextPasswordNueva;
    private TextInputEditText mTextConfirmarPasswordNueva;
    private Button mButtonActualizarPassword;
    private androidx.appcompat.widget.Toolbar mToolbar;

    private AppSharedPreferences mAppSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_password);

        initializeComponents();

        mAppSharedPreferences = new AppSharedPreferences(this);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Registro de usuario");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mButtonActualizarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar();
            }
        });
    }

    private void initializeComponents(){
        mTextPasswordActual = findViewById(R.id.textInputPasswordActual);
        mTextPasswordNueva = findViewById(R.id.textInputPasswordNueva);
        mTextConfirmarPasswordNueva = findViewById(R.id.textInputConfirmarPasswordNueva);
        mButtonActualizarPassword = findViewById(R.id.btnActualizarPassWord);
        mToolbar = findViewById(R.id.toolbar);
    }

    private void actualizar() {
        String passwordActual = mTextPasswordActual.getText().toString();
        String nuevaPassword = mTextPasswordNueva.getText().toString();
        String confirmacionNuevaPassword = mTextConfirmarPasswordNueva.getText().toString();

        if(!passwordActual.isEmpty() && !nuevaPassword.isEmpty() && !confirmacionNuevaPassword.isEmpty()){

            if(nuevaPassword.equals(confirmacionNuevaPassword)){
                Map<String, String> password = new HashMap<>();
                password.put("actualPassword", passwordActual);
                password.put("nuevoPassword", nuevaPassword);
                password.put("idCliente", String.valueOf(mAppSharedPreferences.obtenerIdCliente()));

                ClienteController.actualizarPassword(password).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if(response.isSuccessful()){
                            Toast.makeText(ActualizarPasswordActivity.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ActualizarPasswordActivity.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ActualizarPasswordActivity.this, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}
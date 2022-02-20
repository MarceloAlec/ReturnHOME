package com.returnhome.ui.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.controllers.ClienteController;
import com.returnhome.utils.AppConfig;
import com.returnhome.models.RHRespuesta;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarPasswordActivity extends AppCompatActivity {

    private TextInputEditText mTextCurrentInputPassword;
    private TextInputEditText mTextNewInputPassword;
    private TextInputEditText mTextConfirmNewInputPassword;
    private Button mButtonUpdatePasswordProfile;
    private androidx.appcompat.widget.Toolbar mToolbar;

    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password_profile);

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

        mButtonUpdatePasswordProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickUpdate();
            }
        });
    }

    private void initializeComponents(){
        mTextCurrentInputPassword = findViewById(R.id.textInputCurrentPasswordUpdateProfile);
        mTextNewInputPassword = findViewById(R.id.textInputNewPasswordUpdateProfile);
        mTextConfirmNewInputPassword = findViewById(R.id.textInputConfirmNewPasswordUpdateProfile);
        mButtonUpdatePasswordProfile = findViewById(R.id.btnUpdatePasswordProfile);
        mToolbar = findViewById(R.id.toolbar);


    }

    private void clickUpdate() {
        String currentPassword = mTextCurrentInputPassword.getText().toString();
        String newPassword = mTextNewInputPassword.getText().toString();
        String confirmNewPassword = mTextConfirmNewInputPassword.getText().toString();

        if(!currentPassword.isEmpty() && !newPassword.isEmpty() && !confirmNewPassword.isEmpty()){

            if(newPassword.length() >= 6 ){
                if(newPassword.equals(confirmNewPassword)){
                    Map<String, String> password = new HashMap<>();
                    password.put("currentPassword", currentPassword);
                    password.put("newPassword", newPassword);
                    password.put("id", String.valueOf(mAppConfig.obtenerIdCliente()));
                    updatePassword(password);
                }
                else{
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePassword(Map<String, String> password) {
        ClienteController.actualizarPassword(password).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ActualizarPasswordActivity.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ActualizarPasswordActivity.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(ActualizarPasswordActivity.this, "Se ha producido un error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
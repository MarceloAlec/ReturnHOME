package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.includes.Toolbar;
import com.returnhome.providers.ClientProvider;
import com.returnhome.utils.AppConfig;
import com.returnhome.utils.retrofit.ResponseApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordProfile extends AppCompatActivity {

    private TextInputEditText mTextCurrentInputPassword;
    private TextInputEditText mTextNewInputPassword;
    private TextInputEditText mTextConfirmNewInputPassword;
    private Button mButtonUpdatePasswordProfile;

    private ClientProvider mClientProvider;
    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password_profile);

        mTextCurrentInputPassword = findViewById(R.id.textInputCurrentPasswordUpdateProfile);
        mTextNewInputPassword = findViewById(R.id.textInputNewPasswordUpdateProfile);
        mTextConfirmNewInputPassword = findViewById(R.id.textInputConfirmNewPasswordUpdateProfile);
        mButtonUpdatePasswordProfile = findViewById(R.id.btnUpdatePasswordProfile);

        mClientProvider = new ClientProvider(this);
        mAppConfig = new AppConfig(this);
        Toolbar.show(this, "Cambiar la contraseña", true);

        mButtonUpdatePasswordProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickUpdate();
            }
        });
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
                    password.put("id", String.valueOf(mAppConfig.getUserId()));
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
        mClientProvider.updateClient(password).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                if(response.isSuccessful()){
                    Toast.makeText(UpdatePasswordProfile.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(UpdatePasswordProfile.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Toast.makeText(UpdatePasswordProfile.this, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
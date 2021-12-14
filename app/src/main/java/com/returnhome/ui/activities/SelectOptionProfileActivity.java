package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.returnhome.R;
import com.returnhome.includes.Toolbar;
import com.returnhome.providers.ClientProvider;
import com.returnhome.utils.AppConfig;
import com.returnhome.models.RHResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectOptionProfileActivity extends AppCompatActivity {


    private LinearLayout mButtonDeleteProfile;
    private LinearLayout mButtonChangePassword;
    private LinearLayout mButtonGoToUpdateProfile;

    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_profile);

        mButtonGoToUpdateProfile = findViewById(R.id.btnEditProfile);
        mButtonDeleteProfile = findViewById(R.id.btnDeleteProfile);
        mButtonChangePassword = findViewById(R.id.btnChangePassword);

        mAppConfig = new AppConfig(this);
        Toolbar.show(this, "Seleccionar opción", true);

        mButtonGoToUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectOptionProfileActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        mButtonDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDelete();
            }
        });

        mButtonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectOptionProfileActivity.this, UpdatePasswordProfile.class);
                startActivity(intent);
            }
        });

    }

    private void clickDelete() {
        deleteAccount();
    }

    private void deleteAccount() {
        ClientProvider.deleteAccount(mAppConfig.getUserId()).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(SelectOptionProfileActivity.this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                    mAppConfig.updateLoginStatus(false);
                    mAppConfig.saveUserPhoneNumber(null);
                    mAppConfig.saveUserName(null);
                    mAppConfig.saveUserId(0);
                    Intent intent = new Intent(SelectOptionProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(SelectOptionProfileActivity.this, "No se pudo eliminar su cuenta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
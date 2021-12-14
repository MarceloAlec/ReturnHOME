package com.returnhome.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
    private LinearLayout mButtonLogOut;

    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_profile);

        mButtonGoToUpdateProfile = findViewById(R.id.btnEditProfile);
        mButtonDeleteProfile = findViewById(R.id.btnDeleteProfile);
        mButtonChangePassword = findViewById(R.id.btnChangePassword);
        mButtonLogOut = findViewById(R.id.btnLogOut);

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

        mButtonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogLogOut();
            }
        });

    }

    private void clickDelete() {
        showAlertDialogDeleteAccount();
    }

    private void showAlertDialogDeleteAccount() {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setTitle("ReturnHOME");
        builder.setMessage("Esta seguro que desea eliminar su cuenta?");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });
        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.cancel();
            }
        });

        builder.show();
    }

    private void showAlertDialogLogOut() {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setTitle("ReturnHOME");
        builder.setMessage("Esta seguro que desea cerrar sesion?");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.cancel();
            }
        });

        builder.show();
    }

    private void logout(){
        mAppConfig.updateLoginStatus(false);
        mAppConfig.saveUserPhoneNumber(null);
        mAppConfig.saveUserName(null);
        mAppConfig.saveUserId(0);
        Intent intent = new Intent(SelectOptionProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void deleteAccount(){
        ClientProvider.deleteAccount(mAppConfig.getUserId()).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(SelectOptionProfileActivity.this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                    logout();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(SelectOptionProfileActivity.this, "No se pudo eliminar su cuenta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
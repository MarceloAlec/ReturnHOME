package com.returnhome.ui.activities.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.returnhome.R;
import com.returnhome.controllers.ClienteController;
import com.returnhome.controllers.TokenController;
import com.returnhome.controllers.NotificacionController;
import com.returnhome.utils.AppConfig;
import com.returnhome.models.RHRespuesta;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionOpcionAjustesActivity extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout mDeleteAccount;
    private LinearLayout mUpdatePassword;
    private LinearLayout mGoToUpdateProfile;
    private LinearLayout mLogOut;
    private androidx.appcompat.widget.Toolbar mToolbar;

    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_profile);

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

    }


    private void initializeComponents(){
        mGoToUpdateProfile = findViewById(R.id.btnUpdateProfile);
        mDeleteAccount = findViewById(R.id.btnDeleteAccount);
        mUpdatePassword = findViewById(R.id.btnUpdatePassword);
        mLogOut = findViewById(R.id.btnLogOut);
        mToolbar = findViewById(R.id.toolbar);

        mGoToUpdateProfile.setOnClickListener(this);
        mDeleteAccount.setOnClickListener(this);
        mUpdatePassword.setOnClickListener(this);
        mLogOut.setOnClickListener(this);

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
                TokenController.eliminar().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            deleteAccount();
                        }
                        else{
                            Toast.makeText(SeleccionOpcionAjustesActivity.this,"No se pudo eliminar su cuenta", Toast.LENGTH_SHORT);
                        }
                    }
                });
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

                TokenController.eliminar().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Map<String, String> tokenInfo = new HashMap<>();
                            tokenInfo.put("idClient",String.valueOf(mAppConfig.obtenerIdCliente()));
                            tokenInfo.put("token", "");
                            updateToken(tokenInfo);
                        }
                        else{
                            Toast.makeText(SeleccionOpcionAjustesActivity.this,"No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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


    private void updateToken(Map<String, String> tokenInfo){

        TokenController.registrar(tokenInfo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                   logout();
                }
                else{
                    Toast.makeText(SeleccionOpcionAjustesActivity.this, "No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SeleccionOpcionAjustesActivity.this, "No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout(){
        NotificacionController.desuscribirMascotaDesaparecida();
        mAppConfig.actualizarEstadoAuth(false);
        mAppConfig.guardarNumeroCelular(null);
        mAppConfig.guardarNombreCliente(null);
        mAppConfig.guardarIdCliente(0);

        Intent intent = new Intent(SeleccionOpcionAjustesActivity.this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void deleteAccount(){

        ClienteController.eliminarCuenta(mAppConfig.obtenerIdCliente()).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    Toast.makeText(SeleccionOpcionAjustesActivity.this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                    logout();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(SeleccionOpcionAjustesActivity.this, "No se pudo eliminar su cuenta", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.btnUpdateProfile:
                intent = new Intent(SeleccionOpcionAjustesActivity.this, ActualizarInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.btnDeleteAccount:
                clickDelete();
                break;

            case R.id.btnUpdatePassword:
                intent = new Intent(SeleccionOpcionAjustesActivity.this, ActualizarPasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.btnLogOut:
                showAlertDialogLogOut();
                break;


        }
    }
}
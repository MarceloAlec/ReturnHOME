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
import com.returnhome.utils.AppSharedPreferences;
import com.returnhome.models.RHRespuesta;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionOpcionAjustesActivity extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout mEliminarCuenta;
    private LinearLayout mActualizarPassword;
    private LinearLayout mIrAActualizarInfo;
    private LinearLayout mCerrarSesion;
    private androidx.appcompat.widget.Toolbar mToolbar;

    private AppSharedPreferences mAppSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_opcion_ajustes);

        inicializarComponentes();

        mAppSharedPreferences = new AppSharedPreferences(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Seleccionar opción");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void inicializarComponentes(){
        mIrAActualizarInfo = findViewById(R.id.btnActualizarInfoCliente);
        mEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        mActualizarPassword = findViewById(R.id.btnActualizarPassword);
        mCerrarSesion = findViewById(R.id.btnCerrarSesion);
        mToolbar = findViewById(R.id.toolbar);

        mIrAActualizarInfo.setOnClickListener(this);
        mEliminarCuenta.setOnClickListener(this);
        mActualizarPassword.setOnClickListener(this);
        mCerrarSesion.setOnClickListener(this);

    }

    private void mostrarDialogoEliminarCuenta() {
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
                            eliminarCuenta();
                        }
                        else{
                            Toast.makeText(SeleccionOpcionAjustesActivity.this,"No se pudo eliminar su cuenta", Toast.LENGTH_SHORT).show();
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

    private void mostrarDialogoCerrarSesion() {
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
                            tokenInfo.put("idCliente",String.valueOf(mAppSharedPreferences.obtenerIdCliente()));
                            tokenInfo.put("token", String.valueOf(mAppSharedPreferences.obtenerToken()));
                            eliminarTokenDB(tokenInfo);
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


    private void eliminarTokenDB(Map<String, String> tokenInfo){

        TokenController.eliminarTokenDB(tokenInfo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    NotificacionController.desuscribirMascotaDesaparecida();
                    mAppSharedPreferences.guardarToken(null);
                    mAppSharedPreferences.actualizarEstadoAuth(false);
                    mAppSharedPreferences.guardarNumeroCelular(null);
                    mAppSharedPreferences.guardarNombreCliente(null);
                    mAppSharedPreferences.guardarIdCliente(0);

                    Intent intent = new Intent(SeleccionOpcionAjustesActivity.this, PrincipalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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



    private void eliminarCuenta(){

        ClienteController.eliminarCuenta(mAppSharedPreferences.obtenerIdCliente()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){
                    Toast.makeText(SeleccionOpcionAjustesActivity.this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                    NotificacionController.desuscribirMascotaDesaparecida();
                    mAppSharedPreferences.guardarToken(null);
                    mAppSharedPreferences.actualizarEstadoAuth(false);
                    mAppSharedPreferences.guardarNumeroCelular(null);
                    mAppSharedPreferences.guardarNombreCliente(null);
                    mAppSharedPreferences.guardarIdCliente(0);

                    Intent intent = new Intent(SeleccionOpcionAjustesActivity.this, PrincipalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(SeleccionOpcionAjustesActivity.this, "No se pudo eliminar su cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SeleccionOpcionAjustesActivity.this, "No se pudo eliminar su cuenta", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.btnActualizarInfoCliente:
                intent = new Intent(SeleccionOpcionAjustesActivity.this, ActualizarInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.btnEliminarCuenta:
                mostrarDialogoEliminarCuenta();
                break;

            case R.id.btnActualizarPassword:
                intent = new Intent(SeleccionOpcionAjustesActivity.this, ActualizarPasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.btnCerrarSesion:
                mostrarDialogoCerrarSesion();
                break;
        }
    }
}
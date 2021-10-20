package com.alec.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alec.returnhome.R;

public class MainActivity extends AppCompatActivity {

    //PARA REFERENCIAR LOS BOTONES
    Button mButtonGoToLogin;
    Button mButttonGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SE OBTIENE LA REFERENCIA DE LOS BOTONES
        mButtonGoToLogin = findViewById(R.id.btnGoToLogin);
        mButttonGoToRegister = findViewById(R.id.btnGoToRegister);

        //AÑADE EL EVENTO CLICK A LOS BOTONES
        mButtonGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SE CREA UNA INTENCION PARA PASAR DE UNA ACTIVIDAD A OTRA
                //TOMA COMO ARGUMENTOS EL CONTEXTO ACTUAL Y LA ACTIVIDAD A DIRIGIRSE
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mButttonGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}
package com.alec.returnhome.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alec.returnhome.R;

public class MainActivity extends AppCompatActivity {

    //PARA REFERENCIAR LOS BOTONES
    Button mButtonLogin;
    Button mButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SE OBTIENE LA REFERENCIA DE LOS BOTONES
        mButtonLogin = findViewById(R.id.btnLogin);
        mButtonRegister = findViewById(R.id.btnRegister);

        //AÑADE EL EVENTO CLICK A LOS BOTONES
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SE CREA UNA INTENCION PARA PASAR DE UNA ACTIVIDAD A OTRA
                //TOMA COMO ARGUMENTOS EL CONTEXTO ACTUAL Y LA ACTIVIDAD A DIRIGIRSE
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}
package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.returnhome.R;
import com.returnhome.ui.activities.cliente.HomeActivity;
import com.returnhome.utils.AppConfig;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //PARA REFERENCIAR LOS BOTONES
    private Button mButtonGoToLogin;
    private Button mButttonGoToRegister;

    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_ReturnHOME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();

        mAppConfig = new AppConfig(this);

        //AÑADE EL EVENTO CLICK A LOS BOTONES
        mButtonGoToLogin.setOnClickListener(this);

        mButttonGoToRegister.setOnClickListener(this);
    }

    private void initializeComponents() {
        mButtonGoToLogin = findViewById(R.id.btnGoToLogin);
        mButttonGoToRegister = findViewById(R.id.btnGoToRegister);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){

            case R.id.btnGoToLogin:
                 intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.btnGoToRegister:
                intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(mAppConfig.isUserLogin()){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
    }
}
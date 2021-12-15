package com.returnhome.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.returnhome.R;
import com.returnhome.includes.Toolbar;
import com.returnhome.providers.TokenProvider;
import com.returnhome.ui.activities.client.HomeActivity;
import com.returnhome.utils.AppConfig;
import com.returnhome.models.RHResponse;
import com.returnhome.models.Client;
import com.returnhome.providers.ClientProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button mButtonRegister;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInputPassword;
    private RadioButton mRadioButtonMale;
    private CountryCodePicker mCountryCodePicker;
    private TextInputEditText mTextInputPhoneNumber;


    private AlertDialog mDialog;
    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();

        //MENSAJE DE ESPERA PARA EL PROCESO DE REGISTRO
        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage(R.string.dialogRegister).build();


        mAppConfig = new AppConfig(this);
        Toolbar.show(this, "Registro de usuario", true);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });


    }

    private void initializeComponents() {
        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputNameRegister);
        mTextInputEmail = findViewById(R.id.textInputEmailRegister);
        mTextInputPassword = findViewById(R.id.textInputPasswordRegister);
        mRadioButtonMale = findViewById(R.id.radioButtonMale);
        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mTextInputPhoneNumber = findViewById(R.id.textInputNumberPhone);
    }


    private void clickRegister() {

        String name = mTextInputName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        char gender = ((mRadioButtonMale.isChecked() ? 'M' : 'F'));
        String codeNumber = mCountryCodePicker.getSelectedCountryCodeWithPlus();
        String phoneNumber = mTextInputPhoneNumber.getText().toString();


        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneNumber.isEmpty()){
            if(password.length() >= 6 ){
                //DATOS INGRESADOS CORRECTAMENTE
                mDialog.show();

                TokenProvider.create().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if(task.isSuccessful()){

                            registerClient(new Client(name,email,password,gender,codeNumber+" "+phoneNumber, task.getResult()));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "No se pudo obtener el token del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
            else{
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerClient(Client client){


        ClientProvider.registerClient(client).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                //RECIBE LA RESPUESTA DEL SERVIDOR
                mDialog.dismiss();

                if(response.isSuccessful()){

                    mAppConfig.updateLoginStatus(true);
                    mAppConfig.saveUserName(client.getName());
                    mAppConfig.saveUserId(response.body().getClient().getId());
                    mAppConfig.saveUserPhoneNumber(client.getPhoneNumber());

                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    //SI EL USUARIO INGRESA AL NAVIGATION ACTIVITY NO PODRA REGRESAR AL REGISTER ACTIVITY
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(RegisterActivity.this, "El correo ya se encuentra registrado", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {

                Toast.makeText(RegisterActivity.this, "Registro fallido", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
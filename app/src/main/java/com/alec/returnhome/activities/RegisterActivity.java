package com.alec.returnhome.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alec.returnhome.R;
import com.alec.returnhome.models.Client;
import com.alec.returnhome.providers.ClientProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    Button mButtonRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputPassword;
    RadioButton mRadioButtonMale;
    CountryCodePicker mCountryCodePicker;
    TextInputEditText mTextInputPhoneNumber;
    ClientProvider mClientProvider;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mRadioButtonMale = findViewById(R.id.radioButtonMale);
        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mTextInputPhoneNumber = findViewById(R.id.textInputNumberPhone);

        //MENSAJE DE ESPERA PARA EL PROCESO DE REGISTRO
        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage(R.string.dialogRegister).build();

        mClientProvider = new ClientProvider(RegisterActivity.this);

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });

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
                registerClient(new Client(name,email,password,gender,codeNumber+phoneNumber));
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

        mClientProvider.registerClient(client).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //RECIBE LA RESPUESTA DEL SERVIDOR
                mDialog.hide();

                Intent intent = new Intent(RegisterActivity.this, NavigationActivity.class);
                //SI EL USUARIO INGRESA AL NAVIGATION ACTIVITY NO PODRA REGRESAR AL REGISTER ACTIVITY
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Registro fallido", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
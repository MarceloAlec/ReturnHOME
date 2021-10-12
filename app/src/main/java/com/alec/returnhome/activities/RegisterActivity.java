package com.alec.returnhome.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alec.returnhome.R;
import com.alec.returnhome.models.Client;
import com.alec.returnhome.providers.ClientProvider;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    Button mButtonRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputPassword;

    ClientProvider mClientProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);

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

        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if(password.length() >= 6 ){
                //DATOS INGRESADOS CORRECTAMENTE
                registerClient(new Client(name,email,password));
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
                try{
                    Log.i("Response", response.body().toString());
                }catch (Exception ex){
                    Log.d("Error", "Error encontrado" + ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error", "Error encontrado" + t.getMessage());

            }
        });

    }
}
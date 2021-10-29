package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.returnhome.R;
import com.returnhome.models.Client;
import com.returnhome.providers.ClientProvider;
import com.returnhome.utils.AppConfig;

public class UpdateProfileActivity extends AppCompatActivity {

    private Button mButtonUpdateProfile;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInputPassword;
    private RadioButton mRadioButtonMale;
    private CountryCodePicker mCountryCodePicker;
    private TextInputEditText mTextInputPhoneNumber;
    private ClientProvider mClientProvider;
    private boolean isButtonEdit = true;

    private AlertDialog mDialog;
    private AppConfig mAppConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initializeComponents();

        mButtonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEdit();
            }
        });
    }

    private void clickEdit() {

        if(isButtonEdit){
            mButtonUpdateProfile.setText(R.string.btn_updateProfile);
            mTextInputName.setEnabled(true);
            mTextInputName.setTextInputLayoutFocusedRectEnabled(true);
            mTextInputEmail.setEnabled(true);
            mTextInputPassword.setEnabled(true);
            mRadioButtonMale.setEnabled(true);
            mCountryCodePicker.setEnabled(true);
            mTextInputPhoneNumber.setEnabled(true);
        }
        else{
            String name = mTextInputName.getText().toString();
            String email = mTextInputEmail.getText().toString();
            String password = mTextInputPassword.getText().toString();
            char gender = ((mRadioButtonMale.isChecked() ? 'M' : 'F'));
            String codeNumber = mCountryCodePicker.getSelectedCountryCodeWithPlus();
            String phoneNumber = mTextInputPhoneNumber.getText().toString();


            if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneNumber.isEmpty()){
                if(password.length() >= 6 ){
                    //DATOS INGRESADOS CORRECTAMENTE
                    updateClient(new Client(name,email,password,gender,codeNumber+phoneNumber));
                }
                else{
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void updateClient(Client client) {

    }

    private void initializeComponents() {
        mButtonUpdateProfile = findViewById(R.id.btnUpdateProfile);
        mTextInputName = findViewById(R.id.textInputNameUpdateProfile);
        mTextInputEmail = findViewById(R.id.textInputEmailUpdateProfile);
        mTextInputPassword = findViewById(R.id.textInputPasswordUpdateProfile);
        mRadioButtonMale = findViewById(R.id.radioButtonMaleUpdateProfile);
        mCountryCodePicker = findViewById(R.id.countryCodePickerUpdateProfile);
        mTextInputPhoneNumber = findViewById(R.id.textInputNumberUpdateProfile);

        mButtonUpdateProfile.setText(R.string.btn_editProfile);

        mTextInputName.setEnabled(false);
        mTextInputEmail.setEnabled(false);
        mTextInputPassword.setEnabled(false);
        mRadioButtonMale.setEnabled(false);
        mCountryCodePicker.setEnabled(false);
        mTextInputPhoneNumber.setEnabled(false);
    }
}
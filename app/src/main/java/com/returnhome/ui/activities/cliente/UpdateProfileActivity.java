package com.returnhome.ui.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.returnhome.R;
import com.returnhome.modelos.Cliente;
import com.returnhome.providers.ClientProvider;
import com.returnhome.utils.AppConfig;
import com.returnhome.utils.retrofit.RHResponse;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    private Button mButtonUpdateProfile;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputName;

    private RadioButton mRadioButtonMale;
    private RadioButton mRadioButtonFemale;
    private CountryCodePicker mCountryCodePicker;
    private TextInputEditText mTextInputPhoneNumber;

    private String name;
    private String email;
    private char gender;
    private String code_phoneNumber;

    private AppConfig mAppConfig;
    private Cliente mCliente;

    private CircleImageView mCircleImageGoToSelectionOptionProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initializeComponents();

        mAppConfig = new AppConfig(UpdateProfileActivity.this);

        getClient();

        mButtonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickUpdate();
            }
        });

        mCircleImageGoToSelectionOptionProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getClient() {
        ClientProvider.getClient(mAppConfig.getUserId()).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                    mCliente = response.body().getClient();
                    showClientInformation();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {

            }
        });
    }

    private void showClientInformation() {
        if(mCliente != null){
            mTextInputName.setText(mCliente.getNombre());
            mTextInputEmail.setText(mCliente.getEmail());

            String[] code_phoneNumber = mCliente.getNumeroCelular().split(" ");

            mTextInputPhoneNumber.setText(code_phoneNumber[1]);
            //mCountryCodePicker.setCountryForPhoneCode(code_phoneNumber[0]);


        }
        else{
            mButtonUpdateProfile.setEnabled(false);
            Toast.makeText(this, "No se pudo cargar su información", Toast.LENGTH_SHORT).show();
        }
    }

    private void clickUpdate() {
        name = mTextInputName.getText().toString();
        email = mTextInputEmail.getText().toString();
        code_phoneNumber = mCountryCodePicker.getSelectedCountryCodeWithPlus()+" "+mTextInputPhoneNumber.getText().toString();

        if(!name.isEmpty() && !email.isEmpty() && !code_phoneNumber.isEmpty()){
            updateClient(new Cliente(mAppConfig.getUserId(),name, email, code_phoneNumber));
        }
        else{
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateClient(Cliente cliente) {
        ClientProvider.updateClient(cliente).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {

                Toast.makeText(UpdateProfileActivity.this, "Actualizado", Toast.LENGTH_SHORT).show();
                mAppConfig.saveUserName(name);
                mAppConfig.saveUserPhoneNumber(code_phoneNumber);
                finish();
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(UpdateProfileActivity.this, "No se pudo actualizar sus datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeComponents() {
        mButtonUpdateProfile = findViewById(R.id.btnUpdateProfile);
        mTextInputName = findViewById(R.id.textInputNameUpdateProfile);
        mTextInputEmail = findViewById(R.id.textInputEmailUpdateProfile);
        mRadioButtonMale = findViewById(R.id.radioButtonMaleUpdateProfile);
        mRadioButtonFemale = findViewById(R.id.radioButtonFemaleUpdateProfile);
        mCountryCodePicker = findViewById(R.id.countryCodePickerUpdateProfile);
        mTextInputPhoneNumber = findViewById(R.id.textInputNumberUpdateProfile);
        mCircleImageGoToSelectionOptionProfile = findViewById(R.id.btnGoToSelectOptionProfile);
    }
}
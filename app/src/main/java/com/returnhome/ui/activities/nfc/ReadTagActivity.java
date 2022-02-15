package com.returnhome.ui.activities.nfc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.returnhome.R;
import com.returnhome.providers.NfcProvider;
import com.returnhome.ui.activities.mascota.MapPetReportedFoundActivity;
import com.returnhome.utils.AppConfig;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReadTagActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationNfc;
    private CircleImageView mGoToHome;
    private TextView mTextViewEnableDeviceReader;

    private NfcAdapter mNfcAdapter;
    private String[][] mTechLists;
    IntentFilter[] mFilters;
    PendingIntent mPendingIntent;
    AppConfig mAppConfig;

    private String phoneNumber;
    private LatLng petHomeLatLng;
    int idPet;

    private final static int NFC_REQUEST_CODE = 1;

    private boolean mExtraFoundPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_tag);

        initializeComponents();

        mAppConfig = new AppConfig(this);

        mExtraFoundPet = getIntent().getBooleanExtra("foundPet", false);

        //OBTIENE EL ADAPTADOR NFC DEL DISPOSITIVO MOVIL
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null){
            mTextViewEnableDeviceReader.setText("MANTENGA LA ETIQUETA NFC CONTRA LA PARTE POSTERIOR DE SU DISPOSITIVO MOVIL PARA LEERLA");
            if(!mNfcAdapter.isEnabled()){
                showAlertDialogNONFC();
            }
            else{
                mAnimationNfc.playAnimation();
            }
        }
        else{
            mTextViewEnableDeviceReader.setText("SU DISPOSITIVO MOVIL NO ES COMPATIBLE CON LA TECNOLOGIA NFC");
        }

        //IMPLEMENTACION DEL SISTEMA DE ENVIO EN PRIMER PLANO
        //PARA OBTENER LOS DETALLES DE LA ETIQUETA SE USA PENDING INTENT
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        //SE AÑADE LOS FILTROS DE INTENTS QUE MANEJARAN LA ETIQUETA, SI ESTE FILTRO COINCIDE CON LA ETIQUETA ENTONCES LA APLICACION MANEJARÁ LA INTENCION
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        mFilters = new IntentFilter[]{ndef,};
        //SE AÑADE LAS TECNOLOGIAS DE ETIQUETAS QUE LA APLICACION PUEDE MANEJAR
        mTechLists = new String[][] { new String[] { Ndef.class.getName() },
                new String[] { NdefFormatable.class.getName() }};

        mGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeComponents() {
        mAnimationNfc = findViewById(R.id.animationNfc);
        mGoToHome = findViewById(R.id.btnGoToHomeFromReadTag);
        mTextViewEnableDeviceReader = findViewById(R.id.textViewEnableDeviceReader);
    }

    private void showAlertDialogNONFC() {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setCanceledOnTouchOutside(false);
        builder.setMessage("Por favor activa NFC para continuar");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "Configuraciones", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ESPERA Y ESCUCHA HASTA QUE EL USUARIO ACTIVE EL GPS
                startActivityForResult(new Intent(Settings.ACTION_NFC_SETTINGS), NFC_REQUEST_CODE);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mNfcAdapter.isEnabled()) {
            mAnimationNfc.playAnimation();
        }
        else{
            showAlertDialogNONFC();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter != null){
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }

    //METODO QUE SE EJECUTA AUTOMATICAMENTE CUANDO LA ETIQUETA ES DESCUBIERTA
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
            || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
            || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            //OBTIENE LOS DATOS CONTENIDOS EN LA INTENCION
            try{
                Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage message = NfcProvider.getNdefMessage(rawMessages);
                NdefRecord record = message.getRecords()[0];
                String type = new String(record.getType());

                if(type.equals("application/json")){
                    String s = new String(record.getPayload());
                    JsonParser parser = new JsonParser();
                    JsonObject petInfo = (JsonObject) parser.parse(s);

                    idPet = Integer.valueOf(petInfo.get("id").toString().replace('"',' ').trim());
                    phoneNumber = petInfo.get("tel").toString().replace('"',' ').trim();
                    String[] coordinates = petInfo.get("geo").toString().split(",");
                    double latitude = Double.parseDouble(coordinates[0].replace('"',' ').trim());
                    double longitude = Double.parseDouble(coordinates[1].replace('"',' ').trim());
                    petHomeLatLng = new LatLng(latitude,longitude);

                    goToDetailReadingOrMapPetActivity();
                }
                else{
                    Toast.makeText(this, "Los datos en la etiqueta no se encuentra en formato Json", Toast.LENGTH_LONG).show();
                }
            }
            catch(Exception e){
                Toast.makeText(this, "Error"+ e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void goToDetailReadingOrMapPetActivity(){
        if(mExtraFoundPet){
            Intent intent = new Intent(ReadTagActivity.this, MapPetReportedFoundActivity.class);
            intent.putExtra("pet_home_lat",petHomeLatLng.latitude);
            intent.putExtra("pet_home_lng",petHomeLatLng.longitude);
            intent.putExtra("phone_number",phoneNumber);
            intent.putExtra("idPet",idPet);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(ReadTagActivity.this, DetailReadingActivity.class);
            intent.putExtra("pet_home_lat",petHomeLatLng.latitude);
            intent.putExtra("pet_home_lng",petHomeLatLng.longitude);
            intent.putExtra("phone_number",phoneNumber);
            intent.putExtra("idPet",idPet);
            startActivity(intent);
        }
    }


}
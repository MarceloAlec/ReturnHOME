package com.returnhome.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.model.LatLng;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.NfcProvider;
import com.returnhome.utils.AppConfig;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class WriteTagActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationNfc;
    private Button mButtonCancelWrite;
    private TextView mTextViewEnableDeviceInfo;

    private NfcAdapter mNfcAdapter;
    private NfcProvider mNfcProvider;
    private String[][] mTechLists;
    IntentFilter[] mFilters;
    PendingIntent mPendingIntent;
    AppConfig mAppConfig;


    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;
    private String mExtraPetName;
    private String mExtraBreed;
    //private String mExtraPetHome;
    private String mExtraGender;

    private LatLng mPetHomeLatLng;

    private final static int NFC_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);

        mAnimationNfc = findViewById(R.id.animationNFC);
        mButtonCancelWrite = findViewById(R.id.btnCancelWriting);
        mTextViewEnableDeviceInfo = findViewById(R.id.textViewEnableDeviceInfo);
        mAppConfig = new AppConfig(this);
        mNfcProvider = new NfcProvider(this);


        mExtraBreed = getIntent().getStringExtra("breed");
        mExtraGender= getIntent().getStringExtra("gender");
        mExtraPetName = getIntent().getStringExtra("pet_name");
        //mExtraPetHome = getIntent().getStringExtra("pet_home");
        mExtraPetHomeLat = getIntent().getDoubleExtra("pet_home_lat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("pet_home_lng", 0);

        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        //OBTIENE EL ADAPTADOR NFC DEL DISPOSITIVO MOVIL
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null){
            mTextViewEnableDeviceInfo.setText("MANTENGA LA ETIQUETA NFC CONTRA LA PARTE POSTERIOR DE SU DISPOSITIVO MOVIL PARA ESCRIBIR EN ELLA");
            if(!mNfcAdapter.isEnabled()){
                showAlertDialogNONFC();
            }
            else{
                mAnimationNfc.playAnimation();
            }
        }
        else{
            mTextViewEnableDeviceInfo.setText("SU DISPOSITIVO MOVIL NO ES COMPATIBLE CON LA TECNOLOGIA NFC");
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

        mButtonCancelWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




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
        //GUARDA LA INSTANCIA DE LA ETIQUETA DESCUBIERTA
        //CON NfcAdapter.EXTRA_TAG es usado para obtener la informacion de la etiqueta
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        NdefRecord recordPetName = NdefRecord.createTextRecord(Locale.getDefault().getLanguage(),mExtraPetName);
        NdefRecord recordPetBreed = NdefRecord.createTextRecord(Locale.getDefault().getLanguage(),mExtraBreed);
        NdefRecord recordPetGender = NdefRecord.createTextRecord(Locale.getDefault().getLanguage(),mExtraGender);
        //NdefRecord recordPetHome = NdefRecord.createTextRecord(Locale.getDefault().getLanguage(),mExtraPetHome);
        NdefRecord recordTel = NdefRecord.createUri("tel:"+mAppConfig.getPhoneNumber());
        String petHomeLatLng = "geo:" + mPetHomeLatLng.latitude + "," + mPetHomeLatLng.longitude;
        NdefRecord recordLatLng = NdefRecord.createExternal("com.returnhome","geoPetHome",petHomeLatLng.getBytes());

        NdefMessage newMessage = new NdefMessage(new NdefRecord[]{recordPetName, recordPetBreed, recordPetGender, recordTel, recordLatLng});
        showWritingInfo(mNfcProvider.writeNdefMessageToTag(newMessage, tag));

    }

    private void showWritingInfo(String message){
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setCanceledOnTouchOutside(false);
        builder.setTitle("NFC Writer Mode");
        builder.setIcon(R.drawable.edit);
        builder.setMessage(message);
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(NfcProvider.isIsWritingSuccess()){
                    Intent intent = new Intent(WriteTagActivity.this, SelectOptionNfcActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }




}
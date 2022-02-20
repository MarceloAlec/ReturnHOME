package com.returnhome.ui.activities.nfc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.model.LatLng;
import com.returnhome.R;

import com.returnhome.controllers.ClienteController;

import com.returnhome.ui.activities.cliente.HomeActivity;
import com.returnhome.utils.AppConfig;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class EscrituraEtiquetaActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationNfc;
    private CircleImageView mGoToDetailWriting;
    private TextView mTextViewEnableDeviceInfo;

    private NfcAdapter mNfcAdapter;
    private String[][] mTechLists;
    IntentFilter[] mFilters;
    PendingIntent mPendingIntent;
    AppConfig mAppConfig;


    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;
    private int mExtraIdPet;

    private LatLng mPetHomeLatLng;

    private final static int NFC_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);

        initializeComponents();

        mAppConfig = new AppConfig(this);

        mExtraIdPet = getIntent().getIntExtra("idPet",0);
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

        mGoToDetailWriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeComponents(){
        mAnimationNfc = findViewById(R.id.animationNfc);
        mTextViewEnableDeviceInfo = findViewById(R.id.textViewEnableDeviceInfo);
        mGoToDetailWriting = findViewById(R.id.btnGoToDetailWriting);

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
        try{
            //GUARDA LA INSTANCIA DE LA ETIQUETA DESCUBIERTA
            //CON NfcAdapter.EXTRA_TAG es usado para obtener la informacion de la etiqueta
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NdefMessage newMessage = ClienteController.crearMensajeNdef(mExtraIdPet, mAppConfig.obtenerNumeroCelular(), mPetHomeLatLng);
            showWritingInfo(ClienteController.escribirMensajeNdef(newMessage, tag));
        }
        catch(Exception e){
            Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
        }


    }

    private void showWritingInfo(Map<String, String> message){
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setCanceledOnTouchOutside(false);
        builder.setTitle("ReturnHOME");
        builder.setMessage(message.get("message"));
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(message.get("isSuccess").contains("OK")){
                    Intent intent = new Intent(EscrituraEtiquetaActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }




}
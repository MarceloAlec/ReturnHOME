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
import com.returnhome.utils.AppSharedPreferences;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class EscrituraEtiquetaActivity extends AppCompatActivity {

    private LottieAnimationView mAnimacionNfc;
    private CircleImageView mIrADetalleEscritura;
    private TextView mTextViewTelefonoHabilitadoNFCInfo;

    private NfcAdapter mNfcAdapter;
    private String[][] mListaTech;
    IntentFilter[] mFilters;
    PendingIntent mPendingIntent;
    AppSharedPreferences mAppSharedPreferences;


    private double mExtraPetHomeLat;
    private double mExtraPetHomeLng;
    private int mExtraIdPet;

    private LatLng mPetHomeLatLng;

    private final static int NFC_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escritura_etiqueta);

        inicializarComponentes();

        mAppSharedPreferences = new AppSharedPreferences(this);

        mExtraIdPet = getIntent().getIntExtra("idMascota",0);
        mExtraPetHomeLat = getIntent().getDoubleExtra("hogarMascotaLat", 0);
        mExtraPetHomeLng = getIntent().getDoubleExtra("hogarMascotaLng", 0);

        mPetHomeLatLng = new LatLng(mExtraPetHomeLat, mExtraPetHomeLng);

        //OBTIENE EL ADAPTADOR NFC DEL TELEFONO INTELIGENTE
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null){
            mTextViewTelefonoHabilitadoNFCInfo
                    .setText("MANTENGA LA ETIQUETA NFC CONTRA LA PARTE POSTERIOR DE SU DISPOSITIVO MOVIL " +
                            "PARA ESCRIBIR EN ELLA");
            if(!mNfcAdapter.isEnabled()){
                mostrarDialogoActivarNFC();
            }
            else{
                mAnimacionNfc.playAnimation();
            }
        }
        else{

            mTextViewTelefonoHabilitadoNFCInfo.setText("SU DISPOSITIVO MOVIL NO ES COMPATIBLE CON LA TECNOLOGIA NFC");
        }

        //IMPLEMENTACION DEL SISTEMA DE ENVIO EN PRIMER PLANO
        //PARA OBTENER LOS DETALLES DE LA ETIQUETA SE USA PENDING INTENT
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        //SE AÑADE LOS FILTROS DE INTENTS QUE MANEJARÁ LA ETIQUETA, SI ESTE FILTRO COINCIDE
        // CON LA ETIQUETA ENTONCES LA APLICACION MANEJARÁ LA INTENCION
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        mFilters = new IntentFilter[]{ndef};
        //SE AÑADE LA TECNOLOGIA DE ETIQUETA QUE LA APLICACION PUEDE MANEJAR
        mListaTech = new String[][] { new String[] { Ndef.class.getName()}};

        mIrADetalleEscritura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void inicializarComponentes(){
        mAnimacionNfc = findViewById(R.id.animacionNfc);
        mTextViewTelefonoHabilitadoNFCInfo = findViewById(R.id.textViewTelefonoHabilitadoEscrituraNFC);
        mIrADetalleEscritura = findViewById(R.id.btnIrADetalleEscritura);

    }

    private void mostrarDialogoActivarNFC() {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setCanceledOnTouchOutside(false);
        builder.setMessage("Por favor activa NFC para continuar");
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "Configuraciones", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ESPERA Y ESCUCHA HASTA QUE EL USUARIO ACTIVE NFC
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
            mAnimacionNfc.playAnimation();
        }
        else{
            mostrarDialogoActivarNFC();
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
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mListaTech);
        }
    }

    //METODO QUE SE EJECUTA AUTOMATICAMENTE CUANDO LA ETIQUETA ES DESCUBIERTA
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try{

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NdefMessage newMessage = ClienteController
                    .crearMensajeNdef(mExtraIdPet, mAppSharedPreferences.obtenerNumeroCelular(), mPetHomeLatLng);

            mostrarResultadoEscritura(ClienteController.escribirMensajeNdef(newMessage, tag));
        }
        catch(Exception e) {
            Toast.makeText(this, "Ocurrio un error", Toast.LENGTH_LONG).show();
        }

    }

    private void mostrarResultadoEscritura(Map<String, String> resultado){
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setCanceledOnTouchOutside(false);
        builder.setTitle("ReturnHOME");
        builder.setMessage(resultado.get("mensaje"));
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(resultado.get("estado").contains("OK")){
                    Intent intent = new Intent(EscrituraEtiquetaActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }
}
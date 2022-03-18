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
import com.returnhome.controllers.ClienteController;
import com.returnhome.ui.activities.mascota.MapaNotificacionMascotaEncontradaActivity;
import com.returnhome.utils.AppSharedPreferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class LecturaEtiquetaActivity extends AppCompatActivity {

    private LottieAnimationView mAnimacionNfc;
    private CircleImageView mIrAHome;
    private TextView mTextViewTelefonoHabilitadoNFC;

    private NfcAdapter mNfcAdapter;
    private String[][] mListaTech;
    private IntentFilter[] mFilters;
    private PendingIntent mPendingIntent;
    private AppSharedPreferences mAppSharedPreferences;

    private String numeroCelular;
    private LatLng hogarMascotaLatLng;
    private int idMascota;

    private final static int NFC_REQUEST_CODE = 1;

    private boolean mExtraMascotaEncontrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura_etiqueta);

        inicializarComponentes();

        mAppSharedPreferences = new AppSharedPreferences(this);

        mExtraMascotaEncontrada = getIntent().getBooleanExtra("mascotaEncontrada", false);

        //OBTIENE EL ADAPTADOR NFC DEL DISPOSITIVO MOVIL
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null){
            mTextViewTelefonoHabilitadoNFC
                    .setText("MANTENGA LA ETIQUETA NFC CONTRA LA PARTE POSTERIOR DE SU DISPOSITIVO MOVIL PARA LEERLA");
            if(!mNfcAdapter.isEnabled()){
                mostrarCuadroDialogoActivarNFC();
            }
            else{
                mAnimacionNfc.playAnimation();
            }
        }
        else{
            mTextViewTelefonoHabilitadoNFC.setText("SU DISPOSITIVO MOVIL NO ES COMPATIBLE CON LA TECNOLOGIA NFC");
        }

        //IMPLEMENTACION DEL SISTEMA DE ENVIO EN PRIMER PLANO
        //PARA OBTENER LOS DETALLES DE LA ETIQUETA SE USA PENDING INTENT
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        //SE AÑADE LOS FILTROS DE INTENTS QUE MANEJARAN LA ETIQUETA, SI ESTE FILTRO COINCIDE CON LA ETIQUETA ENTONCES LA APLICACION MANEJARÁ LA INTENCION
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        mFilters = new IntentFilter[]{ndef};
        //SE AÑADE LAS TECNOLOGIAS DE ETIQUETAS QUE LA APLICACION PUEDE MANEJAR
        mListaTech = new String[][] { new String[] { Ndef.class.getName() }};

        mIrAHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void inicializarComponentes() {
        mAnimacionNfc = findViewById(R.id.animacionNfc);
        mIrAHome = findViewById(R.id.btnIrAHomeDesdeLecturaEtiqueta);
        mTextViewTelefonoHabilitadoNFC = findViewById(R.id.textViewTelefonoHabilitadoLecturaNFC);
    }

    private void mostrarCuadroDialogoActivarNFC() {
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
            mAnimacionNfc.playAnimation();
        }
        else{
            mostrarCuadroDialogoActivarNFC();
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

        //OBTIENE LOS DATOS CONTENIDOS EN LA INTENCION
        try{
            Parcelable[] rawMensajes= intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage message = ClienteController.obtenerMensajeNdef(rawMensajes);
            NdefRecord record = message.getRecords()[0];
            String tipo = new String(record.getType());

            if(tipo.equals("application/json")){
                String payload = new String(record.getPayload());
                JsonParser parser = new JsonParser();
                JsonObject mascotaInfoJSON = (JsonObject) parser.parse(payload);

                idMascota = Integer.valueOf(mascotaInfoJSON.get("id").toString().replace('"',' ').trim());
                numeroCelular = mascotaInfoJSON.get("tel").toString().replace('"',' ').trim();
                String[] coordenadas = mascotaInfoJSON.get("geo").toString().split(",");
                double latitud = Double.parseDouble(coordenadas[0].replace('"',' ').trim());
                double longitud = Double.parseDouble(coordenadas[1].replace('"',' ').trim());
                hogarMascotaLatLng = new LatLng(latitud,longitud);

                irADetalleLecturaONotificacionMascotaEncontradaActivity();
            }
            else{
                Toast.makeText(this, "Los datos en la etiqueta no se encuentra en formato Json", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e){
            Toast.makeText(this, "Error"+ e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void irADetalleLecturaONotificacionMascotaEncontradaActivity(){
        if(mExtraMascotaEncontrada){
            Intent intent = new Intent(LecturaEtiquetaActivity.this, MapaNotificacionMascotaEncontradaActivity.class);
            intent.putExtra("hogarMascotaLat", hogarMascotaLatLng.latitude);
            intent.putExtra("hogarMascotaLng", hogarMascotaLatLng.longitude);
            intent.putExtra("numeroContacto", numeroCelular);
            intent.putExtra("idMascota", idMascota);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(LecturaEtiquetaActivity.this, MapaDetalleInfoLecturaActivity.class);
            intent.putExtra("hogarMascotaLat", hogarMascotaLatLng.latitude);
            intent.putExtra("hogarMascotaLng", hogarMascotaLatLng.longitude);
            intent.putExtra("numeroContacto", numeroCelular);
            intent.putExtra("idMascota", idMascota);
            startActivity(intent);
        }
    }


}
package com.returnhome.providers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.returnhome.R;
import com.returnhome.ui.activities.SelectOptionNfcActivity;

import java.io.IOException;

public class NfcProvider {

    private Context context;
    private static boolean isWritingSuccess = false;

    public static boolean isIsWritingSuccess() {
        return isWritingSuccess;
    }


    public NfcProvider(Context context){
        this.context = context;
    }

    public String writeNdefMessageToTag(NdefMessage message, Tag tag) {

        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return "La etiqueta es de solo lectura";
                }
                if (ndef.getMaxSize() < size) {
                    return "Los datos a ser escritos (" + size + " bytes) superan el tamaño de la etiqueta ("+ ndef.getMaxSize() + "bytes)";
                }
                ndef.writeNdefMessage(message);
                ndef.close();
                isWritingSuccess = true;
                return "Los datos se han escrito exitosamente";
            } else {
                NdefFormatable ndefFormat = NdefFormatable.get(tag);
                if (ndefFormat != null) {
                    try {
                        ndefFormat.connect();
                        ndefFormat.format(message);
                        ndefFormat.close();
                        isWritingSuccess = true;
                        return "Los datos se han escrito exitosamente";
                    } catch (IOException e) {
                        return "El formato de la etiqueta no es soportado";
                    }
                } else {
                    return "La etiqueta no soporta datos NDEF";
                }
            }
        } catch (Exception e) {
            return "No se pudo escribir los datos a la etiqueta";
        }
    }



}

package com.returnhome.providers;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import java.io.IOException;

public class NfcProvider {

    private static boolean isWritingSuccess = false;

    public static boolean isWritingSuccess() {
        return isWritingSuccess;
    }

    public static NdefMessage createNdefMessage(int idPet, String phoneNumber, LatLng petHomeCoordinates){

        JsonObject petInfo = new JsonObject();
        petInfo.addProperty("id",idPet);
        petInfo.addProperty("tel",phoneNumber);
        petInfo.addProperty("geo",petHomeCoordinates.latitude + "," + petHomeCoordinates.longitude);


        NdefRecord recordPetInfo = NdefRecord.createMime("application/json",petInfo.toString().getBytes());

        return new NdefMessage(new NdefRecord[]{recordPetInfo});

    }

    public static String writeNdefMessageToTag(NdefMessage message, Tag tag) {

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

    public static NdefMessage getNdefMessage(Parcelable[] rawMessages) {
        NdefMessage message = null;

            if (rawMessages != null) {
                //LA ETIQUETA CONTIENE DATOS NDEF
                message = (NdefMessage) rawMessages[0];
            }

        return message;
    }





}

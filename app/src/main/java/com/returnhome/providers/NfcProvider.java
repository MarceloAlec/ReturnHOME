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
import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, String> writeNdefMessageToTag(NdefMessage message, Tag tag) {
        Map<String, String> writingInfo = new HashMap<>();

        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    writingInfo.put("isSuccess","NOK");
                    writingInfo.put("message","La etiqueta es de solo lectura");
                    return writingInfo;
                }
                if (ndef.getMaxSize() < size) {
                    writingInfo.put("isSuccess","NOK");
                    writingInfo.put("message","Los datos a ser escritos ("+ size +" bytes) superan el tamaño de la etiqueta ("+ ndef.getMaxSize() +" bytes)");
                    return writingInfo;
                }
                ndef.writeNdefMessage(message);
                ndef.close();
                writingInfo.put("isSuccess","OK");
                writingInfo.put("message", "Los datos se han escrito exitosamente");
                return writingInfo;
            } else {
                NdefFormatable ndefFormat = NdefFormatable.get(tag);
                if (ndefFormat != null) {
                    try {
                        ndefFormat.connect();
                        ndefFormat.format(message);
                        ndefFormat.close();
                        writingInfo.put("isSuccess","OK");
                        writingInfo.put("message", "Los datos se han escrito exitosamente");
                        return writingInfo;
                    } catch (IOException e) {
                        writingInfo.put("isSuccess","NOK");
                        writingInfo.put("message", "El formato de la etiqueta no es soportado");
                        return writingInfo;
                    }
                } else {
                    writingInfo.put("isSuccess","NOK");
                    writingInfo.put("message", "La etiqueta no soporta datos NDEF");
                    return writingInfo;
                }
            }
        } catch (Exception e) {
            writingInfo.put("isSuccess","NOK");
            writingInfo.put("message", "No se pudo escribir los datos a la etiqueta");
            return writingInfo;
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

package com.returnhome.controllers;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.returnhome.utils.retrofit.RHRespuesta;
import com.returnhome.utils.retrofit.ClienteRetrofit;
import com.returnhome.utils.retrofit.IClienteApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class ClienteController {

    private static final String BASE_URL = "http://192.168.0.3:82/api.returnhome.com/v1/";

    //SE EJECUTAN LAS LLAMADAS AL SERVICIO WEB CON SUS RESPECTIVOS METODOS HTTP

    public static Call<RHRespuesta> registrar(com.returnhome.models.Cliente cliente){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IClienteApi.class).registrar(cliente);
    }

    public static Call<RHRespuesta> obtener(int idClient){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IClienteApi.class).obtener(idClient);
    }

    public static Call<RHRespuesta> autenticar(Map<String,String> auth){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IClienteApi.class).autenticar(auth);
    }

    public static Call<Void> actualizarInfo(com.returnhome.models.Cliente cliente){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IClienteApi.class).actualizar(cliente);
    }

    public static Call<Void> actualizarPassword(Map<String, String> password){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IClienteApi.class).actualizarPassword(password);
    }

    public static Call<Void> eliminarCuenta(int idClient){
        return ClienteRetrofit.obtenerCliente(BASE_URL).create(IClienteApi.class).eliminar(idClient);
    }

    public static NdefMessage crearMensajeNdef(int idPet, String phoneNumber, LatLng petHomeCoordinates){

        JsonObject petInfo = new JsonObject();
        petInfo.addProperty("id",idPet);
        petInfo.addProperty("tel",phoneNumber);
        petInfo.addProperty("geo",petHomeCoordinates.latitude + "," + petHomeCoordinates.longitude);

        NdefRecord recordPetInfo = NdefRecord.createMime("application/json",petInfo.toString().getBytes());

        return new NdefMessage(new NdefRecord[]{recordPetInfo});

    }

    public static Map<String, String> escribirMensajeNdef(NdefMessage mensaje, Tag tag) {

        Map<String, String> infoEscritura = new HashMap<>();

        int tamano = mensaje.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    infoEscritura.put("estado","NOK");
                    infoEscritura.put("mensaje","La etiqueta es de solo lectura");
                    return infoEscritura;
                }
                if (ndef.getMaxSize() < tamano) {
                    infoEscritura.put("estado","NOK");
                    infoEscritura
                            .put("mensaje","Los datos a ser escritos ("+ tamano +" bytes) " +
                                    "superan el tamaño de la etiqueta ("+ ndef.getMaxSize() +" bytes)");
                    return infoEscritura;
                }
                ndef.writeNdefMessage(mensaje);
                ndef.close();
                infoEscritura.put("estado","OK");
                infoEscritura.put("mensaje", "Los datos se han escrito exitosamente");
                return infoEscritura;
            }
            else{
                infoEscritura.put("estado","NOK");
                infoEscritura.put("mensaje", "No se pudo escribir los datos a la etiqueta");
                return infoEscritura;
            }

        } catch (Exception e) {
            infoEscritura.put("estado","NOK");
            infoEscritura.put("mensaje", "No se pudo escribir los datos a la etiqueta");
            return infoEscritura;
        }
    }

    public static NdefMessage obtenerMensajeNdef(Parcelable[] rawMessages) {
        NdefMessage message = null;

        if (rawMessages != null) {
            //LA ETIQUETA CONTIENE DATOS NDEF
            message = (NdefMessage) rawMessages[0];
        }

        return message;
    }



}

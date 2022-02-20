package com.returnhome.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.returnhome.R;

public class AppConfig {

    private Context context;
    private SharedPreferences sharedPreferences;

    public AppConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("pref_archivo",Context.MODE_PRIVATE);
    }

    public boolean comprobarClienteAuth(){
        return sharedPreferences.getBoolean("usuarioAuth", false);
    }

    public void actualizarEstadoAuth(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("clienteAuth",status);
        editor.apply();
    }

    public void guardarNombreCliente(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nombreCliente",name);
        editor.apply();
    }

    public String obtenerNombreCliente(){
        return sharedPreferences.getString("nombreCliente", "Desconocido");
    }

    public void guardarIdCliente(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idCliente",id);
        editor.apply();
    }

    public int obtenerIdCliente(){
        return sharedPreferences.getInt("idCliente", 0);
    }

    public void guardarNumeroCelular(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("numeroCelular",phoneNumber);
        editor.apply();
    }

    public String obtenerNumeroCelular(){
        return sharedPreferences.getString("numeroCelular", "Desconocido");
    }

    public void guardarToken(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",phoneNumber);
        editor.apply();
    }

    public String obtenerToken(){
        return sharedPreferences.getString("token", "Desconocido");
    }




}

package com.returnhome.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.returnhome.R;

public class AppSharedPreferences {
    private Context context;
    private SharedPreferences sharedPreferences;

    public AppSharedPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("pref_archivo",Context.MODE_PRIVATE);
    }

    public boolean comprobarClienteAuth(){
        return sharedPreferences.getBoolean("clienteAuth", false);
    }

    public void actualizarEstadoAuth(boolean estado){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("clienteAuth",estado);
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

    public void guardarIdCliente(int idCliente) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idCliente",idCliente);
        editor.apply();
    }

    public int obtenerIdCliente(){
        return sharedPreferences.getInt("idCliente", 0);
    }

    public void guardarNumeroCelular(String numeroCelular) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("numeroCelular",numeroCelular);
        editor.apply();
    }

    public String obtenerNumeroCelular(){
        return sharedPreferences.getString("numeroCelular", "Desconocido");
    }

    public void guardarToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token);
        editor.apply();
    }

    public String obtenerToken(){
        return sharedPreferences.getString("token", "Desconocido");
    }
}

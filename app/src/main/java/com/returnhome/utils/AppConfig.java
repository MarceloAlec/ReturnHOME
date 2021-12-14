package com.returnhome.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.returnhome.R;

public class AppConfig {

    private Context context;
    private SharedPreferences sharedPreferences;

    public AppConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file_key),Context.MODE_PRIVATE);
    }

    public boolean isUserLogin(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_is_user_login), false);
    }

    public void updateLoginStatus(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_is_user_login),status);
        editor.apply();
    }

    public void saveUserName(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_username),name);
        editor.apply();
    }

    public String getUserName(){
        return sharedPreferences.getString(context.getString(R.string.pref_username), "Unknown");
    }

    public void saveUserId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_user_id),id);
        editor.apply();
    }

    public int getUserId(){
        return sharedPreferences.getInt(context.getString(R.string.pref_user_id), 0);
    }

    public void saveUserPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_user_phonenumber),phoneNumber);
        editor.apply();
    }

    public String getPhoneNumber(){
        return sharedPreferences.getString(context.getString(R.string.pref_user_phonenumber), "Unknown");
    }

    public void saveUserToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_user_token),token);
        editor.apply();
    }

    public String getToken(){
        return sharedPreferences.getString(context.getString(R.string.pref_user_token), "Unknown");
    }




}

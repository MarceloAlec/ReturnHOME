package com.returnhome.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProvider {

    public static Task<Void> delete(){
        return FirebaseMessaging.getInstance().deleteToken();
    }

    public static Task<String> create(){

        return FirebaseMessaging.getInstance().getToken();
    }


}

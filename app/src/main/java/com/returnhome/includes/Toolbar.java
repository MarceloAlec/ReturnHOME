package com.returnhome.includes;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.returnhome.R;
import com.returnhome.ui.activities.client.HomeActivity;
import com.returnhome.ui.activities.pet.MapPetFoundActivity;

public class Toolbar {

    public static void show(AppCompatActivity activity, String title, boolean upButton){
        //Para ejecutar el findViewById se necesita un contexto
        androidx.appcompat.widget.Toolbar mToolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    activity.finish();
            }
        });
    }
}

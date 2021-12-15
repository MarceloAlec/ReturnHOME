package com.returnhome.ui.activities.nfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.returnhome.R;
import com.returnhome.includes.Toolbar;
import com.returnhome.ui.activities.pet.MapPetHomeActivity;

public class SelectOptionNfcActivity extends AppCompatActivity {

    private LinearLayout mButtonGoToWriteTag;
    private LinearLayout mButtonGoToReadTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_nfc);

        mButtonGoToWriteTag = findViewById(R.id.btnGoToWriteTag);
        mButtonGoToReadTag = findViewById(R.id.btnGoToReadTag);
        mButtonGoToWriteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectOptionNfcActivity.this, MapPetHomeActivity.class);
                startActivity(intent);
            }
        });

        mButtonGoToReadTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectOptionNfcActivity.this, ReadTagActivity.class);
                startActivity(intent);
            }
        });

        Toolbar.show(this, "Seleccionar opción", true);
    }
}
package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.returnhome.R;

public class SelectOptionNfcActivity extends AppCompatActivity {

    private LinearLayout mButtonGoToWriteTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_nfc);

        mButtonGoToWriteTag = findViewById(R.id.btnGoToWriteTag);
        mButtonGoToWriteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectOptionNfcActivity.this, DetailWritingActivity.class);
                startActivity(intent);
            }
        });
    }
}
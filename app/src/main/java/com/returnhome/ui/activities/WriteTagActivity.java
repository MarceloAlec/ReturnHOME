package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.returnhome.R;

public class WriteTagActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationNfc;
    private Button mButtonCancelWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag);

        mAnimationNfc = findViewById(R.id.animationNFC);
        mButtonCancelWrite = findViewById(R.id.btnCancelWriting);

        mAnimationNfc.playAnimation();

        mButtonCancelWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
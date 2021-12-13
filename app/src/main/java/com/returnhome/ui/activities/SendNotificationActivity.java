package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.returnhome.R;

public class SendNotificationActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationNotification;
    private Button mCancelNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        mAnimationNotification = findViewById(R.id.animationNotification);
        mCancelNotification = findViewById(R.id.btnCancelNotification);

        mAnimationNotification.playAnimation();
        mCancelNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
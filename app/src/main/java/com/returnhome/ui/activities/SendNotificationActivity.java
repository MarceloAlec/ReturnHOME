package com.returnhome.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.returnhome.R;
import com.returnhome.models.Client;
import com.returnhome.models.FCMBody;
import com.returnhome.models.FCMResponse;
import com.returnhome.models.RHResponse;
import com.returnhome.providers.ClientProvider;
import com.returnhome.providers.NotificationProvider;
import com.returnhome.utils.AppConfig;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationActivity extends AppCompatActivity {

    private LottieAnimationView mAnimationNotification;

    private TextView mTextViewNotificationInfo;

    private double mExtraPetLat;
    private double mExtraPetLng;
    private String mExtraPetName;
    private int mExtraIdClient;

    private CircleImageView mCircleImageReturnMapPetHome;

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        mAnimationNotification = findViewById(R.id.animationNotification);

        mCircleImageReturnMapPetHome = findViewById(R.id.btnGoToMapPet);
        mTextViewNotificationInfo = findViewById(R.id.textViewNotificacionInfo);

        mExtraIdClient = getIntent().getIntExtra("id_client", 0);
        mExtraPetName = getIntent().getStringExtra("pet_name");
        mExtraPetLat = getIntent().getDoubleExtra("pet_lat", 0);
        mExtraPetLng = getIntent().getDoubleExtra("pet_lng", 0);

        mAnimationNotification.playAnimation();

        getClient();

        mCircleImageReturnMapPetHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void sendNotification(){
        String token = client.getToken();
        if(!token.equals("Unknown") || !token.equals(null)){
            Map<String, String> map = new HashMap<>();
            map.put("title","Mascota encontrada");
            map.put("body",mExtraPetName+" fue encontrada en las siguientes coordenadas:" +
                    "\nLatitud: "+mExtraPetLat+"" +
                    "\nLongitud: "+mExtraPetLng
            );
            map.put("pet_name",mExtraPetName);
            map.put("pet_lat",String.valueOf(mExtraPetLat));
            map.put("pet_lng",String.valueOf(mExtraPetLng));
            FCMBody fcmBody = new FCMBody(token, "high", map);
            NotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                    if(response.body() != null){
                        if(response.body().getSuccess() == 1){
                            mTextViewNotificationInfo.setText("LA NOTIFICACION SE HA ENVIADO CORRECTAMENTE");
                        }
                        else{
                            mTextViewNotificationInfo.setText("NO SE PUDO ENVIAR LA NOTIFICACION");
                        }
                    }
                    else{
                        mTextViewNotificationInfo.setText("NO SE PUDO ENVIAR LA NOTIFICACION");
                    }

                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {
                    mTextViewNotificationInfo.setText("NO SE PUDO ENVIAR LA NOTIFICACION");
                }
            });
        }
        else{
            mTextViewNotificationInfo.setText("NO SE PUDO ENVIAR LA NOTIFICACION");
        }


    }

    private void getClient() {
        ClientProvider.getClient(mExtraIdClient).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if(response.isSuccessful()){
                    client = response.body().getClient();
                    sendNotification();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                mTextViewNotificationInfo.setText("NO SE PUDO ENVIAR LA NOTIFICACION");
            }
        });
    }
}
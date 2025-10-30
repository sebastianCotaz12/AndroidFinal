package com.example.myapplication.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Crear canal de notificaciones para FCM (obligatorio Android 8+)
        createNotificationChannel();


        // 🔹 Referencias UI
        ImageView logo = findViewById(R.id.imginicio);
        Button btnInicio = findViewById(R.id.btnStart);

        // 🔹 Animación del logo
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1200);
        fadeIn.setInterpolator(new DecelerateInterpolator());

        ScaleAnimation scaleUp = new ScaleAnimation(
                0.7f, 1f,
                0.7f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleUp.setDuration(1200);
        scaleUp.setInterpolator(new DecelerateInterpolator());

        AnimationSet logoAnimSet = new AnimationSet(true);
        logoAnimSet.addAnimation(fadeIn);
        logoAnimSet.addAnimation(scaleUp);
        logoAnimSet.setFillAfter(true);
        logo.startAnimation(logoAnimSet);

        // 🔹 Animación del botón
        TranslateAnimation slideUp = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        slideUp.setDuration(900);
        slideUp.setInterpolator(new DecelerateInterpolator());
        slideUp.setStartOffset(1000);

        AlphaAnimation fadeButton = new AlphaAnimation(0f, 1f);
        fadeButton.setDuration(900);
        fadeButton.setStartOffset(1000);
        fadeButton.setInterpolator(new DecelerateInterpolator());

        AnimationSet btnAnimSet = new AnimationSet(true);
        btnAnimSet.addAnimation(slideUp);
        btnAnimSet.addAnimation(fadeButton);
        btnAnimSet.setFillAfter(true);
        btnInicio.startAnimation(btnAnimSet);

        // 🔹 Click → Login
        btnInicio.setOnClickListener(v -> {
            Animation press = new ScaleAnimation(
                    1f, 0.95f,
                    1f, 0.95f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            press.setDuration(100);
            btnInicio.startAnimation(press);

            Intent intent = new Intent(MainActivity.this, InicioSesion.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    /**
     * Crea el canal de notificación "events" para FCM (solo una vez)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "events";
            CharSequence channelName = "Eventos importantes";
            String channelDescription = "Notificaciones sobre eventos nuevos";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


}

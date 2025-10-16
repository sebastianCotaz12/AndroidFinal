package com.example.myapplication.controller;

import android.content.Intent;
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

        // 🔹 Referencias de vista
        ImageView logo = findViewById(R.id.imginicio);
        Button btnInicio = findViewById(R.id.btnStart);

        // ---------------------------
        // 🔹 ANIMACIÓN DEL LOGO
        // ---------------------------
        // Fade in suave
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1200);
        fadeIn.setInterpolator(new DecelerateInterpolator());

        // Zoom-in elegante
        ScaleAnimation scaleUp = new ScaleAnimation(
                0.7f, 1f,
                0.7f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleUp.setDuration(1200);
        scaleUp.setInterpolator(new DecelerateInterpolator());

        // Combinar fade + scale
        AnimationSet logoAnimSet = new AnimationSet(true);
        logoAnimSet.addAnimation(fadeIn);
        logoAnimSet.addAnimation(scaleUp);
        logoAnimSet.setFillAfter(true);
        logo.startAnimation(logoAnimSet);

        // ---------------------------
        // 🔹 ANIMACIÓN DEL BOTÓN
        // ---------------------------
        // Deslizamiento desde abajo
        TranslateAnimation slideUp = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        slideUp.setDuration(900);
        slideUp.setInterpolator(new DecelerateInterpolator());
        slideUp.setStartOffset(1000); // comienza después del logo

        // Aparición gradual
        AlphaAnimation fadeButton = new AlphaAnimation(0f, 1f);
        fadeButton.setDuration(900);
        fadeButton.setStartOffset(1000);
        fadeButton.setInterpolator(new DecelerateInterpolator());

        // Combinar ambas animaciones
        AnimationSet btnAnimSet = new AnimationSet(true);
        btnAnimSet.addAnimation(slideUp);
        btnAnimSet.addAnimation(fadeButton);
        btnAnimSet.setFillAfter(true);
        btnInicio.startAnimation(btnAnimSet);

        // ---------------------------
        // 🔹 EVENTO CLICK → LOGIN
        // ---------------------------
        btnInicio.setOnClickListener(v -> {
            btnInicio.startAnimation(new ScaleAnimation(
                    1f, 0.95f,
                    1f, 0.95f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            ));
            Intent intent = new Intent(MainActivity.this, InicioSesion.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // transición suave
            finish();
        });
    }
}

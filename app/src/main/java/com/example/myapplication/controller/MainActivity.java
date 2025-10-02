package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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

        ImageView logo = findViewById(R.id.imginicio);
        Button btnInicio = findViewById(R.id.btninicio);

        // 🔹 Animación Logo (fade + scale)
        Animation animLogo = new AlphaAnimation(0f, 1f);
        animLogo.setDuration(1000);

        Animation scaleLogo = new ScaleAnimation(
                0.7f, 1.0f, // X
                0.7f, 1.0f, // Y
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot X
                Animation.RELATIVE_TO_SELF, 0.5f  // Pivot Y
        );
        scaleLogo.setDuration(1000);

        logo.startAnimation(animLogo);
        logo.startAnimation(scaleLogo);

        // 🔹 Animación Botón (slide-up + fade)
        Animation animBtn = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        animBtn.setDuration(800);
        animBtn.setStartOffset(1000);

        Animation fadeBtn = new AlphaAnimation(0f, 1f);
        fadeBtn.setDuration(800);
        fadeBtn.setStartOffset(1000);

        btnInicio.startAnimation(animBtn);
        btnInicio.startAnimation(fadeBtn);

        // 🔹 Evento click → Ir al login (inicioSesion)
        btnInicio.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InicioSesion.class);
            startActivity(intent);
            finish(); // cerrar MainActivity para que no regrese
        });
    }
}

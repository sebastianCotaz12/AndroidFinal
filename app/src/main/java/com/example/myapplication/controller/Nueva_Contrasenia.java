package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class Nueva_Contrasenia extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_contrasenia);

        // Ajuste de padding para status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencia al botón
        Button btnVolverLogin = findViewById(R.id.btnVolverLogin);

        // Acción del botón: volver a inicioSesion
        btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Nueva_Contrasenia.this, inicioSesion.class);
            startActivity(intent);
            finish(); // opcional: cerrar la pantalla actual
        });
    }
}

package com.example.myapplication.controller;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class EventDetail extends AppCompatActivity {

    TextView tvTitulo, tvDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_event_detail);

        tvTitulo = findViewById(R.id.tvTitulo);
        tvDescripcion = findViewById(R.id.tvDescripcion);

        // 🔹 Obtener extras
        String eventId = getIntent().getStringExtra("eventId");
        String tenantId = getIntent().getStringExtra("tenantId");

        // 🔹 Mostrar los datos (puedes hacer una llamada al backend aquí)
        tvTitulo.setText("Evento ID: " + eventId);
        tvDescripcion.setText("Empresa ID: " + tenantId);
    }
}
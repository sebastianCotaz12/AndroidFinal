package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class detalles_actLudicas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_act_ludicas);

        // Referencias a los TextView del layout
        TextView tvUsuario = findViewById(R.id.tvUsuario);
        TextView tvNombreActividad = findViewById(R.id.tvNombreActividad);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        TextView tvEvidencia = findViewById(R.id.tvEvidencia);

        // Obtener datos del intent
        Intent intent = getIntent();

        if (intent != null) {
            tvUsuario.setText(intent.getStringExtra("usuario"));
            tvNombreActividad.setText(intent.getStringExtra("nombre_actividad"));
            tvFecha.setText(intent.getStringExtra("fecha"));
            tvDescripcion.setText(intent.getStringExtra("descripcion"));
            tvEvidencia.setText(intent.getStringExtra("evidencia"));
        }
    }
}
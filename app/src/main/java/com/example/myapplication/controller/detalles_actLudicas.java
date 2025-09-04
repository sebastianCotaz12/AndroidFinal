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
            int id = intent.getIntExtra("id", -1); // por si lo necesitas
            String usuario = intent.getStringExtra("usuario");
            String nombreActividad = intent.getStringExtra("nombreActividad");
            String fecha = intent.getStringExtra("fecha");
            String descripcion = intent.getStringExtra("descripcion");
            String archivoAdjunto = intent.getStringExtra("archivoAdjunto");
            String imagenVideo = intent.getStringExtra("imagenVideo");

            // Setear en los TextView
            tvUsuario.setText(usuario);
            tvNombreActividad.setText(nombreActividad);
            tvFecha.setText(fecha);
            tvDescripcion.setText(descripcion);

            // Si hay archivo adjunto lo mostramos, si no, el video
            if (archivoAdjunto != null && !archivoAdjunto.isEmpty()) {
                tvEvidencia.setText(archivoAdjunto);
            } else if (imagenVideo != null && !imagenVideo.isEmpty()) {
                tvEvidencia.setText(imagenVideo);
            } else {
                tvEvidencia.setText("Sin evidencia");
            }
        }
    }
}

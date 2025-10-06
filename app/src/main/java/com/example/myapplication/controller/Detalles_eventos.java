package com.example.myapplication.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Detalles_eventos extends AppCompatActivity {

    private TextView tvTitulo, tvFecha, tvDescripcion;
    private ImageView ivPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);

        // Referencias a los TextView del layout
        tvTitulo = findViewById(R.id.tvTituloEvento);
        tvFecha = findViewById(R.id.tvFechaEvento);
        tvDescripcion = findViewById(R.id.tvDescripcionEvento);
        ivPreview = findViewById(R.id.ivPreviewEventoDetalle);

        // Obtener datos del intent
        Intent intent = getIntent();


        if (intent != null) {
            String tituloEvento  = intent.getStringExtra("tituloEvento");
            String fechaEvento = intent.getStringExtra("fechaEvento");
            String descripcionEvento = intent.getStringExtra("descripcionEvento");
            String Adjuntar = intent.getStringExtra("adjuntar");
            String imagen = intent.getStringExtra("imagen");
            ivPreview.setVisibility(ImageView.VISIBLE);


            // Setear en los TextView
            tvTitulo.setText(tituloEvento);
            tvFecha.setText(fechaEvento);
            tvDescripcion.setText(descripcionEvento);

            // Si hay archivo adjunto lo mostramos, si no, el video
            if (Adjuntar != null && !Adjuntar.isEmpty()) {
                ivPreview.setVisibility(Integer.parseInt(Adjuntar));
            } else if (imagen != null && !imagen.isEmpty()) {
                ivPreview.setVisibility(Integer.parseInt(imagen));
            } else {
                ivPreview.setVisibility(Integer.parseInt("Sin evidencia"));
            }
        }

        }
    }


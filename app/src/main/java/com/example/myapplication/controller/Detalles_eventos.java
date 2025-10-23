package com.example.myapplication.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

public class Detalles_eventos extends AppCompatActivity {

    private ImageView imgEventoDetalle;
    private TextView tvTituloEventoDetalle, tvFechaEventoDetalle, tvUsuarioEventoDetalle, tvDescripcionEventoDetalle;
    private Button btnAbrirArchivo, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);

        // Enlazar vistas
        imgEventoDetalle = findViewById(R.id.imgEventoDetalle);
        tvTituloEventoDetalle = findViewById(R.id.tvTituloEventoDetalle);
        tvFechaEventoDetalle = findViewById(R.id.tvFechaEventoDetalle);
        tvUsuarioEventoDetalle = findViewById(R.id.tvUsuarioEventoDetalle);
        tvDescripcionEventoDetalle = findViewById(R.id.tvDescripcionEventoDetalle);
        btnAbrirArchivo = findViewById(R.id.btnAbrirArchivo);
        btnVolver = findViewById(R.id.btnVolver);
        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            Intent intent = new Intent(Detalles_eventos.this, Lista_eventos.class);
            startActivity(intent);
            finish();
        });

        // Obtener datos del Intent
        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        String fecha = intent.getStringExtra("fecha");
        String descripcion = intent.getStringExtra("descripcion");
        String imagen = intent.getStringExtra("imagen");
        String archivo = intent.getStringExtra("archivo");
        String nombreUsuario = intent.getStringExtra("nombre_usuario");

        // Asignar valores
        tvTituloEventoDetalle.setText(titulo);
        tvFechaEventoDetalle.setText("Fecha: " + fecha);
        tvUsuarioEventoDetalle.setText("Publicado por: " + nombreUsuario);
        tvDescripcionEventoDetalle.setText(descripcion);

        // Cargar imagen (o placeholder)
        if (imagen != null && !imagen.isEmpty()) {
            Glide.with(this)
                    .load(imagen)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imgEventoDetalle);
        } else {
            imgEventoDetalle.setImageResource(R.drawable.placeholder_image);
        }

        // Acción para abrir archivo adjunto
        btnAbrirArchivo.setOnClickListener(v -> {
            if (archivo != null && !archivo.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(archivo));
                startActivity(browserIntent);
            }
        });

        // Acción volver
        btnVolver.setOnClickListener(v -> finish());
    }
}

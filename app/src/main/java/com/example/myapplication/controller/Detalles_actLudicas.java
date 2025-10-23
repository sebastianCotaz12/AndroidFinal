package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Detalles_actLudicas extends AppCompatActivity {

    TextView tvUsuario, tvNombreActividad, tvFecha, tvDescripcion;
    ImageView ivEvidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_act_ludicas);

        tvUsuario = findViewById(R.id.tvUsuario);
        tvNombreActividad = findViewById(R.id.tvNombreActividad);
        tvFecha = findViewById(R.id.tvFecha);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        ivEvidencia = findViewById(R.id.ivEvidencia);

        tvUsuario.setText(getIntent().getStringExtra("usuario"));
        tvNombreActividad.setText(getIntent().getStringExtra("nombreActividad"));

        // Formatear la fecha
        String fechaOriginal = getIntent().getStringExtra("fecha");
        String fechaFormateada = formatearFecha(fechaOriginal);
        tvFecha.setText(fechaFormateada);

        tvDescripcion.setText(getIntent().getStringExtra("descripcion"));

        String archivoAdjunto = getIntent().getStringExtra("archivoAdjunto");
        if (archivoAdjunto != null && !archivoAdjunto.isEmpty()) {
            Glide.with(this)
                    .load(archivoAdjunto)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivEvidencia);
        }

        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            Intent intent = new Intent(Detalles_actLudicas.this, Lista_actLudicas.class);
            startActivity(intent);
            finish();
        });
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return "Fecha no disponible";
        }

        try {
            // Para formato: 2025-10-22T00:00:00.000Z
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            // Si falla, intentar con formato simple
            try {
                String soloFecha = fechaOriginal.split("T")[0];
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date fecha = formatoEntrada.parse(soloFecha);
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
                return formatoSalida.format(fecha);
            } catch (Exception ex) {
                return fechaOriginal; // Devolver la fecha original si no se puede formatear
            }
        }
    }
}
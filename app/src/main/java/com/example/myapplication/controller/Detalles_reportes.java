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

public class Detalles_reportes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_reportes);

        TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        TextView tvCargo = findViewById(R.id.tvCargo);
        TextView tvCedula = findViewById(R.id.tvCedula);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvLugar = findViewById(R.id.tvLugar);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        ImageView imgReporte = findViewById(R.id.imgReporte);
        TextView tvArchivos = findViewById(R.id.tvArchivos);
        TextView tvEstado = findViewById(R.id.tvEstado);

        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();

        tvNombreUsuario.setText( formatearNombre(getExtra(intent, "nombre_usuario")));
        tvCargo.setText(getExtra(intent, "cargo"));
        tvCedula.setText(getExtra(intent, "cedula"));
        tvFecha.setText(formatearFecha(getExtra(intent, "fecha")));
        tvLugar.setText(getExtra(intent, "lugar"));
        tvDescripcion.setText(getExtra(intent, "descripcion"));
        tvArchivos.setText(getExtra(intent, "archivos"));
        tvEstado.setText(getExtra(intent, "estado"));

        cargarImagen(getExtra(intent, "imagen"), imgReporte);
    }

    private String getExtra(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        return value != null && !value.equals("null") ? value : "No disponible";
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty() || fechaOriginal.equals("No disponible")) {
            return "No disponible";
        }

        try {
            SimpleDateFormat formatoEntrada;
            Date fecha;

            if (fechaOriginal.contains("T")) {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            }

            fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            return fechaOriginal;
        }
    }

    private String formatearNombre(String nombre) {
        if (nombre == null || nombre.equals("No disponible")) {
            return "No disponible";
        }

        // Remover corchetes si existen
        if (nombre.startsWith("[") && nombre.endsWith("]")) {
            nombre = nombre.substring(1, nombre.length() - 1);
        }

        // Remover comillas si existen
        if (nombre.startsWith("\"") && nombre.endsWith("\"")) {
            nombre = nombre.substring(1, nombre.length() - 1);
        }

        return nombre.trim();
    }

    private void cargarImagen(String imagenUrl, ImageView imageView) {
        if (imagenUrl != null && !imagenUrl.isEmpty() && !imagenUrl.equals("No disponible") && !imagenUrl.equals("null")) {
            Glide.with(this)
                    .load(imagenUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_image);
        }
    }
}
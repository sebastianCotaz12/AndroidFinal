package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

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
        TextView tvImagen = findViewById(R.id.tvImagen);
        TextView tvArchivos = findViewById(R.id.tvArchivos);
        TextView tvEstado = findViewById(R.id.tvEstado);

        Intent intent = getIntent();

        tvNombreUsuario.setText("Nombre Usuario: " + getExtra(intent, "nombre_usuario"));
        tvCargo.setText("Cargo: " + getExtra(intent, "cargo"));
        tvCedula.setText("Cédula: " + getExtra(intent, "cedula"));
        tvFecha.setText("Fecha: " + getExtra(intent, "fecha"));
        tvLugar.setText("Lugar: " + getExtra(intent, "lugar"));
        tvDescripcion.setText("Descripción: " + getExtra(intent, "descripcion"));
        tvImagen.setText("Imagen: " + getExtra(intent, "imagen"));
        tvArchivos.setText("Archivos: " + getExtra(intent, "archivos"));
        tvEstado.setText("Estado: " + getExtra(intent, "estado"));
    }

    private String getExtra(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        return value != null ? value : "No disponible";
    }
}
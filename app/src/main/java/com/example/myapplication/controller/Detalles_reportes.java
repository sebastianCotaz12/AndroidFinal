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

        // Referencias a los TextView de tu XML
        TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        TextView tvCargo = findViewById(R.id.tvCargo);
        TextView tvCedula = findViewById(R.id.tvCedula);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvLugar = findViewById(R.id.tvLugar);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        TextView tvImagen = findViewById(R.id.tvImagen);
        TextView tvArchivos = findViewById(R.id.tvArchivos);
        TextView tvEstado = findViewById(R.id.tvEstado);

        // Obtener datos que llegan del Intent
        Intent intent = getIntent();

        tvNombreUsuario.setText("Nombre Usuario: " + intent.getStringExtra("nombre_usuario"));
        tvCargo.setText("Cargo: " + intent.getStringExtra("cargo"));
        tvCedula.setText("Cédula: " + intent.getStringExtra("cedula"));
        tvFecha.setText("Fecha: " + intent.getStringExtra("fecha"));
        tvLugar.setText("Lugar: " + intent.getStringExtra("lugar"));
        tvDescripcion.setText("Descripción: " + intent.getStringExtra("descripcion"));
        tvImagen.setText("Imagen: " + intent.getStringExtra("imagen"));
        tvArchivos.setText("Archivos: " + intent.getStringExtra("archivos"));
        tvEstado.setText("Estado: " + intent.getStringExtra("estado"));
    }
}

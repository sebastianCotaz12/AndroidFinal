package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Detalles_gestionEPP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_gestion_epp); // nombre del XML que compartiste

        // Referencias a los TextView
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvApellido = findViewById(R.id.tvApellido);
        TextView tvCedula = findViewById(R.id.tvCedula);
        TextView tvCargo = findViewById(R.id.tvCargo);
        TextView tvProductos = findViewById(R.id.tvProductos);
        TextView tvCantidad = findViewById(R.id.tvCantidad);
        TextView tvImportancia = findViewById(R.id.tvImportancia);
        TextView tvEstado = findViewById(R.id.tvEstado);
        TextView tvFechaCreacion = findViewById(R.id.tvFechaCreacion);

        // Obtener datos desde el Intent
        Intent intent = getIntent();

        if (intent != null) {
            tvNombre.setText(intent.getStringExtra("nombre"));
            tvApellido.setText(intent.getStringExtra("apellido"));
            tvCedula.setText(intent.getStringExtra("cedula"));
            tvCargo.setText(intent.getStringExtra("cargo"));
            tvProductos.setText(intent.getStringExtra("productos"));
            tvCantidad.setText(intent.getStringExtra("cantidad"));
            tvImportancia.setText(intent.getStringExtra("importancia"));
            tvEstado.setText(intent.getStringExtra("estado"));
            tvFechaCreacion.setText(intent.getStringExtra("fecha_creacion"));
        }
    }
}

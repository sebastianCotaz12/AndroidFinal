package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Detalles_gestionEPP extends AppCompatActivity {

    private TextView tvId, tvCedula, tvImportancia, tvEstado, tvFechaCreacion,
            tvProductos, tvCargo, tvArea, tvCantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_gestion_epp);

        // Referencias a los TextView del layout
        tvId = findViewById(R.id.tvIdG);
        tvCedula = findViewById(R.id.tvCedula);
        tvImportancia = findViewById(R.id.tvImportancia);
        tvEstado = findViewById(R.id.tvEstado);
        tvFechaCreacion = findViewById(R.id.tvFechaCreacion);
        tvProductos = findViewById(R.id.tvProductos);
        tvCargo = findViewById(R.id.tvCargo);
        tvArea = findViewById(R.id.tvNombre);
        tvCantidad = findViewById(R.id.tvCantidad);
        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            Intent intent = new Intent(Detalles_gestionEPP.this, Lista_gestionEpp.class);
            startActivity(intent);
            finish();
        });

        // Obtener los datos del Intent
        Intent intent = getIntent();

        tvId.setText("ID: " + getExtra(intent, "id"));
        tvCedula.setText("Cédula: " + getExtra(intent, "cedula"));
        tvImportancia.setText("Importancia: " + getExtra(intent, "importancia"));
        tvEstado.setText("Estado: " + getExtra(intent, "estado"));
        tvFechaCreacion.setText("Fecha de creación: " + getExtra(intent, "fecha_creacion"));
        tvProductos.setText("Productos: " + getExtra(intent, "productos"));
        tvCargo.setText("Cargo: " + getExtra(intent, "cargo"));
        tvArea.setText("Área: " + getExtra(intent, "area"));
        tvCantidad.setText("Cantidad: " + getExtra(intent, "cantidad"));
    }

    // Helper para evitar valores nulos
    private String getExtra(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        return value != null ? value : "No disponible";
    }
}

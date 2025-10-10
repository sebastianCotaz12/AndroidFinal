package com.example.myapplication.controller;

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

        // Obtener los datos del Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            String cedula = extras.getString("cedula");
            String importancia = extras.getString("importancia");
            String estado = extras.getString("estado");
            String fecha = extras.getString("fecha_creacion");
            String productos = extras.getString("productos");
            String cargo = extras.getString("cargo");
            String area = extras.getString("area");
            int cantidad = extras.getInt("cantidad");

            // Mostrar los datos en los TextView
            tvId.setText("ID: " + id);
            tvCedula.setText("Cédula: " + cedula);
            tvImportancia.setText("Importancia: " + importancia);
            tvEstado.setText("Estado: " + estado);
            tvFechaCreacion.setText("Fecha de creación: " + fecha);
            tvProductos.setText("Productos: " + productos);
            tvCargo.setText("Cargo: " + cargo);
            tvArea.setText("Área: " + area);
            tvCantidad.setText("Cantidad: " + cantidad);
        }
    }
}

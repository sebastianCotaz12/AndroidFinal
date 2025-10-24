package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Detalles_gestionEPP extends AppCompatActivity {

    private TextView tvNombre, tvCedula, tvImportancia, tvEstado, tvFechaCreacion,
            tvProductos, tvCargo, tvNombreArea, tvCantidad, tvEstadoHeader;

    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_gestion_epp);

        prefsManager = new PrefsManager(this);

        // Referencias a los TextView
        tvNombre = findViewById(R.id.tvNombre);
        tvCedula = findViewById(R.id.tvCedula);
        tvImportancia = findViewById(R.id.tvImportancia);
        tvEstado = findViewById(R.id.tvEstado);
        tvEstadoHeader = findViewById(R.id.tvEstadoHeader);
        tvFechaCreacion = findViewById(R.id.tvFechaCreacion);
        tvProductos = findViewById(R.id.tvProductos);
        tvCargo = findViewById(R.id.tvCargo);
        tvNombreArea = findViewById(R.id.tvNombreArea);
        tvCantidad = findViewById(R.id.tvCantidad);

        // Obtener datos desde el Intent
        Intent intent = getIntent();

        String cedula = intent.getStringExtra("cedula");
        String importancia = intent.getStringExtra("importancia");
        String estado = intent.getStringExtra("estado");
        String fechaCreacion = intent.getStringExtra("fecha_creacion");
        String productos = intent.getStringExtra("productos");
        String cargo = intent.getStringExtra("cargo");
        String cantidad = intent.getStringExtra("cantidad");

        // Obtener nombre y área desde PrefsManager
        String nombreUsuario = prefsManager.getNombreUsuario();
        String nombreArea = prefsManager.getNombreArea();

        // Asignar valores
        tvNombre.setText(nombreUsuario != null ? nombreUsuario : "No disponible");
        tvNombreArea.setText(nombreArea != null ? nombreArea : "No disponible");
        tvCedula.setText(cedula != null ? cedula : "No disponible");
        tvImportancia.setText(importancia != null ? importancia : "No disponible");

        tvEstado.setText(estado != null ? estado : "Sin estado");
        tvEstadoHeader.setText(estado != null ? estado : "Sin estado");

        // ✅ Formatear la fecha
        tvFechaCreacion.setText(formatearFecha(fechaCreacion));

        tvProductos.setText(productos != null ? productos : "No disponible");
        tvCargo.setText(cargo != null ? cargo : "No disponible");
        tvCantidad.setText(cantidad != null ? cantidad : "0");

        // Botón Volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty() || fechaOriginal.equals("Sin fecha")) {
            return "Fecha no disponible";
        }

        try {
            SimpleDateFormat formatoEntrada;
            Date fecha;

            if (fechaOriginal.contains("T")) {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                // Si ya está formateada, devolverla tal cual
                return fechaOriginal;
            }

            fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            return fechaOriginal;
        }
    }
}

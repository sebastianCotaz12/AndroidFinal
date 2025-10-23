package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Detalles_listaChequeo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_lista_chequeo);

        // Referencias a los TextView del layout
        TextView tvUsuario = findViewById(R.id.tvUsuario);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvHora = findViewById(R.id.tvHora);
        TextView tvModelo = findViewById(R.id.tvModelo);
        TextView tvMarca = findViewById(R.id.tvMarca);
        TextView tvSoat = findViewById(R.id.tvSoat);
        TextView tvTecnico = findViewById(R.id.tvTecnico);
        TextView tvKilometraje = findViewById(R.id.tvKilometraje);
        TextView tvPlaca = findViewById(R.id.tvPlaca);
        TextView tvObservaciones = findViewById(R.id.tvObservaciones);

        // Botón volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> {
            finish();
        });

        // Obtener datos del intent
        Intent intent = getIntent();

        // Asignar valores con manejo de nulos
        tvUsuario.setText(obtenerValorSeguro(intent.getStringExtra("usuario"), "No especificado"));
        tvFecha.setText(formatearFecha(intent.getStringExtra("fecha")));
        tvHora.setText(formatearHora(intent.getStringExtra("hora"))); // 🔹 Hora formateada
        tvModelo.setText(obtenerValorSeguro(intent.getStringExtra("modelo"), "No especificado"));
        tvMarca.setText(obtenerValorSeguro(intent.getStringExtra("marca"), "No especificada"));
        tvSoat.setText(obtenerValorSeguro(intent.getStringExtra("soat"), "No especificado"));
        tvTecnico.setText(obtenerValorSeguro(intent.getStringExtra("tecnico"), "No asignado"));
        tvKilometraje.setText(obtenerValorSeguro(intent.getStringExtra("kilometraje"), "No registrado"));
        tvPlaca.setText(formatearPlaca(intent.getStringExtra("placa")));
        tvObservaciones.setText(obtenerValorSeguro(intent.getStringExtra("observaciones"), "Sin observaciones"));
    }

    private String obtenerValorSeguro(String valor, String valorPorDefecto) {
        if (valor == null || valor.isEmpty() || valor.equals("null")) {
            return valorPorDefecto;
        }
        return valor;
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty() || fechaOriginal.equals("null")) {
            return "Fecha no disponible";
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
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            return fechaOriginal;
        }
    }

    private String formatearHora(String horaOriginal) {
        if (horaOriginal == null || horaOriginal.isEmpty() || horaOriginal.equals("null")) {
            return "Hora no disponible";
        }

        try {
            // Intentar diferentes formatos de hora
            SimpleDateFormat formatoEntrada;
            Date hora;

            if (horaOriginal.contains("T")) {
                // Formato: 2024-01-15T10:30:00
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                hora = formatoEntrada.parse(horaOriginal);
            } else if (horaOriginal.contains(":")) {
                // Formato: 10:30:00 o 10:30
                if (horaOriginal.length() <= 5) {
                    formatoEntrada = new SimpleDateFormat("HH:mm", Locale.getDefault());
                } else {
                    formatoEntrada = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                }
                hora = formatoEntrada.parse(horaOriginal);
            } else {
                return horaOriginal; // Devolver original si no se puede parsear
            }

            SimpleDateFormat formatoSalida = new SimpleDateFormat("hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(hora);

        } catch (ParseException e) {
            return horaOriginal;
        }
    }

    private String formatearPlaca(String placa) {
        if (placa == null || placa.isEmpty() || placa.equals("null")) {
            return "Sin placa";
        }
        return placa.toUpperCase();
    }
}
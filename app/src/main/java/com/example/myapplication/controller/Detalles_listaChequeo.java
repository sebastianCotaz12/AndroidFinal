package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Detalles_listaChequeo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_lista_chequeo);

                TextView tvUsuario = findViewById(R.id.tvUsuario);
                TextView tvFecha = findViewById(R.id.tvFecha);
                TextView tvHora = findViewById(R.id.tvHora);
                TextView tvModelo = findViewById(R.id.tvModelo);
                TextView tvMarca = findViewById(R.id.tvMarca);
                TextView tvSoat = findViewById(R.id.tvSoat);
                TextView tvTecnico = findViewById(R.id.tvTecnico);
                TextView tvKilometraje = findViewById(R.id.tvKilometraje);

                // Obtener datos del intent
                Intent intent = getIntent();

                tvUsuario.setText(intent.getStringExtra("usuario"));
                tvFecha.setText(intent.getStringExtra("fecha"));
                tvHora.setText(intent.getStringExtra("hora"));
                tvModelo.setText(intent.getStringExtra("modelo"));
                tvMarca.setText(intent.getStringExtra("marca"));
                tvSoat.setText(intent.getStringExtra("soat"));
                tvTecnico.setText(intent.getStringExtra("tecnico"));
                tvKilometraje.setText(intent.getStringExtra("kilometraje"));





    }
}
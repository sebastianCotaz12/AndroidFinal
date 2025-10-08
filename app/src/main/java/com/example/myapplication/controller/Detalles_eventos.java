package com.example.myapplication.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

public class Detalles_eventos extends AppCompatActivity {

    TextView tvUsuario, tvTituloEvento, tvFechaEventos, tvDescripcionEventos;
    ImageView ivEvidencia;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_act_ludicas);

        tvUsuario = findViewById(R.id.tvUsuario);
        tvTituloEvento= findViewById(R.id.txtTituloEvento);
        tvFechaEventos = findViewById(R.id.tvFechaEvento);
        tvDescripcionEventos = findViewById(R.id.tvDescripcion);
        ivEvidencia = findViewById(R.id.ivEvidencia);

        tvUsuario.setText(getIntent().getStringExtra("usuario"));
        tvTituloEvento.setText(getIntent().getStringExtra("Titulo"));
        tvFechaEventos.setText(getIntent().getStringExtra("fecha"));
        tvDescripcionEventos.setText(getIntent().getStringExtra("descripcion"));

        String adjuntarEventos = getIntent().getStringExtra("adjuntarEventos");
        if (adjuntarEventos != null && !adjuntarEventos.isEmpty()) {
            Glide.with(this)
                    .load(adjuntarEventos)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivEvidencia);
        }
    }
}

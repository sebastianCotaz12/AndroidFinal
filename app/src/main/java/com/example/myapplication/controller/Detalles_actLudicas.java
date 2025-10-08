package com.example.myapplication.controller;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

public class Detalles_actLudicas extends AppCompatActivity {

    TextView tvUsuario, tvNombreActividad, tvFecha, tvDescripcion;
    ImageView ivEvidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_act_ludicas);

        tvUsuario = findViewById(R.id.tvUsuario);
        tvNombreActividad = findViewById(R.id.tvNombreActividad);
        tvFecha = findViewById(R.id.tvFecha);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        ivEvidencia = findViewById(R.id.ivEvidencia);

        tvUsuario.setText(getIntent().getStringExtra("usuario"));
        tvNombreActividad.setText(getIntent().getStringExtra("nombreActividad"));
        tvFecha.setText(getIntent().getStringExtra("fecha"));
        tvDescripcion.setText(getIntent().getStringExtra("descripcion"));

        String archivoAdjunto = getIntent().getStringExtra("archivoAdjunto");
        if (archivoAdjunto != null && !archivoAdjunto.isEmpty()) {
            Glide.with(this)
                    .load(archivoAdjunto)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivEvidencia);
        }
    }
}

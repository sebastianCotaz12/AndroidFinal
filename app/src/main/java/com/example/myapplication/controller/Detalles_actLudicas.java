package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Detalles_actLudicas extends AppCompatActivity {

    private TextView tvUsuario, tvNombreActividad, tvFecha, tvDescripcion;
    private ImageView ivEvidencia;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_act_ludicas);

        tvUsuario = findViewById(R.id.tvUsuario);
        tvNombreActividad = findViewById(R.id.tvNombreActividad);
        tvFecha = findViewById(R.id.tvFecha);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        ivEvidencia = findViewById(R.id.ivEvidencia);
        btnVolver = findViewById(R.id.btnVolver);

        Intent intent = getIntent();
        String usuario = intent.getStringExtra("usuario");
        String nombreActividad = intent.getStringExtra("nombreActividad");
        String fechaOriginal = intent.getStringExtra("fecha");
        String descripcion = intent.getStringExtra("descripcion");
        String imagenVideo = intent.getStringExtra("imagenVideo"); // ← aquí usamos imagenVideo

        tvUsuario.setText(usuario != null ? usuario : "Usuario no disponible");
        tvNombreActividad.setText(nombreActividad != null ? nombreActividad : "Sin nombre");
        tvFecha.setText(formatearFecha(fechaOriginal));
        tvDescripcion.setText(descripcion != null && !descripcion.isEmpty() ? descripcion : "Sin descripción disponible");

        // Cargar imagen desde Cloudinary
        cargarImagenActividad(imagenVideo);

        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarImagenActividad(String urlImagen) {
        if (urlImagen != null && !urlImagen.isEmpty() && !urlImagen.equals("null")) {
            Glide.with(this)
                    .load(urlImagen)
                    .transform(new CenterCrop(), new RoundedCorners(32))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(ivEvidencia);
        } else {
            ivEvidencia.setImageResource(R.drawable.placeholder_image);
        }
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) return "Fecha no disponible";

        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);
        } catch (ParseException e) {
            try {
                String soloFecha = fechaOriginal.split("T")[0];
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date fecha = formatoEntrada.parse(soloFecha);
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                return formatoSalida.format(fecha);
            } catch (Exception ex) {
                return fechaOriginal;
            }
        }
    }
}

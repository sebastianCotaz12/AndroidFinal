package com.example.myapplication.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Detalles_eventos extends AppCompatActivity {

    private ImageView imgEventoDetalle;
    private TextView tvTituloEventoDetalle, tvFechaEventoDetalle, tvUsuarioEventoDetalle, tvDescripcionEventoDetalle, tvTipoEvento;
    private Button btnAbrirArchivo, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);

        // Enlazar vistas
        imgEventoDetalle = findViewById(R.id.imgEventoDetalle);
        tvTituloEventoDetalle = findViewById(R.id.tvTituloEventoDetalle);
        tvFechaEventoDetalle = findViewById(R.id.tvFechaEventoDetalle);
        tvUsuarioEventoDetalle = findViewById(R.id.tvUsuarioEventoDetalle);
        tvDescripcionEventoDetalle = findViewById(R.id.tvDescripcionEventoDetalle);
        tvTipoEvento = findViewById(R.id.tvTipoEvento);
        btnAbrirArchivo = findViewById(R.id.btnAbrirArchivo);
        btnVolver = findViewById(R.id.btnVolver);

        // Obtener datos del Intent
        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        String fecha = intent.getStringExtra("fecha");
        String descripcion = intent.getStringExtra("descripcion");
        String imagen = intent.getStringExtra("imagen");
        String archivo = intent.getStringExtra("archivo");
        String nombreUsuario = intent.getStringExtra("nombre_usuario");

        // Asignar valores con formato mejorado
        tvTituloEventoDetalle.setText(titulo != null ? titulo : "Sin título");
        tvFechaEventoDetalle.setText(formatearFecha(fecha));
        tvUsuarioEventoDetalle.setText( (nombreUsuario != null ? nombreUsuario : "Usuario no disponible"));

        // Manejar descripción
        if (descripcion != null && !descripcion.isEmpty() && !descripcion.equals("null")) {
            tvDescripcionEventoDetalle.setText(descripcion);
        } else {
            tvDescripcionEventoDetalle.setText("No hay descripción disponible para este evento.");
        }

        // Configurar tipo de evento en el header
        configurarTipoEvento(titulo);

        // Cargar imagen con Glide mejorado
        cargarImagenEvento(imagen);

        // Configurar botón de archivo
        configurarBotonArchivo(archivo);

        // Botón Volver
        btnVolver.setOnClickListener(v -> {
            finish();
        });

        // Botón Archivo
        btnAbrirArchivo.setOnClickListener(v -> {
            abrirArchivo(archivo);
        });
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return "Fecha no disponible";
        }

        try {
            SimpleDateFormat formatoEntrada;
            Date fecha;

            if (fechaOriginal.contains("T") && fechaOriginal.contains(".000Z")) {
                // Formato: 2025-10-22T00:00:00.000Z
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            } else if (fechaOriginal.contains("T")) {
                // Formato: 2024-01-15T10:30:00
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                // Formato simple: 2024-01-15
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            }

            fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            // Si falla, intentar formato simple
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

    private void cargarImagenEvento(String imagenUrl) {
        if (imagenUrl != null && !imagenUrl.isEmpty() && !imagenUrl.equals("null")) {
            Glide.with(this)
                    .load(imagenUrl)
                    .transform(new CenterCrop(), new RoundedCorners(32))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imgEventoDetalle);
        } else {
            imgEventoDetalle.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void configurarBotonArchivo(String archivo) {
        if (archivo != null && !archivo.isEmpty() && !archivo.equals("null")) {
            btnAbrirArchivo.setVisibility(android.view.View.VISIBLE);
        } else {
            btnAbrirArchivo.setVisibility(android.view.View.GONE);
        }
    }

    private void configurarTipoEvento(String titulo) {
        if (titulo != null) {
            String tipo = determinarTipoEvento(titulo);
            tvTipoEvento.setText(tipo);
        }
    }

    private String determinarTipoEvento(String titulo) {
        if (titulo == null) return "General";

        String tituloLower = titulo.toLowerCase();
        if (tituloLower.contains("salida") || tituloLower.contains("campo") || tituloLower.contains("externa")) {
            return "Salida";
        } else if (tituloLower.contains("reunión") || tituloLower.contains("reunion") || tituloLower.contains("meeting")) {
            return "Reunión";
        } else if (tituloLower.contains("capacitación") || tituloLower.contains("capacitacion") || tituloLower.contains("training")) {
            return "Capacitación";
        } else if (tituloLower.contains("emergencia") || tituloLower.contains("incidente")) {
            return "Emergencia";
        } else if (tituloLower.contains("fiesta") || tituloLower.contains("celebración") || tituloLower.contains("celebraccion")) {
            return "Celebración";
        } else {
            return "General";
        }
    }

    private void abrirArchivo(String archivo) {
        if (archivo != null && !archivo.isEmpty() && !archivo.equals("null")) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(archivo));
                startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(this, "No se puede abrir el archivo", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay archivo disponible", Toast.LENGTH_SHORT).show();
        }
    }
}
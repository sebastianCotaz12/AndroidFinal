package com.example.myapplication.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
    private TextView tvNombreArchivo, tvTipoArchivo;
    private Button btnAbrirArchivo, btnVolver;
    private CardView cardArchivo;
    private ImageView ivIconoArchivo;

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

        // Nuevas vistas para el archivo
        cardArchivo = findViewById(R.id.cardArchivo);
        tvNombreArchivo = findViewById(R.id.tvNombreArchivo);
        tvTipoArchivo = findViewById(R.id.tvTipoArchivo);
        ivIconoArchivo = findViewById(R.id.ivIconoArchivo);

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
        tvUsuarioEventoDetalle.setText((nombreUsuario != null ? nombreUsuario : "Usuario no disponible"));

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

        // Configurar sección de archivo
        configurarSeccionArchivo(archivo);

        // Botón Volver
        btnVolver.setOnClickListener(v -> {
            finish();
        });

        // Botón Archivo
        btnAbrirArchivo.setOnClickListener(v -> {
            abrirArchivo(archivo);
        });

        // Card de archivo también clickeable
        cardArchivo.setOnClickListener(v -> {
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

    private void configurarSeccionArchivo(String archivo) {
        if (archivo != null && !archivo.isEmpty() && !archivo.equals("null")) {
            // Mostrar tarjeta de archivo
            cardArchivo.setVisibility(View.VISIBLE);

            // Obtener nombre y tipo del archivo desde la URL
            String nombreArchivo = obtenerNombreArchivoDesdeUrl(archivo);
            String tipoArchivo = obtenerTipoArchivo(nombreArchivo);

            // Configurar textos
            tvNombreArchivo.setText(nombreArchivo);
            tvTipoArchivo.setText(tipoArchivo);

            // Configurar ícono según el tipo de archivo
            configurarIconoArchivo(tipoArchivo);

        } else {
            // Ocultar tarjeta de archivo si no hay archivo
            cardArchivo.setVisibility(View.GONE);
        }
    }

    private String obtenerNombreArchivoDesdeUrl(String url) {
        if (url == null || url.isEmpty()) return "Archivo adjunto";

        try {
            // Extraer nombre del archivo de la URL
            Uri uri = Uri.parse(url);
            String path = uri.getPath();
            if (path != null && path.contains("/")) {
                return path.substring(path.lastIndexOf("/") + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Archivo adjunto";
    }

    private String obtenerTipoArchivo(String nombreArchivo) {
        if (nombreArchivo == null) return "Documento";

        String nombreLower = nombreArchivo.toLowerCase();
        if (nombreLower.endsWith(".pdf")) {
            return "PDF Document";
        } else if (nombreLower.endsWith(".doc") || nombreLower.endsWith(".docx")) {
            return "Word Document";
        } else if (nombreLower.endsWith(".txt")) {
            return "Text File";
        } else if (nombreLower.endsWith(".xls") || nombreLower.endsWith(".xlsx")) {
            return "Excel Spreadsheet";
        } else if (nombreLower.endsWith(".jpg") || nombreLower.endsWith(".jpeg") || nombreLower.endsWith(".png")) {
            return "Image File";
        } else {
            return "Document";
        }
    }

    private void configurarIconoArchivo(String tipoArchivo) {
        // Aquí puedes cambiar el ícono según el tipo de archivo
        // Por ahora mantenemos el ícono por defecto
        // Puedes agregar lógica para cambiar el ícono según el tipo
        ivIconoArchivo.setImageResource(R.drawable.ic_attach_file);
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
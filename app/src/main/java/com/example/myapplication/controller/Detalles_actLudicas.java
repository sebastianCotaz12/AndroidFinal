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

public class Detalles_actLudicas extends AppCompatActivity {

    private TextView tvUsuario, tvNombreActividad, tvFecha, tvDescripcion;
    private TextView tvNombreArchivo, tvTipoArchivo;
    private ImageView ivEvidencia, ivIconoArchivo;
    private Button btnVolver, btnAbrirArchivo;
    private CardView cardArchivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_act_ludicas);

        // Enlazar vistas existentes
        tvUsuario = findViewById(R.id.tvUsuario);
        tvNombreActividad = findViewById(R.id.tvNombreActividad);
        tvFecha = findViewById(R.id.tvFecha);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        ivEvidencia = findViewById(R.id.ivEvidencia);
        btnVolver = findViewById(R.id.btnVolver);

        // Nuevas vistas para el archivo
        cardArchivo = findViewById(R.id.cardArchivo);
        tvNombreArchivo = findViewById(R.id.tvNombreArchivo);
        tvTipoArchivo = findViewById(R.id.tvTipoArchivo);
        ivIconoArchivo = findViewById(R.id.ivIconoArchivo);
        btnAbrirArchivo = findViewById(R.id.btnAbrirArchivo);

        Intent intent = getIntent();
        String usuario = intent.getStringExtra("usuario");
        String nombreActividad = intent.getStringExtra("nombreActividad");
        String fechaOriginal = intent.getStringExtra("fecha");
        String descripcion = intent.getStringExtra("descripcion");
        String imagenVideo = intent.getStringExtra("imagenVideo");
        String archivoAdjunto = intent.getStringExtra("archivoAdjunto"); // Nuevo campo

        tvUsuario.setText(usuario != null ? usuario : "Usuario no disponible");
        tvNombreActividad.setText(nombreActividad != null ? nombreActividad : "Sin nombre");
        tvFecha.setText(formatearFecha(fechaOriginal));
        tvDescripcion.setText(descripcion != null && !descripcion.isEmpty() ? descripcion : "Sin descripción disponible");

        // Cargar imagen desde Cloudinary
        cargarImagenActividad(imagenVideo);

        // Configurar sección de archivo
        configurarSeccionArchivo(archivoAdjunto);

        btnVolver.setOnClickListener(v -> finish());

        // Botón para abrir archivo
        btnAbrirArchivo.setOnClickListener(v -> {
            abrirArchivo(archivoAdjunto);
        });

        // Card de archivo también clickeable
        cardArchivo.setOnClickListener(v -> {
            abrirArchivo(archivoAdjunto);
        });
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

    private void configurarSeccionArchivo(String archivoAdjunto) {
        if (archivoAdjunto != null && !archivoAdjunto.isEmpty() && !archivoAdjunto.equals("null")) {
            // Mostrar tarjeta de archivo
            cardArchivo.setVisibility(View.VISIBLE);

            // Obtener nombre y tipo del archivo desde la URL
            String nombreArchivo = obtenerNombreArchivoDesdeUrl(archivoAdjunto);
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
package com.example.myapplication.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Detalles_reportes extends AppCompatActivity {

    private CardView cardArchivo;
    private TextView tvNombreArchivo, tvTipoArchivo;
    private ImageView ivIconoArchivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_reportes);

        // Vistas existentes
        TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        TextView tvCargo = findViewById(R.id.tvCargo);
        TextView tvCedula = findViewById(R.id.tvCedula);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvLugar = findViewById(R.id.tvLugar);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        ImageView imgReporte = findViewById(R.id.imgReporte);
        TextView tvEstado = findViewById(R.id.tvEstado);

        // Nuevas vistas para el archivo
        cardArchivo = findViewById(R.id.cardArchivo);
        tvNombreArchivo = findViewById(R.id.tvNombreArchivo);
        tvTipoArchivo = findViewById(R.id.tvTipoArchivo);
        ivIconoArchivo = findViewById(R.id.ivIconoArchivo);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        Intent intent = getIntent();

        tvNombreUsuario.setText(formatearNombre(getExtra(intent, "nombre_usuario")));
        tvCargo.setText(getExtra(intent, "cargo"));
        tvCedula.setText(getExtra(intent, "cedula"));
        tvFecha.setText(formatearFecha(getExtra(intent, "fecha")));
        tvLugar.setText(getExtra(intent, "lugar"));
        tvDescripcion.setText(getExtra(intent, "descripcion"));
        tvEstado.setText(getExtra(intent, "estado"));

        String archivoUrl = getExtra(intent, "archivos");
        configurarArchivo(archivoUrl);
        cargarImagen(getExtra(intent, "imagen"), imgReporte);
    }

    private String getExtra(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        return value != null && !value.equals("null") ? value : "No disponible";
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty() || fechaOriginal.equals("No disponible")) {
            return "No disponible";
        }
        try {
            SimpleDateFormat formatoEntrada = fechaOriginal.contains("T")
                    ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'a las' hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);
        } catch (ParseException e) {
            return fechaOriginal;
        }
    }

    private String formatearNombre(String nombre) {
        if (nombre == null || nombre.equals("No disponible")) return "No disponible";
        if (nombre.startsWith("[") && nombre.endsWith("]")) nombre = nombre.substring(1, nombre.length() - 1);
        if (nombre.startsWith("\"") && nombre.endsWith("\"")) nombre = nombre.substring(1, nombre.length() - 1);
        return nombre.trim();
    }

    private void cargarImagen(String imagenUrl, ImageView imageView) {
        if (imagenUrl != null && !imagenUrl.isEmpty() && !imagenUrl.equals("No disponible") && !imagenUrl.equals("null")) {
            Glide.with(this)
                    .load(imagenUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void configurarArchivo(String archivoUrl) {
        if (archivoUrl == null || archivoUrl.isEmpty() || archivoUrl.equals("No disponible") || archivoUrl.equals("null")) {
            cardArchivo.setVisibility(View.GONE);
            return;
        }

        // Mostrar tarjeta de archivo
        cardArchivo.setVisibility(View.VISIBLE);

        // Extraer nombre del archivo de la URL
        String nombreArchivo = obtenerNombreArchivoDesdeUrl(archivoUrl);
        String tipoArchivo = obtenerTipoArchivo(nombreArchivo);

        // Configurar textos
        tvNombreArchivo.setText(nombreArchivo);
        tvTipoArchivo.setText(tipoArchivo);

        // Configurar ícono según el tipo de archivo
        configurarIconoArchivo(tipoArchivo);

        // Hacer la tarjeta clickeable
        cardArchivo.setOnClickListener(v -> abrirArchivo(archivoUrl));

        // Botón también clickeable
        findViewById(R.id.btnAbrirArchivo).setOnClickListener(v -> abrirArchivo(archivoUrl));
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
        } else if (nombreLower.endsWith(".ppt") || nombreLower.endsWith(".pptx")) {
            return "PowerPoint Presentation";
        } else if (nombreLower.endsWith(".zip") || nombreLower.endsWith(".rar")) {
            return "Compressed File";
        } else {
            return "Document";
        }
    }

    private void configurarIconoArchivo(String tipoArchivo) {
        // Por ahora mantenemos el ícono por defecto
        // Puedes agregar lógica para cambiar el ícono según el tipo
        ivIconoArchivo.setImageResource(R.drawable.ic_attach_file);
    }

    private void abrirArchivo(String archivoUrl) {
        if (archivoUrl != null && !archivoUrl.isEmpty() && !archivoUrl.equals("No disponible") && !archivoUrl.equals("null")) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(archivoUrl));
                startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(this, "No se puede abrir el archivo", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay archivo disponible", Toast.LENGTH_SHORT).show();
        }
    }
}
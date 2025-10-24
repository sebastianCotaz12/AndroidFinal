package com.example.myapplication.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Detalles_reportes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_reportes);

        TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        TextView tvCargo = findViewById(R.id.tvCargo);
        TextView tvCedula = findViewById(R.id.tvCedula);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvLugar = findViewById(R.id.tvLugar);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        ImageView imgReporte = findViewById(R.id.imgReporte);
        TextView tvArchivos = findViewById(R.id.tvArchivos);
        TextView tvEstado = findViewById(R.id.tvEstado);

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
        mostrarArchivo(archivoUrl, tvArchivos);
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

    private void mostrarArchivo(String archivoUrl, TextView tvArchivo) {
        if (archivoUrl == null || archivoUrl.isEmpty() || archivoUrl.equals("No disponible") || archivoUrl.equals("null")) {
            tvArchivo.setText("No hay archivo adjunto");
            tvArchivo.setOnClickListener(null);
            return;
        }

        // Extraer nombre del archivo de la URL
        String[] partes = archivoUrl.split("/");
        String nombreArchivo = partes[partes.length - 1];

        // Obtener extensión y tipo
        String tipoArchivo = "Archivo";
        if (nombreArchivo.contains(".")) {
            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase();
            switch (extension) {
                case "pdf": tipoArchivo = "PDF"; break;
                case "doc": case "docx": tipoArchivo = "Word"; break;
                case "xls": case "xlsx": tipoArchivo = "Excel"; break;
                case "ppt": case "pptx": tipoArchivo = "PowerPoint"; break;
                case "jpg": case "jpeg": case "png": case "gif": tipoArchivo = "Imagen"; break;
                case "txt": tipoArchivo = "Texto"; break;
                case "zip": case "rar": tipoArchivo = "Archivo Comprimido"; break;
                default: tipoArchivo = extension.toUpperCase(); break;
            }
        }

        // Mostrar nombre y tipo
        tvArchivo.setText(nombreArchivo + " (" + tipoArchivo + ")");

        // Abrir archivo al hacer clic
        tvArchivo.setOnClickListener(v -> {
            try {
                Intent abrirArchivo = new Intent(Intent.ACTION_VIEW);
                abrirArchivo.setData(Uri.parse(archivoUrl));
                abrirArchivo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(abrirArchivo);
            } catch (Exception e) {
                Toast.makeText(this, "No se puede abrir el archivo", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

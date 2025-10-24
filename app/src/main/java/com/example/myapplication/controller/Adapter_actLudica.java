package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter_actLudica extends RecyclerView.Adapter<Adapter_actLudica.ViewHolder> {

    private final Context context;
    private final List<Item_actLudicas> lista;

    public Adapter_actLudica(Context context, List<Item_actLudicas> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_act_ludicas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item_actLudicas item = lista.get(position);

        // Nombre y fecha
        holder.txtNombre.setText(item.getNombreActividad());
        holder.txtFecha.setText(formatearFecha(item.getFechaActividad()));

        // Cargar imagen desde Cloudinary o placeholder
        String urlImagen = item.getImagenVideo();
        if (urlImagen != null && !urlImagen.isEmpty() && !urlImagen.equals("null")) {
            Glide.with(context)
                    .load(urlImagen)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .placeholder(R.drawable.ludicas)
                    .error(R.drawable.ludicas)
                    .into(holder.imgMiniatura);
        } else {
            holder.imgMiniatura.setImageResource(R.drawable.ludicas);
        }

        // Botón Detalles
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_actLudicas.class);
            intent.putExtra("usuario", item.getNombreUsuario());
            intent.putExtra("nombreActividad", item.getNombreActividad());
            intent.putExtra("fecha", item.getFechaActividad());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("archivoAdjunto", item.getArchivoAdjunto());
            intent.putExtra("imagenVideo", item.getImagenVideo());
            context.startActivity(intent);
        });

        // Botón Descargar/abrir archivo
        holder.btnDownload.setOnClickListener(v -> {
            String archivo = item.getArchivoAdjunto();
            if (archivo != null && !archivo.isEmpty()) {
                try {
                    Intent abrir = new Intent(Intent.ACTION_VIEW, Uri.parse(archivo));
                    context.startActivity(abrir);
                } catch (Exception e) {
                    Toast.makeText(context, "⚠️ No se puede abrir el archivo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No hay archivo disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Formateo de fecha
    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) return "Fecha no disponible";
        try {
            SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date fecha = entrada.parse(fechaOriginal);
            SimpleDateFormat salida = new SimpleDateFormat("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return salida.format(fecha);
        } catch (ParseException e) {
            try {
                String soloFecha = fechaOriginal.split("T")[0];
                SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date fecha = entrada.parse(soloFecha);
                SimpleDateFormat salida = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
                return salida.format(fecha);
            } catch (Exception ex) {
                return fechaOriginal;
            }
        }
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMiniatura;
        TextView txtNombre, txtFecha;
        MaterialButton btnDetalles;
        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMiniatura = itemView.findViewById(R.id.imgPoster);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}

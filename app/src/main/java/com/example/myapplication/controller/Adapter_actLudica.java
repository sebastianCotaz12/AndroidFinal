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

        // Configurar datos básicos
        holder.txtNombre.setText(item.getNombreActividad());

        // Formatear fecha
        String fechaFormateada = formatearFecha(item.getFechaActividad());
        holder.txtFecha.setText(fechaFormateada);

        // Cargar imagen con Glide mejorado
        cargarImagenActividad(holder, item.getArchivoAdjunto());

        // Botón Detalles
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_actLudicas.class);
            intent.putExtra("usuario", item.getNombreUsuario());
            intent.putExtra("nombreActividad", item.getNombreActividad());
            intent.putExtra("fecha", item.getFechaActividad());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("archivoAdjunto", item.getArchivoAdjunto());
            context.startActivity(intent);
        });

        // Botón Descargar
        holder.btnDownload.setOnClickListener(v -> {
            if (item.getArchivoAdjunto() != null && !item.getArchivoAdjunto().isEmpty()) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getArchivoAdjunto()));
                    context.startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "No se puede abrir el archivo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No hay archivo disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return "Fecha no disponible";
        }

        try {
            // Para formato: 2025-10-22T00:00:00.000Z
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            // Si falla, intentar con formato simple
            try {
                String soloFecha = fechaOriginal.split("T")[0];
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date fecha = formatoEntrada.parse(soloFecha);
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
                return formatoSalida.format(fecha);
            } catch (Exception ex) {
                return "Fecha inválida";
            }
        }
    }

    private void cargarImagenActividad(ViewHolder holder, String imagenUrl) {
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(context)
                    .load(imagenUrl)
                    .transform(new CenterCrop(), new RoundedCorners(16))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgMiniatura);
        } else {
            Glide.with(context)
                    .load(R.mipmap.ic_launcher)
                    .transform(new CenterCrop(), new RoundedCorners(16))
                    .into(holder.imgMiniatura);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

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
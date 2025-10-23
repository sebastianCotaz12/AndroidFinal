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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplication.R;
import com.example.myapplication.controller.Item_eventos;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter_eventos extends RecyclerView.Adapter<Adapter_eventos.ViewHolder> {

    private final Context context;
    private final List<Item_eventos> lista;

    public Adapter_eventos(Context context, List<Item_eventos> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_eventos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item_eventos item = lista.get(position);

        // Configurar datos básicos
        holder.txtTituloEvento.setText(item.getTitulo());
        holder.txtNombreUsuario.setText(item.getNombreUsuario());

        // Formatear fecha
        String fechaFormateada = formatearFecha(item.getFechaActividad());
        holder.txtFechaEvento.setText(fechaFormateada);

        // Configurar tipo de evento en el chip
        configurarTipoEvento(holder, item.getTitulo());

        // Cargar imagen con Glide mejorado
        cargarImagenEvento(holder, item.getImagen());

        // Botón Detalles
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_eventos.class);
            intent.putExtra("titulo", item.getTitulo());
            intent.putExtra("fecha", item.getFechaActividad());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("imagen", item.getImagen());
            intent.putExtra("archivo", item.getArchivo());
            intent.putExtra("nombre_usuario", item.getNombreUsuario());
            context.startActivity(intent);
        });

        // Botón Descargar
        holder.btnDownload.setOnClickListener(v -> {
            if (item.getArchivo() != null && !item.getArchivo().isEmpty()) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getArchivo()));
                    context.startActivity(browserIntent);
                } catch (Exception e) {
                    android.widget.Toast.makeText(context, "No se puede abrir el archivo", android.widget.Toast.LENGTH_SHORT).show();
                }
            } else {
                android.widget.Toast.makeText(context, "No hay archivo disponible", android.widget.Toast.LENGTH_SHORT).show();
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

    private void configurarTipoEvento(ViewHolder holder, String titulo) {
        if (titulo == null) {
            holder.chipTipoEvento.setText("Evento");
            holder.chipTipoEvento.setChipBackgroundColorResource(R.color.evento_default);
            return;
        }

        String tipo = "Evento";
        int colorRes = R.color.evento_default;

        String tituloLower = titulo.toLowerCase();
        if (tituloLower.contains("salida") || tituloLower.contains("campo") || tituloLower.contains("externa")) {
            tipo = "Salida";
            colorRes = R.color.evento_salida;
        } else if (tituloLower.contains("reunión") || tituloLower.contains("reunion") || tituloLower.contains("meeting")) {
            tipo = "Reunión";
            colorRes = R.color.evento_reunion;
        } else if (tituloLower.contains("capacitación") || tituloLower.contains("capacitacion") || tituloLower.contains("training")) {
            tipo = "Capacitación";
            colorRes = R.color.evento_capacitacion;
        } else if (tituloLower.contains("emergencia") || tituloLower.contains("incidente")) {
            tipo = "Emergencia";
            colorRes = R.color.evento_emergencia;
        }

        holder.chipTipoEvento.setText(tipo);
        holder.chipTipoEvento.setChipBackgroundColorResource(colorRes);
    }

    private void cargarImagenEvento(ViewHolder holder, String imagenUrl) {
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(context)
                    .load(imagenUrl)
                    .transform(new CenterCrop(), new RoundedCorners(16))
                    .placeholder(R.drawable.placeholder_event)
                    .error(R.drawable.placeholder_event)
                    .into(holder.imgMiniatura);
        } else {
            Glide.with(context)
                    .load(R.drawable.placeholder_event)
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
        TextView txtTituloEvento, txtFechaEvento, txtNombreUsuario;
        MaterialButton btnDetalles, btnCompartir;
        ImageButton btnDownload;
        Chip chipTipoEvento;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMiniatura = itemView.findViewById(R.id.imgPoster);
            txtTituloEvento = itemView.findViewById(R.id.txtTituloEvento);
            txtFechaEvento = itemView.findViewById(R.id.txtFechaEvento);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            chipTipoEvento = itemView.findViewById(R.id.chipTipoEvento);
        }
    }
}
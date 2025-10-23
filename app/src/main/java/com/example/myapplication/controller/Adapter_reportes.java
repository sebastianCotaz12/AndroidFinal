package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.controller.ItemReporte;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter_reportes extends RecyclerView.Adapter<Adapter_reportes.ViewHolder> {

    private final Context context;
    private final List<ItemReporte> lista;

    public Adapter_reportes(Context context, List<ItemReporte> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_item_reportes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemReporte item = lista.get(position);

        String nombreTexto = item.getNombreUsuario() != null ? item.getNombreUsuario() : "Sin nombre";
        String lugarTexto = item.getLugar() != null ? item.getLugar() : "Sin lugar";
        holder.txtNombre.setText(nombreTexto + " - " + lugarTexto);

        String fechaFormateada = formatearFecha(item.getFecha());
        holder.txtFecha.setText(fechaFormateada);

        configurarEstado(holder, item.getEstado());

        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_reportes.class);

            intent.putExtra("nombre_usuario", item.getNombreUsuario());
            intent.putExtra("cargo", item.getCargo());
            intent.putExtra("cedula", item.getCedula());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("lugar", item.getLugar());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("imagen", item.getImagen());
            intent.putExtra("archivos", item.getArchivos());
            intent.putExtra("estado", item.getEstado());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.btnDownload.setOnClickListener(v -> {
            // Aquí implementarás la descarga de archivos
        });
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return "Fecha no disponible";
        }

        try {
            SimpleDateFormat formatoEntrada;
            Date fecha;

            if (fechaOriginal.contains("T")) {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            }

            fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            return fechaOriginal;
        }
    }

    private void configurarEstado(ViewHolder holder, String estado) {
        if (estado == null) {
            holder.txtEstado.setText("Estado: No definido");
            holder.indicadorEstado.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            return;
        }

        String textoEstado = "Estado: " + estado;
        holder.txtEstado.setText(textoEstado);

        int colorEstado;
        switch (estado.toLowerCase()) {
            case "realizado":
            case "completado":
            case "finalizado":
                colorEstado = ContextCompat.getColor(context, R.color.estado_realizado);
                break;
            case "en proceso":
            case "en progreso":
                colorEstado = ContextCompat.getColor(context, R.color.estado_proceso);
                break;
            case "pendiente":
                colorEstado = ContextCompat.getColor(context, R.color.estado_pendiente);
                break;
            default:
                colorEstado = ContextCompat.getColor(context, android.R.color.darker_gray);
                break;
        }

        holder.indicadorEstado.setBackgroundColor(colorEstado);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha, txtEstado;
        MaterialButton btnDetalles;
        ImageButton btnDownload;
        View indicadorEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            indicadorEstado = itemView.findViewById(R.id.indicadorEstado);
        }
    }
}
package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class Adapter_reportes extends RecyclerView.Adapter<Adapter_reportes.ViewHolder> {

    private final Context context;
    private final List<ItemReporte> lista;

    // 🔹 Constructor corregido
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

        // Mostrar nombre + fecha
        holder.txtNombreFecha.setText(item.getNombreUsuario() + " - " + item.getFecha());

        // Botón detalles
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_reportes.class);

            // Pasamos los datos al intent
            intent.putExtra("id", item.getId());
            intent.putExtra("nombre_usuario", item.getNombreUsuario());
            intent.putExtra("cargo", item.getCargo());
            intent.putExtra("cedula", item.getCedula());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("lugar", item.getLugar());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("imagen", item.getImagen());
            intent.putExtra("archivos", item.getArchivos());
            intent.putExtra("estado", item.getEstado());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // necesario si no viene de una Activity
            context.startActivity(intent);
        });

        // Botón de descarga
        holder.btnDownload.setOnClickListener(v -> {
            // Aquí implementarás la descarga de archivos
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreFecha;
        Button btnDetalles;
        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreFecha = itemView.findViewById(R.id.txtNombre);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}

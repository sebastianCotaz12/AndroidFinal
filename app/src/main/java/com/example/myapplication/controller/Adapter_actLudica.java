package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.List;

public class Adapter_actLudica extends RecyclerView.Adapter<Adapter_actLudica.ViewHolder> {

    private Context context;
    private List<Item_actLudicas> lista;

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

        holder.txtNombre.setText(item.getNombreActividad());
        holder.txtFecha.setText(item.getFechaActividad());

        // Cargar miniatura (si hay archivoAdjunto, sino placeholder)
        if (item.getArchivoAdjunto() != null && !item.getArchivoAdjunto().isEmpty()) {
            Glide.with(context)
                    .load(item.getArchivoAdjunto())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgMiniatura);
        } else {
            holder.imgMiniatura.setImageResource(R.mipmap.ic_launcher);
        }

        // Botón Detalles → abrir Detalles_actLudicas
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_actLudicas.class);
            intent.putExtra("usuario", item.getNombreUsuario());
            intent.putExtra("nombreActividad", item.getNombreActividad());
            intent.putExtra("fecha", item.getFechaActividad());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("archivoAdjunto", item.getArchivoAdjunto());
            context.startActivity(intent);
        });

        // Botón Download → abrir archivo adjunto en navegador
        holder.btnDownload.setOnClickListener(v -> {
            if (item.getArchivoAdjunto() != null && !item.getArchivoAdjunto().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getArchivoAdjunto()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgMiniatura;
        TextView txtNombre, txtFecha;
        Button btnDetalles;
        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMiniatura = itemView.findViewById(R.id.imgMiniatura);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}

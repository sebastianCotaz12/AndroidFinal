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

public class Adapter_eventos extends RecyclerView.Adapter<Adapter_eventos.ViewHolder> {

    private Context context;
    private List<Item_eventos> lista;

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

        holder.txtTituloEvento.setText(item.getTituloEvento());
        holder.txtFechaEvento.setText(item.getFechaEvento());

        // Cargar miniatura (si hay Adjuntar, sino placeholder)
        if (item.getAdjuntarEvento() != null && !item.getAdjuntarEvento().isEmpty()) {
            Glide.with(context)
                    .load(item.getAdjuntarEvento())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgMiniatura);
        } else {
            holder.imgMiniatura.setImageResource(R.mipmap.ic_launcher);
        }

        // Botón Detalles → abrir Detalles_actLudicas
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_actLudicas.class);
            intent.putExtra("tituloEventos", item.getTituloEvento());
            intent.putExtra("fecha", item.getFechaEvento());
            intent.putExtra("descripcion", item.getDescripcionEvento());
            intent.putExtra("adjuntar", item.getAdjuntarEvento());
            context.startActivity(intent);
        });

        // Botón Download → abrir archivo adjunto en navegador
        holder.btnDownload.setOnClickListener(v -> {
            if (item.getAdjuntarEvento() != null && !item.getAdjuntarEvento().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getAdjuntarEvento()));
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
        TextView txtTituloEvento, txtFechaEvento;
        Button btnDetalles;
        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMiniatura = itemView.findViewById(R.id.imgMiniatura);
            txtTituloEvento = itemView.findViewById(R.id.txtTituloEvento);
            txtFechaEvento = itemView.findViewById(R.id.txtFechaEvento);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}

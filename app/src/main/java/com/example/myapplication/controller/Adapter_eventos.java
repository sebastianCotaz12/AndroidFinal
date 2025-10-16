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

        // Mostrar datos del evento
        holder.txtTituloEvento.setText(item.getTitulo());
        holder.txtFechaEvento.setText(item.getFechaActividad());
        holder.txtNombreUsuario.setText(item.getNombreUsuario());

        // Cargar imagen del evento (campo "imagen" del modelo)
        if (item.getImagen() != null && !item.getImagen().isEmpty()) {
            Glide.with(context)
                    .load(item.getImagen())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.imgMiniatura);
        } else {
            holder.imgMiniatura.setImageResource(R.drawable.placeholder_image);
        }

        // Botón Detalles → abrir Detalles_eventos
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

        // Botón Descargar → abrir archivo adjunto en navegador
        holder.btnDownload.setOnClickListener(v -> {
            if (item.getArchivo() != null && !item.getArchivo().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getArchivo()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // -----------------------------------------------
    // ViewHolder
    // -----------------------------------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgMiniatura;
        TextView txtTituloEvento, txtFechaEvento, txtNombreUsuario;
        Button btnDetalles;
        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMiniatura = itemView.findViewById(R.id.imgPoster);
            txtTituloEvento = itemView.findViewById(R.id.txtTituloEvento);
            txtFechaEvento = itemView.findViewById(R.id.txtFechaEvento);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}

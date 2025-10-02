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

public class Adapter_eventos extends RecyclerView.Adapter<Adapter_eventos.ViewHolder> {

    private Context context;
    private List<Item_eventos> listaEventos;

    public Adapter_eventos(Context context, List<Item_eventos> listaEventos) {
        this.context = context;
        this.listaEventos = listaEventos;
    }

    @NonNull
    @Override
    public Adapter_eventos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_eventos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item_eventos item = listaEventos.get(position);
        holder.txtTitulo.setText(item.getTitulo());
        holder.txtFecha.setText(item.getFecha());
        holder.txtDescripcion.setText(item.getDescripcion());

        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_eventos.class);


            intent.putExtra("titulo", item.getTitulo());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("imagen", item.getImagen());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtFecha, txtDescripcion;

        Button btnDetalles;

        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.etTituloEvento);
            txtFecha = itemView.findViewById(R.id.etFechaEvento);
            txtDescripcion = itemView.findViewById(R.id.etDescripcionEvento);

            btnDetalles= itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);

        }
    }
}

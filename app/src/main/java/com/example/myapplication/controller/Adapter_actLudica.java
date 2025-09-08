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
        holder.txtNombreFecha.setText(item.getNombreActividad() + " - " + item.getFecha());

        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_actLudicas.class);

            // Pasamos TODOS los datos que existen en el modelo
            intent.putExtra("id", item.getId());
            intent.putExtra("usuario", item.getUsuario());
            intent.putExtra("nombreActividad", item.getNombreActividad());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("imagenVideo", item.getImagenVideo());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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
package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class Adapter_gestionEpp extends RecyclerView.Adapter<Adapter_gestionEpp.ViewHolder> {

    private final Context context;
    private final List<Item_gestionEpp> lista;

    public Adapter_gestionEpp(Context context, List<Item_gestionEpp> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_lista_chequeo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item_gestionEpp item = lista.get(position);

        // Texto visible en la lista
        holder.txtNombre.setText("Cédula: " + item.getCedula() +
                " | Estado: " + item.getEstado() +
                " | Fecha: " + item.getFecha_creacion());

        // Evento botón detalles
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_gestionEPP.class);
            intent.putExtra("id", item.getId());
            intent.putExtra("cedula", item.getCedula());
            intent.putExtra("importancia", item.getImportancia());
            intent.putExtra("estado", item.getEstado());
            intent.putExtra("fecha_creacion", item.getFecha_creacion());
            intent.putExtra("productos", item.getProductos());
            intent.putExtra("cargo", item.getCargo());
            intent.putExtra("area", item.getArea());
            intent.putExtra("cantidad", item.getCantidad());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        Button btnDetalles;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
        }
    }
}

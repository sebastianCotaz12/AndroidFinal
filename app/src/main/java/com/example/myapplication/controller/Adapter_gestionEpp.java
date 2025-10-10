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
import com.example.myapplication.utils.PrefsManager;

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

        holder.txtNombre.setText("Cédula: " + item.getCedula() +
                " | Estado: " + item.getEstado() +
                " | Fecha: " + item.getFecha_creacion());

        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_gestionEPP.class);

            intent.putExtra("id", String.valueOf(item.getId()));
            intent.putExtra("cedula", item.getCedula() != null ? item.getCedula() : "No disponible");
            intent.putExtra("importancia", item.getImportancia() != null ? item.getImportancia() : "No disponible");
            intent.putExtra("estado", item.getEstado() != null ? item.getEstado() : "No disponible");
            intent.putExtra("fecha_creacion", item.getFecha_creacion() != null ? item.getFecha_creacion() : "Sin fecha");
            intent.putExtra("productos", item.getProductos() != null ? item.getProductos() : "Sin productos");
            intent.putExtra("cargo", item.getCargo() != null ? item.getCargo() : "No disponible");
            intent.putExtra("area", item.getArea() != null ? item.getArea() : "No disponible");
            intent.putExtra("cantidad", String.valueOf(item.getCantidad()));

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

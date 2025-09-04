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

public class adapter_gestionEpp extends RecyclerView.Adapter<adapter_gestionEpp.ViewHolder> {

    private final Context context;
    private final List<item_gestionEpp> lista;

    public adapter_gestionEpp(Context context, List<item_gestionEpp> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_gestion_epp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        item_gestionEpp item = lista.get(position);

        // Mostrar en la lista el nombre + apellido
        holder.txtNombre.setText(item.getNombre() + " " + item.getApellido());

        // Acción al presionar "Detalles"
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, detalles_gestionEPP.class);

            intent.putExtra("nombre", item.getNombre());
            intent.putExtra("apellido", item.getApellido());
            intent.putExtra("cedula", item.getCedula());
            intent.putExtra("cargo", item.getCargo());
            intent.putExtra("productos", item.getProductos());
            intent.putExtra("cantidad", item.getCantidad());
            intent.putExtra("importancia", item.getImportancia());
            intent.putExtra("estado", item.getEstado());
            intent.putExtra("fecha_creacion", item.getFechaCreacion());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // necesario si el context no es una activity
            context.startActivity(intent);
        });

        // Acción al presionar "Descargar"
        holder.btnDownload.setOnClickListener(v -> {
            // Aquí puedes implementar exportar datos a PDF, Excel o similar
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        Button btnDetalles;
        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}

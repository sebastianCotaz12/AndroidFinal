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

public class adapter_listaChequeo extends RecyclerView.Adapter<adapter_listaChequeo.ViewHolder> {

    private final Context context;
    private final List<item_listaChequeo> lista;

    public adapter_listaChequeo(Context context, List<item_listaChequeo> lista) {
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
        item_listaChequeo item = lista.get(position);

        holder.txtNombre.setText(item.getNombre() + " - " + item.getFecha());

        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, detalles_listaChequeo.class);
            intent.putExtra("usuario", item.getNombre());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("hora", item.getHora());
            intent.putExtra("modelo", item.getModelo());
            intent.putExtra("marca", item.getMarca());
            intent.putExtra("soat", item.getSoat());
            intent.putExtra("tecnico", item.getTecnico());
            intent.putExtra("kilometraje", item.getKilometraje());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // necesario si el context no es una activity
            context.startActivity(intent);
        });

        holder.btnDownload.setOnClickListener(v -> {
            // Implementa aquí la funcionalidad de descarga si deseas
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

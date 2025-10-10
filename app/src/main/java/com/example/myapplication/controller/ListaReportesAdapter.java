package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ListaReportesAdapter extends RecyclerView.Adapter<ListaReportesAdapter.ViewHolder> {

    private final Context context;
    private final List<ItemReporte> listaReportes;

    public ListaReportesAdapter(Context context, List<ItemReporte> listaReportes) {
        this.context = context;
        this.listaReportes = listaReportes;
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
        ItemReporte reporte = listaReportes.get(position);

        holder.txtNombre.setText(
                reporte.getNombreUsuario() != null
                        ? reporte.getNombreUsuario()
                        : ""
        );
        holder.txtFecha.setText(
                reporte.getFecha() != null
                        ? reporte.getFecha()
                        : ""
        );

        // Click para ver detalles (solo enviamos el ID)
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_reportes.class);
            intent.putExtra("reporte_id", reporte.getIdReporte());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // Botón Descargar archivo
        holder.btnDownload.setOnClickListener(v -> {
            String url = reporte.getArchivos();
            if (url != null && !url.trim().isEmpty()) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(
                            context,
                            "No se pudo abrir el archivo.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            } else {
                Toast.makeText(
                        context,
                        "No hay archivo disponible para descargar.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaReportes != null ? listaReportes.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha;
        Button btnDetalles;
        ImageButton btnDownload;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre   = itemView.findViewById(R.id.txtNombre);
            txtFecha    = itemView.findViewById(R.id.txtFecha);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}
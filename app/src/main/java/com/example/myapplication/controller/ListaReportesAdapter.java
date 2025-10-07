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
        // Asegúrate de usar el nombre del layout que realmente tienes.
        // En tu código usabas "activity_item_reportes" — lo mantenemos.
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_reportes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemReporte reporte = listaReportes.get(position);

        // Rellenar vistas (si un campo es null, ponemos cadena vacía para evitar NPE)
        holder.txtNombre.setText(reporte.getNombreUsuario() != null ? reporte.getNombreUsuario() : "");
        holder.txtFecha.setText(reporte.getFecha() != null ? reporte.getFecha() : "");

        // Detalles -> abre Detalles_reportes (asegúrate que esa Activity exista)
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_reportes.class);
            intent.putExtra("id", reporte.getId());
            intent.putExtra("nombre_usuario", reporte.getNombreUsuario());
            intent.putExtra("cargo", reporte.getCargo());
            intent.putExtra("cedula", reporte.getCedula());
            intent.putExtra("fecha", reporte.getFecha());
            intent.putExtra("lugar", reporte.getLugar());
            intent.putExtra("descripcion", reporte.getDescripcion());
            intent.putExtra("imagen", reporte.getImagen());
            intent.putExtra("archivos", reporte.getArchivos());
            intent.putExtra("estado", reporte.getEstado());
            // Si llamas desde un Context que no es Activity, esto evita crash
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // Descargar -> abrir URL si existe
        holder.btnDownload.setOnClickListener(v -> {
            String url = reporte.getArchivos();
            if (url != null && !url.trim().isEmpty()) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(context, "No se pudo abrir el archivo.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No hay archivo disponible para descargar.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaReportes != null ? listaReportes.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        TextView txtFecha;
        Button btnDetalles;
        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Los IDs deben coincidir exactamente con los del XML que pegaste
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}

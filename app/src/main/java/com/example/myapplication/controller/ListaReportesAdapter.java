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
import com.example.myapplication.controller.ItemReporte;

import java.util.List;

public class ListaReportesAdapter extends RecyclerView.Adapter<ListaReportesAdapter.ViewHolder> {

    private Context context;
    private List<ItemReporte> listaReportes;

    public ListaReportesAdapter(Context context, List<ItemReporte> listaReportes) {
        this.context = context;
        this.listaReportes = listaReportes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_reportes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemReporte reporte = listaReportes.get(position);

        // Asignar datos a las vistas del item_reporte.xml
        holder.tvNombreUsuario.setText(reporte.getNombreUsuario());
        holder.tvCargo.setText(reporte.getCargo());
        holder.tvFecha.setText(reporte.getFecha());
        holder.tvEstado.setText(reporte.getEstado());

        // Botón Detalles
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_reportes.class);
            intent.putExtra("nombre_usuario", reporte.getNombreUsuario());
            intent.putExtra("cargo", reporte.getCargo());
            intent.putExtra("cedula", reporte.getCedula());
            intent.putExtra("fecha", reporte.getFecha());
            intent.putExtra("lugar", reporte.getLugar());
            intent.putExtra("descripcion", reporte.getDescripcion());
            intent.putExtra("imagen", reporte.getImagen());
            intent.putExtra("archivos", reporte.getArchivos());
            intent.putExtra("estado", reporte.getEstado());
            context.startActivity(intent);
        });

        // Botón Descargar (ejemplo)
        holder.btnDescargar.setOnClickListener(v -> {
            // Aquí pondrías tu lógica real de descarga
            // De momento solo mostramos un mensaje
            android.widget.Toast.makeText(context,
                    "Descargando: " + reporte.getArchivos(),
                    android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuario, tvCargo, tvFecha, tvEstado;
        Button btnDetalles, btnDescargar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreUsuario = itemView.findViewById(R.id.etNombre);
            tvCargo = itemView.findViewById(R.id.tvCargo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);

        }
    }
}

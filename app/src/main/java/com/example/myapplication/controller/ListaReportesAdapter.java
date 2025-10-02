package com.example.myapplication.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ItemReporte;
import com.example.myapplication.R;

import java.util.List;

public class ListaReportesAdapter extends RecyclerView.Adapter<ListaReportesAdapter.ReporteViewHolder> {

    private Context context;
    private List<ItemReporte> listaReportes;
    private OnItemClickListener listener;

    // Interfaz para manejar clicks
    public interface OnItemClickListener {
        void onDetallesClick();
        void onDownloadClick(ItemReporte reporte);
    }

    public ListaReportesAdapter(Context context, List<ItemReporte> listaReportes, OnItemClickListener listener) {
        this.context = context;
        this.listaReportes = listaReportes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_reportes, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        ItemReporte reporte = listaReportes.get(position);
        holder.txtNombre.setText(reporte.getNombreUsuario());
        holder.txtFecha.setText(reporte.getFecha());

        // Click en botón Detalles
        holder.btnDetalles.setOnClickListener(v -> listener.onDetallesClick());

        // Click en botón Descargar
        holder.btnDownload.setOnClickListener(v -> listener.onDownloadClick(reporte));
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    // ViewHolder
    public static class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha;
        Button btnDetalles;
        ImageButton btnDownload;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}

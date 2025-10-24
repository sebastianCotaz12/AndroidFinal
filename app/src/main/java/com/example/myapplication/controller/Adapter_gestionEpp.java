package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter_gestionEpp extends RecyclerView.Adapter<Adapter_gestionEpp.ViewHolder> {

    private final Context context;
    private final List<Item_gestionEpp> lista;
    private final PrefsManager prefsManager;

    public Adapter_gestionEpp(Context context, List<Item_gestionEpp> lista) {
        this.context = context;
        this.lista = lista;
        this.prefsManager = new PrefsManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_item_gestion_epp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item_gestionEpp item = lista.get(position);

        // Nombre usuario desde preferencias
        String nombreUsuario = prefsManager.getNombreUsuario();
        String nombreTexto = (nombreUsuario != null && !nombreUsuario.isEmpty())
                ? nombreUsuario
                : "Gestión EPP";
        holder.txtNombre.setText(nombreTexto);

        // Fecha formateada (dd/MM/yyyy)
        String fechaFormateada = formatearFecha(item.getFecha_creacion());
        holder.txtFecha.setText(fechaFormateada);

        // Configurar estado visual
        configurarEstado(holder, item.getEstado());

        // Extraer nombre del producto (si existe)
        String productosTexto;
        if (item.getProductos() != null && !item.getProductos().isEmpty()) {
            productosTexto = item.getProductos(); // si ya es texto concatenado desde modelo
        } else {
            productosTexto = "Sin productos";
        }

        // Cargo (si viene nulo, mostrar texto por defecto)
        String cargoTexto = (item.getCargo() != null && !item.getCargo().isEmpty())
                ? item.getCargo()
                : "Sin cargo";

        // Botón Detalles
        String estadoFinal = holder.txtEstado.getText().toString().replace("Estado: ", "");
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_gestionEPP.class);
            intent.putExtra("id", String.valueOf(item.getId()));
            intent.putExtra("cedula", item.getCedula() != null ? item.getCedula() : "No disponible");
            intent.putExtra("importancia", item.getImportancia() != null ? item.getImportancia() : "No disponible");
            intent.putExtra("estado", estadoFinal);
            intent.putExtra("fecha_creacion", fechaFormateada);
            intent.putExtra("productos", productosTexto);
            intent.putExtra("cargo", cargoTexto);
            intent.putExtra("area", item.getArea() != null ? item.getArea() : "No disponible");
            intent.putExtra("cantidad", String.valueOf(item.getCantidad()));
            intent.putExtra("nombre_usuario", nombreUsuario != null ? nombreUsuario : "No disponible");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // Botón Descargar
        holder.btnDownload.setOnClickListener(v ->
                Toast.makeText(context, "Descargando gestión EPP...", Toast.LENGTH_SHORT).show());
    }

    // 🔹 Formatear fecha en dd/MM/yyyy
    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return "Fecha no disponible";
        }
        try {
            SimpleDateFormat formatoEntrada;
            if (fechaOriginal.contains("T")) {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                return fechaOriginal;
            }
            Date fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
            return formatoSalida.format(fecha);
        } catch (ParseException e) {
            return fechaOriginal;
        }
    }

    // 🔹 Configurar texto legible y color del estado
    private void configurarEstado(ViewHolder holder, String estado) {
        String estadoLegible = "Pendiente"; // Valor por defecto

        if (estado != null) {
            if (estado.equalsIgnoreCase("true")) {
                estadoLegible = "Activo";
            } else if (estado.equalsIgnoreCase("false")) {
                estadoLegible = "Inactivo";
            } else {
                estadoLegible = estado; // Por si el backend manda "pendiente" o algo textual
            }
        }

        holder.txtEstado.setText("Estado: " + estadoLegible);

        int colorEstado;
        switch (estadoLegible.toLowerCase()) {
            case "activo":
            case "completado":
            case "finalizado":
                colorEstado = ContextCompat.getColor(context, R.color.estado_realizado);
                break;
            case "pendiente":
            case "inactivo":
                colorEstado = ContextCompat.getColor(context, R.color.evento_emergencia);
                break;
            case "en proceso":
            case "en progreso":
                colorEstado = ContextCompat.getColor(context, R.color.estado_proceso);
                break;
            default:
                colorEstado = ContextCompat.getColor(context, android.R.color.darker_gray);
                break;
        }

        holder.indicadorEstado.setBackgroundColor(colorEstado);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha, txtEstado;
        MaterialButton btnDetalles;
        ImageButton btnDownload;
        View indicadorEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            indicadorEstado = itemView.findViewById(R.id.indicadorEstado);
        }
    }
}

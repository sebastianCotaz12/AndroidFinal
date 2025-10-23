package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.controller.Item_listaChequeo;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter_listaChequeo extends RecyclerView.Adapter<Adapter_listaChequeo.ViewHolder> {

    private final Context context;
    private final List<Item_listaChequeo> lista;

    public Adapter_listaChequeo(Context context, List<Item_listaChequeo> lista) {
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
        Item_listaChequeo item = lista.get(position);

        // txtNombre: Nombre + Placa (campos más importantes)
        String nombreTexto = item.getNombre() != null ? item.getNombre() : "Sin nombre";
        if (item.getPlaca() != null && !item.getPlaca().isEmpty()) {
            nombreTexto += " - " + formatearPlaca(item.getPlaca()); // 🔹 Placa en mayúsculas
        }
        holder.txtNombre.setText(nombreTexto);

        // txtFecha: Fecha formateada + Estado
        String fechaFormateada = formatearFecha(item.getFecha());
        holder.txtFecha.setText(fechaFormateada);

        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_listaChequeo.class);
            intent.putExtra("usuario", item.getNombre());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("hora", item.getHora());
            intent.putExtra("modelo", item.getModelo());
            intent.putExtra("marca", item.getMarca());
            intent.putExtra("soat", item.getSoat());
            intent.putExtra("tecnico", item.getTecnico());
            intent.putExtra("kilometraje", item.getKilometraje());
            intent.putExtra("placa", item.getPlaca());
            intent.putExtra("observaciones", item.getObservaciones());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.btnDownload.setOnClickListener(v -> {
            // Implementa aquí la funcionalidad de descarga si deseas
        });
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) {
            return "Fecha no disponible";
        }

        try {
            SimpleDateFormat formatoEntrada;
            Date fecha;

            if (fechaOriginal.contains("T")) {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            }

            fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            return fechaOriginal;
        }
    }

    private String formatearPlaca(String placa) {
        if (placa == null || placa.isEmpty()) {
            return "";
        }
        return placa.toUpperCase(); // 🔹 Convertir a mayúsculas
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtFecha;
        MaterialButton btnDetalles;
        ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}
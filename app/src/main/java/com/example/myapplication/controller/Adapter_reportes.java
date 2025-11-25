package com.example.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter_reportes extends RecyclerView.Adapter<Adapter_reportes.ViewHolder> {

    private final Context context;
    private List<ItemReporte> lista;           // LISTA QUE SE MUESTRA
    private final List<ItemReporte> listaFull; // LISTA ORIGINAL (NO SE MODIFICA)

    public Adapter_reportes(Context context, List<ItemReporte> listaInicial) {
        this.context = context;
        this.lista = new ArrayList<>(listaInicial);
        this.listaFull = new ArrayList<>(listaInicial);
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
        ItemReporte item = lista.get(position);

        // Nombre y lugar
        holder.txtNombre.setText(
                (item.getNombreUsuario() != null ? item.getNombreUsuario() : "Sin nombre")
                        + " - " +
                        (item.getLugar() != null ? item.getLugar() : "Sin lugar")
        );

        // Fecha
        holder.txtFecha.setText(formatearFecha(item.getFecha()));

        // Estado
        configurarEstado(holder, item.getEstado());

        // Imagen
        cargarImagen(holder, item.getImagen());

        // Detalles
        holder.btnDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalles_reportes.class);
            intent.putExtra("nombre_usuario", item.getNombreUsuario());
            intent.putExtra("cargo", item.getCargo());
            intent.putExtra("cedula", item.getCedula());
            intent.putExtra("fecha", item.getFecha());
            intent.putExtra("lugar", item.getLugar());
            intent.putExtra("descripcion", item.getDescripcion());
            intent.putExtra("imagen", item.getImagen());
            intent.putExtra("archivos", item.getArchivos());
            intent.putExtra("estado", item.getEstado());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // Descargar archivo
        holder.btnDownload.setOnClickListener(v -> {
            String archivo = item.getArchivos();
            if (archivo != null && !archivo.isEmpty() && !archivo.equals("null")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(archivo));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "⚠️ No se puede abrir el archivo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No hay archivo disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------------------------------------------------------
    //                   FILTRO TEXTO + FECHA
    // ---------------------------------------------------------
    public void filtrar(String texto, String fecha) {
        lista = new ArrayList<>();

        for (ItemReporte item : listaFull) {

            boolean coincideTexto =
                    texto == null || texto.isEmpty() ||
                            item.getNombreUsuario().toLowerCase().contains(texto.toLowerCase()) ||
                            item.getLugar().toLowerCase().contains(texto.toLowerCase());

            boolean coincideFecha =
                    fecha == null || fecha.isEmpty() ||
                            (item.getFecha() != null && item.getFecha().startsWith(fecha));

            if (coincideTexto && coincideFecha) {
                lista.add(item);
            }
        }

        notifyDataSetChanged();
    }

    // ---------------------------------------------------------
    //           MÉTODOS DE IMAGEN, ESTADO Y FECHA
    // ---------------------------------------------------------
    private void cargarImagen(ViewHolder holder, String imagenUrl) {
        if (imagenUrl != null && !imagenUrl.isEmpty() && !imagenUrl.equals("null")) {
            Glide.with(context)
                    .load(imagenUrl)
                    .transform(new CenterCrop(), new RoundedCorners(16))
                    .placeholder(R.drawable.reportes)
                    .error(R.drawable.reportes)
                    .into(holder.imgReporte);
        } else {
            Glide.with(context)
                    .load(R.drawable.reportes)
                    .transform(new CenterCrop(), new RoundedCorners(16))
                    .into(holder.imgReporte);
        }
    }

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty()) return "Fecha no disponible";

        try {
            SimpleDateFormat formatoEntrada;
            if (fechaOriginal.contains("T")) {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            } else {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            }

            Date fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            return fechaOriginal;
        }
    }

    private void configurarEstado(ViewHolder holder, String estado) {
        if (estado == null) {
            holder.txtEstado.setText("Estado: No definido");
            holder.indicadorEstado.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            return;
        }

        holder.txtEstado.setText("Estado: " + estado);
        int color;

        switch (estado.toLowerCase()) {
            case "realizado":
                color = ContextCompat.getColor(context, R.color.estado_realizado);
                break;
            case "en proceso":
                color = ContextCompat.getColor(context, R.color.estado_proceso);
                break;
            case "pendiente":
                color = ContextCompat.getColor(context, R.color.estado_pendiente);
                break;
            default:
                color = ContextCompat.getColor(context, android.R.color.darker_gray);
        }

        holder.indicadorEstado.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void filtrar(List<ItemReporte> filtrada) {
    }

    // ---------------------------------------------------------
    //                  VIEW HOLDER
    // ---------------------------------------------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgReporte;
        TextView txtNombre, txtFecha, txtEstado;
        MaterialButton btnDetalles;
        ImageButton btnDownload;
        View indicadorEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgReporte = itemView.findViewById(R.id.imgPoster);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            btnDetalles = itemView.findViewById(R.id.btnDetalles);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            indicadorEstado = itemView.findViewById(R.id.indicadorEstado);
        }
    }
}

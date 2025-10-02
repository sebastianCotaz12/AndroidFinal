package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ItemReporte;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityListaReportesBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class Lista_reportes extends AppCompatActivity {

    private ActivityListaReportesBinding binding;
    private ListaReportesAdapter adapter;
    private List<ItemReporte> listaReportes = new ArrayList<>();

    // 🔹 Ajusta esta URL a tu endpoint real
    private final String URL_API = "https://backsst.onrender.com/listarReportes";

    private final ActivityResultLauncher<Intent> formLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Si se guardó un nuevo reporte, recargar lista
                    obtenerReportes();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ajuste de márgenes por sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView
        RecyclerView recyclerView = binding.recyclerViewListaReportes;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListaReportesAdapter(this, listaReportes, new ListaReportesAdapter.OnItemClickListener() {
            @Override
            public void onDetallesClick(ItemReporte reporte) {
                // Abrir actividad de detalles
                Intent intent = new Intent(Lista_reportes.this, Detalles_reportes.class);
                intent.putExtra("idReporte", reporte.getId());
                startActivity(intent);
            }

            @Override
            public void onDownloadClick(ItemReporte reporte) {
                // Lógica de descarga
                Toast.makeText(Lista_reportes.this, "Descargando: " + reporte.getArchivos(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        // Llamar API
        obtenerReportes();

        // Botón para crear nuevo reporte
        binding.imgButtonCrearreporte.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_reportes.this, Form_reportes.class);
            formLauncher.launch(intent);
        });
    }

    private void obtenerReportes() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        JSONArray datos = response.getJSONArray("datos");
                        listaReportes.clear();

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);
                            ItemReporte item = new ItemReporte(
                                    obj.getInt("idReporte"),
                                    obj.getString("nombreUsuario"),
                                    obj.getString("cargo"),
                                    obj.getString("cedula"),
                                    obj.getString("fecha"),
                                    obj.getString("lugar"),
                                    obj.getString("descripcion"),
                                    obj.optString("imagen"),
                                    obj.optString("archivos"),
                                    obj.optString("estado")
                            );
                            listaReportes.add(item);
                        }
                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error parseando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error API: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }
}

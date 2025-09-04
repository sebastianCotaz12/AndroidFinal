package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.controller.adapter_reportes;
import com.example.myapplication.controller.item_reportes;
import com.example.myapplication.databinding.ActivityListaReportesBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class lista_reportes extends AppCompatActivity {

    ActivityListaReportesBinding binding;
    adapter_reportes adapter;
    List<item_reportes> listaReportes = new ArrayList<>();

    // 🔹 Ajusta esta URL a tu endpoint real
    String URL_API = "https://backsst.onrender.com/listarReportes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView
        RecyclerView recyclerView = binding.recyclerViewListaReportes;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new adapter_reportes(this, listaReportes);
        recyclerView.setAdapter(adapter);

        // Llamar API
        obtenerReportes();

        // Botón para crear nuevo reporte
        binding.imgButtonCrearreporte.setOnClickListener(v -> {
            startActivity(new Intent(lista_reportes.this, form_reportes.class));
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

                            item_reportes item = new item_reportes(
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

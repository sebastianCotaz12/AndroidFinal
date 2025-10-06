package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
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
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityListaEventosBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Lista_eventos extends AppCompatActivity {

    private ActivityListaEventosBinding  binding;

    private final List<Item_eventos> listaEventos = new ArrayList<>();
    private Adapter_eventos adapter;

    private static final String URL_API = "https://backsst.onrender.com/listarEventos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaEventosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ajustar padding por barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView con un layout manager
        RecyclerView recyclerView = binding.recyclerEventos;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Asignar el adapter (sin DBHelper, solo consumo de API)
        adapter = new Adapter_eventos(this, listaEventos);
        recyclerView.setAdapter(adapter);

        // Llamar a la API
        obtenerEventos();

        // Botón para crear nueva actividad
        binding.imgButtonCrearEvento.setOnClickListener(v -> {
            startActivity(new Intent(Lista_eventos.this, Form_eventos.class));
        });
    }




    private void obtenerEventos() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        JSONArray datos = response.getJSONArray("datos");

                        listaEventos.clear();

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            Item_eventos item = new Item_eventos(
                                    obj.getString("titulo"),
                                    obj.getString("fechaEvento"),
                                    obj.getString("descripcion"),
                                    obj.optString("archivoAdjunto", ""),
                                    obj.optString("imagenVideo", "")
                            );
                            listaEventos.add(item);
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

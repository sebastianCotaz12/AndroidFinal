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
import com.example.myapplication.databinding.ActivityListaActLudicasBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Lista_actLudicas extends AppCompatActivity {

    private ActivityListaActLudicasBinding binding; // ✅ corregido
    private final List<Item_actLudicas> listaActividades = new ArrayList<>();
    private Adapter_actLudica adapter;

    private static final String URL_API = "https://backsst.onrender.com/listarActividadesLudicas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaActLudicasBinding.inflate(getLayoutInflater()); // ✅ corregido
        setContentView(binding.getRoot());

        // Ajustar padding por barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView con un layout manager
        RecyclerView recyclerView = binding.recyclerViewListaChequeo;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Asignar el adapter (sin DBHelper, solo consumo de API)
        adapter = new Adapter_actLudica(this, listaActividades);
        recyclerView.setAdapter(adapter);

        // Llamar a la API
        obtenerActividades();

        // Botón para crear nueva actividad
        binding.imgButtonCrearlista.setOnClickListener(v -> {
            startActivity(new Intent(Lista_actLudicas.this, Form_actLudicas.class));
        });
    }

    private void obtenerActividades() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        JSONArray datos = response.getJSONArray("datos");

                        listaActividades.clear();
                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            Item_actLudicas item = new Item_actLudicas(
                                    obj.getInt("id"),
                                    obj.getString("nombreUsuario"),
                                    obj.getString("nombreActividad"),
                                    obj.getString("fechaActividad"),
                                    obj.getString("descripcion"),
                                    obj.optString("archivoAdjunto", ""),
                                    obj.optString("imagenVideo", "")
                            );
                            listaActividades.add(item);
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

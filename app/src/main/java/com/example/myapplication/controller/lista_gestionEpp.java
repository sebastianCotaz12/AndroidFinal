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
import com.example.myapplication.controller.adapter_gestionEpp;
import com.example.myapplication.controller.item_gestionEpp;
import com.example.myapplication.databinding.ActivityListaGestionEppBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class lista_gestionEpp extends AppCompatActivity {

    ActivityListaGestionEppBinding binding;
    adapter_gestionEpp adapter;
    List<item_gestionEpp> listaGestion = new ArrayList<>();

    // 🔹 Ajusta esta URL a tu endpoint real
    String URL_API = "https://backsst.onrender.com/listarGestiones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaGestionEppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView
        RecyclerView recyclerView = binding.recyclerViewListaChequeo;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new adapter_gestionEpp(this, listaGestion);
        recyclerView.setAdapter(adapter);

        // Llamar API
        obtenerGestiones();

        // Botón para crear nueva gestión
        // Botón para crear nueva lista de chequeo
        binding.imgButtonCrearlista.setOnClickListener(v -> {
            startActivity(new Intent(lista_gestionEpp.this, form_gestionEpp.class));
        });

    }

    private void obtenerGestiones() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        JSONArray datos = response.getJSONArray("datos");

                        listaGestion.clear();
                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            item_gestionEpp item = new item_gestionEpp(
                                    obj.getInt("idUsuario"),
                                    obj.getString("nombre"),
                                    obj.getString("apellido"),
                                    obj.getString("cedula"),
                                    obj.getString("cargo"),
                                    obj.getString("productos"),
                                    obj.getInt("cantidad"),
                                    obj.getString("importancia"),
                                    obj.optString("estado"),
                                    obj.getString("fechaCreacion")
                            );
                            listaGestion.add(item);
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

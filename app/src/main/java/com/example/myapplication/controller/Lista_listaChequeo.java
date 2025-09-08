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
import com.example.myapplication.databinding.ActivityListaListaChequeoBinding
        ;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Lista_listaChequeo extends AppCompatActivity {

    private ActivityListaListaChequeoBinding binding;
    private Adapter_listaChequeo adapter;
    private List<Item_listaChequeo> listaChequeos = new ArrayList<>();

    private static final String URL_API = "https://backsst.onrender.com/listarListasChequeo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaListaChequeoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView
        RecyclerView recyclerView = binding.recyclerViewListaChequeo;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_listaChequeo(this, listaChequeos);
        recyclerView.setAdapter(adapter);

        // Llamar API
        obtenerListasChequeo();

        // Botón para crear nueva lista de chequeo
        binding.imgButtonCrearlista.setOnClickListener(v -> {
            startActivity(new Intent(Lista_listaChequeo.this, Form_listaChequeo.class));
        });
    }

    private void obtenerListasChequeo() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        JSONArray datos = response.getJSONArray("datos");

                        listaChequeos.clear();
                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            Item_listaChequeo item = new Item_listaChequeo(
                                    obj.getString("usuarioNombre"),
                                    obj.getString("fecha"),
                                    obj.getString("hora"),
                                    obj.getString("modelo"),
                                    obj.getString("marca"),
                                    obj.getString("soat"),
                                    obj.getString("tecnico"),
                                    obj.getString("kilometraje")
                            );
                            listaChequeos.add(item);
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


package com.example.myapplication.controller;

import android.annotation.SuppressLint;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lista_eventos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_eventos adapter;
    private List<Item_eventos> lista = new ArrayList<>();
    private PrefsManager prefsManager;

    private final String URL_API = "https://backsst.onrender.com/listarblog";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        prefsManager = new PrefsManager(this);

        recyclerView = findViewById(R.id.recyclerViewEventos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter_eventos(this, lista);
        recyclerView.setAdapter(adapter);

        // Ajuste márgenes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Botón Crear nueva actividad ---
        ImageButton btnCrear = findViewById(R.id.imgButton_crearlista);
        btnCrear.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_eventos.this, Form_eventos.class);
            startActivity(intent); // Aquí reemplazamos crearLauncher
        });

        obtenerEventos();
    }

    private void obtenerEventos() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String token = prefsManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "⚠️ Debes iniciar sesión primero", Toast.LENGTH_LONG).show();
            return;
        }

        JsonArrayRequest request = new JsonArrayRequest(
                URL_API,
                response -> {
                    try {
                        lista.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Item_eventos item = new Item_eventos(
                                    obj.getString("tituloEvento"),
                                    obj.getString("fechaEvento"),
                                    obj.getString("descripcionEvento"),
                                    obj.optString("adjuntarEvento")
                            );
                            lista.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(Lista_eventos.this, "Error parseando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error API: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

        queue.add(request);
    }
}

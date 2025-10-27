package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.myapplication.utils.PrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lista_actLudicas extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_actLudica adapter;
    private List<Item_actLudicas> lista = new ArrayList<>();
    private PrefsManager prefsManager;

    private final String URL_API = "https://backsst.onrender.com/listarActis";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_act_ludicas);

        prefsManager = new PrefsManager(this);

        recyclerView = findViewById(R.id.recyclerViewListaChequeo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter_actLudica(this, lista);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnCrear = findViewById(R.id.imgButton_crearlista);
        btnCrear.setOnClickListener(v -> {
            startActivity(new Intent(Lista_actLudicas.this, Form_actLudicas.class));
        });

        ImageView btnVolverLogin = findViewById(R.id.imgButton_VolverInicio);
        btnVolverLogin.setOnClickListener(v -> {
            startActivity(new Intent(Lista_actLudicas.this, Menu.class));
            finish();
        });

        obtenerActividades();
    }

    private void obtenerActividades() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String token = prefsManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "⚠️ Debes iniciar sesión primero", Toast.LENGTH_LONG).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        lista.clear();
                        JSONArray datos = response.getJSONArray("data");

                        Log.d("ACTLUDICAS_API", "Respuesta recibida: " + datos.length() + " elementos");

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            // DEBUG: Ver el JSON completo
                            Log.d("ACTLUDICAS_API", "Elemento " + i + ": " + obj.toString());

                            Item_actLudicas item = new Item_actLudicas(
                                    obj.getInt("id"),
                                    obj.getString("nombreUsuario"),
                                    obj.getString("nombreActividad"),
                                    obj.getString("fechaActividad"),
                                    obj.getString("descripcion"),
                                    obj.optString("archivoAdjunto", ""), // <-- Campo archivo adjunto
                                    obj.optString("imagenVideo", "") // <-- Imagen de Cloudinary
                            );
                            lista.add(item);

                            // DEBUG: Verificar campos específicos
                            Log.d("ACTLUDICAS_API", "Archivo adjunto: " + obj.optString("archivoAdjunto", ""));
                        }
                        adapter.notifyDataSetChanged();

                        if (lista.isEmpty()) {
                            Toast.makeText(this, "No hay actividades disponibles", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Actividades cargadas: " + lista.size(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("ACTLUDICAS_API", "Error parseando datos: " + e.getMessage());
                        Toast.makeText(this, "Error parseando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("ACTLUDICAS_API", "Error API: " + error.getMessage());
                    Toast.makeText(this, "Error API: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}
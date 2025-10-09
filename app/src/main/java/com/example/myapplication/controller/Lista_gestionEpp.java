package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lista_gestionEpp extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_gestionEpp adapter;
    private List<Item_gestionEpp> lista = new ArrayList<>();
    private PrefsManager prefsManager;

    private final String URL_API = "https://backsst.onrender.com/listarGestiones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gestion_epp); // ✅ CORRECTO

        prefsManager = new PrefsManager(this);
        recyclerView = findViewById(R.id.recyclerViewListaChequeo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_gestionEpp(this, lista);
        recyclerView.setAdapter(adapter);

        ImageButton btnCrear = findViewById(R.id.imgButton_crearlista);
        btnCrear.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_gestionEpp.this, Form_gestionEpp.class);
            startActivity(intent);
        });

        obtenerGestiones();
    }

    private void obtenerGestiones() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String token = prefsManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "⚠️ Debes iniciar sesión primero", Toast.LENGTH_LONG).show();
            return;
        }

        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        JSONArray datosArray = response.getJSONArray("datos");
                        lista.clear();

                        for (int i = 0; i < datosArray.length(); i++) {
                            JSONObject obj = datosArray.getJSONObject(i);
                            Item_gestionEpp item = new Item_gestionEpp(
                                    obj.getInt("id"),
                                    obj.optString("cedula", "Sin cédula"),
                                    obj.optString("importancia", "N/A"),
                                    obj.optString("estado", "N/A"),
                                    obj.optString("fecha_creacion", "Sin fecha"),
                                    obj.optString("productos", "Sin productos"),
                                    obj.optString("cargo", "Sin cargo"),
                                    obj.optString("area", "Sin área"),
                                    obj.optInt("cantidad", 0)
                            );
                            lista.add(item);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error API: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                if (token != null) headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}

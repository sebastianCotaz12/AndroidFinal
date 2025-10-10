package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.myapplication.databinding.ActivityListaListaChequeoBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lista_listaChequeo extends AppCompatActivity {

    private ActivityListaListaChequeoBinding binding;
    private Adapter_listaChequeo adapter;
    private List<Item_listaChequeo> listaChequeos = new ArrayList<>();

    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    private static final String URL_API = "https://backsst.onrender.com/listarListasChequeo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaListaChequeoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar manejo de sesión
        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // Verificar sesión activa
        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

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

        // Cargar datos desde API
        obtenerListasChequeo();

        // Botón para crear nueva lista de chequeo
        binding.imgButtonCrearlista.setOnClickListener(v -> {
            startActivity(new Intent(Lista_listaChequeo.this, Form_listaChequeo.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar lista al volver del formulario
        obtenerListasChequeo();
    }

    private void obtenerListasChequeo() {
        String token = prefsManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "🚫 Token inválido. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://backsst.onrender.com/listarlistasU", // ✅ nuevo endpoint filtrado por usuario
                null,
                response -> {
                    try {
                        JSONArray datos = response.getJSONArray("data"); // ✅ accede a "data", no "datos"

                        listaChequeos.clear();
                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            // 🔹 Crear el objeto con los nuevos campos
                            Item_listaChequeo item = new Item_listaChequeo(
                                    obj.optString("usuarioNombre", "N/A"),
                                    obj.optString("fecha", "Sin fecha"),
                                    obj.optString("hora", "Sin hora"),
                                    obj.optString("modelo", "Sin modelo"),
                                    obj.optString("marca", "Sin marca"),
                                    obj.optString("soat", "N/A"),
                                    obj.optString("tecnico", "N/A"),
                                    obj.optString("kilometraje", "0"),
                                    obj.optString("placa", "Sin placa"),
                                    obj.optString("observaciones", "Sin observaciones")
                            );

                            listaChequeos.add(item);
                        }

                        adapter.notifyDataSetChanged();

                        if (listaChequeos.isEmpty()) {
                            Toast.makeText(this, "No hay listas de chequeo disponibles.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("LISTA_ERR", "Error parseando datos", e);
                        Toast.makeText(this, "Error procesando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("LISTA_API_ERR", "Error API: " + error.getMessage());
                    Toast.makeText(this, "Error API: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(request);
    }
}

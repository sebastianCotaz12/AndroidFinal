package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.myapplication.databinding.ActivityListaReportesBinding;
import com.example.myapplication.utils.PrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Lista_reportes extends AppCompatActivity {

    private ActivityListaReportesBinding binding;
    private Adapter_reportes adapter;
    private List<ItemReporte> listaReportes = new ArrayList<>();
    private PrefsManager prefsManager;

    private final String URL_API = "https://unreproaching-rancorously-evelina.ngrok-free.dev/listarUsu";

    private final ActivityResultLauncher<Intent> formLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    obtenerReportes();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = binding.recyclerViewListaReportes;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter_reportes(this, listaReportes);
        recyclerView.setAdapter(adapter);

        obtenerReportes();

        binding.imgButtonCrearreporte.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_reportes.this, Form_reportes.class);
            formLauncher.launch(intent);
        });

        ImageView btnVolverLogin = findViewById(R.id.imgButton_VolverInicio);
        btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_reportes.this, Menu.class);
            startActivity(intent);
            finish();
        });
    }

    private void obtenerReportes() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String token = prefsManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "⚠ Debes iniciar sesión primero", Toast.LENGTH_LONG).show();
            return;
        }

        int page = 1;
        int perPage = 20;
        String q = "";
        String estado = "";

        String url = URL_API + "?page=" + page + "&perPage=" + perPage;
        try {
            if (!q.isEmpty()) url += "&q=" + URLEncoder.encode(q, "UTF-8");
            if (!estado.isEmpty()) url += "&estado=" + URLEncoder.encode(estado, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject meta = response.getJSONObject("meta");
                        JSONArray datos = response.getJSONArray("data");
                        listaReportes.clear();

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            // Manejar cargo correctamente
                            String cargo = "No disponible";
                            if (obj.has("cargo")) {
                                if (obj.isNull("cargo")) {
                                    cargo = "No disponible";
                                } else if (obj.get("cargo") instanceof String) {
                                    cargo = obj.getString("cargo");
                                } else if (obj.get("cargo") instanceof JSONObject) {
                                    JSONObject cargoObj = obj.getJSONObject("cargo");
                                    cargo = cargoObj.optString("nombre", "No disponible");
                                }
                            }
                            cargo = cargo.replace("[", "").replace("]", "").replace("\"", "").trim();

                            // 🔹 OBTENER CÉDULA COMO STRING
                            String cedula = "";
                            if (obj.has("cedula") && !obj.isNull("cedula")) {
                                if (obj.get("cedula") instanceof Integer) {
                                    cedula = String.valueOf(obj.getInt("cedula"));
                                } else if (obj.get("cedula") instanceof String) {
                                    cedula = obj.getString("cedula");
                                } else if (obj.get("cedula") instanceof Double) {
                                    cedula = String.valueOf(obj.getDouble("cedula")).split("\\.")[0];
                                }
                            }

                            ItemReporte item = new ItemReporte(
                                    obj.getInt("idReporte"),
                                    obj.getString("nombreUsuario"),
                                    cargo,
                                    cedula, // 🔹 ENVIAR COMO STRING
                                    obj.getString("fecha"),
                                    obj.getString("lugar"),
                                    obj.getString("descripcion"),
                                    obj.optString("imagen", ""),
                                    obj.optString("archivos", ""),
                                    obj.optString("estado", "Pendiente")
                            );
                            listaReportes.add(item);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error parseando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error API: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }
}
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
import com.example.myapplication.databinding.ActivityListaGestionEppBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Lista_gestionEpp extends AppCompatActivity {

    private ActivityListaGestionEppBinding binding;
    private Adapter_gestionEpp adapter;
    private List<Item_gestionEpp> listaGestionEpp = new ArrayList<>();

    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    private static final String URL_API = "https://backsst.onrender.com/listarGestiones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaGestionEppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // ✅ Verificar sesión activa
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

        // ✅ Configurar RecyclerView
        RecyclerView recyclerView = binding.recyclerViewGestionEpp;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_gestionEpp(this, listaGestionEpp);
        recyclerView.setAdapter(adapter);

        // ✅ Cargar datos desde la API
        obtenerGestionesEpp();

        // ✅ Botón para crear nueva gestión EPP
        binding.imgButtonCrearlista.setOnClickListener(v -> {
            startActivity(new Intent(Lista_gestionEpp.this, Form_gestionEpp.class));
        });

        // ✅ Botón para abrir el detector de EPP
        ImageButton btnDetectorEpp = findViewById(R.id.imgButton_detector_epp);
        btnDetectorEpp.setOnClickListener(v -> {
            abrirDetectorEpp();
        });

        // ✅ También hacer clickeable toda la tarjeta del detector
        findViewById(R.id.card_detector_epp).setOnClickListener(v -> {
            abrirDetectorEpp();
        });

        // ✅ Botón volver al menú principal
        ImageView btnVolverLogin = findViewById(R.id.imgButton_VolverInicio);
        btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_gestionEpp.this, Menu.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Método para abrir el detector de EPP
     */
    private void abrirDetectorEpp() {
        try {
            Intent intent = new Intent(Lista_gestionEpp.this, Detector_obj.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("DETECTOR_ERR", "Error al abrir detector: " + e.getMessage());
            Toast.makeText(this, "Error al abrir detector de EPP", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar la lista al volver del formulario o detector
        obtenerGestionesEpp();
    }

    private void obtenerGestionesEpp() {
        String token = prefsManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "🚫 Token inválido. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        JSONArray datos = response.getJSONArray("datos");
                        listaGestionEpp.clear();

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            // ✅ Fecha formateada
                            String fechaCreacion = obj.optString("fechaCreacion", null);
                            if (fechaCreacion == null || fechaCreacion.equals("null") || fechaCreacion.isEmpty()) {
                                fechaCreacion = obj.optString("createdAt", "Sin fecha");
                            }
                            String fechaFormateada = formatearFecha(fechaCreacion);

                            // ✅ Estado traducido
                            String estadoTexto = "Pendiente";
                            if (obj.has("estado") && !obj.isNull("estado")) {
                                String estado = obj.getString("estado");
                                if (estado.equalsIgnoreCase("true") || estado.equalsIgnoreCase("activo")) {
                                    estadoTexto = "Activo";
                                } else if (estado.equalsIgnoreCase("false") || estado.equalsIgnoreCase("inactivo")) {
                                    estadoTexto = "Inactivo";
                                }
                            }

                            // ✅ Área
                            String areaNombre = "Sin área";
                            if (obj.has("area") && !obj.isNull("area")) {
                                JSONObject areaObj = obj.getJSONObject("area");
                                areaNombre = areaObj.optString("nombre", "Sin área");
                            }

                            // ✅ Cargo
                            String cargoNombre = "Sin cargo";
                            if (obj.has("cargo") && !obj.isNull("cargo")) {
                                JSONObject cargoObj = obj.getJSONObject("cargo");
                                cargoNombre = cargoObj.optString("nombre", "Sin cargo");
                            }

                            // ✅ Productos
                            String productosTexto = "Sin productos";
                            if (obj.has("productos")) {
                                JSONArray productosArr = obj.getJSONArray("productos");
                                if (productosArr.length() > 0) {
                                    List<String> nombresProductos = new ArrayList<>();
                                    for (int j = 0; j < productosArr.length(); j++) {
                                        JSONObject prod = productosArr.getJSONObject(j);
                                        nombresProductos.add(prod.optString("nombre", "Desconocido"));
                                    }
                                    productosTexto = String.join(", ", nombresProductos);
                                }
                            }

                            // ✅ Crear el objeto
                            Item_gestionEpp item = new Item_gestionEpp(
                                    obj.optInt("id", 0),
                                    obj.optString("cedula", "N/A"),
                                    obj.optString("importancia", "N/A"),
                                    estadoTexto,
                                    fechaFormateada,
                                    productosTexto,
                                    cargoNombre,
                                    areaNombre,
                                    obj.optInt("cantidad", 0)
                            );

                            listaGestionEpp.add(item);
                        }

                        adapter.notifyDataSetChanged();

                        if (listaGestionEpp.isEmpty()) {
                            Toast.makeText(this, "No hay gestiones EPP disponibles.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("GESTION_ERR", "Error parseando datos", e);
                        Toast.makeText(this, "Error procesando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("GESTION_API_ERR", "Error API: " + error.getMessage());
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

    private String formatearFecha(String fechaOriginal) {
        if (fechaOriginal == null || fechaOriginal.isEmpty() || fechaOriginal.equals("Sin fecha")) {
            return "Fecha no disponible";
        }

        try {
            SimpleDateFormat formatoEntrada;
            Date fecha;

            if (fechaOriginal.contains("T")) {
                formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                return fechaOriginal;
            }

            fecha = formatoEntrada.parse(fechaOriginal);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return formatoSalida.format(fecha);

        } catch (ParseException e) {
            return fechaOriginal;
        }
    }
}
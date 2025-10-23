package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class Lista_gestionEpp extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_gestionEpp adapter;
    private List<Item_gestionEpp> lista = new ArrayList<>();
    private PrefsManager prefsManager;

    private final String URL_API = "https://backsst.onrender.com/listarGestiones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gestion_epp);

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

        // 🔹 Botón de regresar al inicio de sesión
        ImageView btnVolverLogin = findViewById(R.id.imgButton_VolverInicio);
        btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_gestionEpp.this, Menu.class);
            startActivity(intent);
            finish();
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

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://backsst.onrender.com/listarGestions",
                null,
                response -> {
                    try {
                        JSONArray datosArray = response.getJSONArray("data");
                        lista.clear();

                        for (int i = 0; i < datosArray.length(); i++) {
                            JSONObject obj = datosArray.getJSONObject(i);

                            JSONArray productosArray = obj.optJSONArray("productos");
                            StringBuilder productosNombres = new StringBuilder();
                            if (productosArray != null) {
                                for (int j = 0; j < productosArray.length(); j++) {
                                    JSONObject prod = productosArray.getJSONObject(j);
                                    String nombreProd = prod.optString("nombre", "Desconocido");
                                    if (j > 0) productosNombres.append(", ");
                                    productosNombres.append(nombreProd);
                                }
                            }

                            String nombreArea = "Sin área";
                            JSONObject areaObj = obj.optJSONObject("area");
                            if (areaObj != null) {
                                nombreArea = areaObj.optString("nombre", "Sin área");
                            }

                            String fechaRaw = obj.optString("createdAt", null);
                            String fechaFormateada = "Sin fecha";
                            if (fechaRaw != null && !fechaRaw.equals("null")) {
                                fechaFormateada = formatearFecha(fechaRaw);
                            }

                            boolean estadoBool = obj.optBoolean("estado", false);
                            String estadoTexto = estadoBool ? "Activo" : "Inactivo";

                            String cargo = "ID: " + obj.optInt("idCargo", 0);

                            Item_gestionEpp item = new Item_gestionEpp(
                                    obj.getInt("id"),
                                    obj.optString("cedula", "Sin cédula"),
                                    obj.optString("importancia", "N/A"),
                                    estadoTexto,
                                    fechaFormateada,
                                    productosNombres.toString(),
                                    cargo,
                                    nombreArea,
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

    private String formatearFecha(String fechaIso) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaIso);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return formatoSalida.format(fecha);
        } catch (ParseException e) {
            return "Sin fecha";
        }
    }
}

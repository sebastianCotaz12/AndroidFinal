package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.example.myapplication.databinding.ActivityListaListaChequeoBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Lista_listaChequeo extends AppCompatActivity {

    private ActivityListaListaChequeoBinding binding;

    private Adapter_listaChequeo adapter;

    // Listas
    private final List<Item_listaChequeo> listaOriginal = new ArrayList<>();
    private final List<Item_listaChequeo> listaFiltrada = new ArrayList<>();

    // Sesión
    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    // Filtros
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    private static final String URL_API = "https://backsst.onrender.com/listarlistasU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaListaChequeoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        configurarRecycler();
        configurarFiltros();

        obtenerListasChequeo();

        // Crear nueva lista
        binding.imgButtonCrearlista.setOnClickListener(v ->
                startActivity(new Intent(this, Form_listaChequeo.class))
        );

        // Volver al menú
        binding.imgButtonVolverInicio.setOnClickListener(v -> {
            startActivity(new Intent(this, Menu.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerListasChequeo();
    }

    private void configurarRecycler() {
        RecyclerView rv = binding.recyclerViewListaChequeo;
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_listaChequeo(this, listaFiltrada);
        rv.setAdapter(adapter);
    }

    private void configurarFiltros() {
        // Fecha
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Abrir calendario
        binding.etFiltrarFecha.setOnClickListener(v -> {
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        String fecha = dateFormatter.format(calendar.getTime());

                        binding.etFiltrarFecha.setText(fecha);
                        binding.btnLimpiarFiltro.setVisibility(View.VISIBLE);

                        filtrar();
                    },
                    y, m, d
            ).show();
        });

        // Limpiar filtro
        binding.btnLimpiarFiltro.setOnClickListener(v -> {
            binding.etFiltrarFecha.setText("");
            binding.btnLimpiarFiltro.setVisibility(View.GONE);
            filtrar();
        });

        // Buscar texto
        binding.etBuscarLista.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar();
            }
        });
    }

    private void filtrar() {
        String texto = binding.etBuscarLista.getText().toString().toLowerCase();
        String fechaFiltro = binding.etFiltrarFecha.getText().toString();

        listaFiltrada.clear();

        for (Item_listaChequeo item : listaOriginal) {

            boolean coincideTexto =
                    item.getNombre().toLowerCase().contains(texto) ||
                            item.getMarca().toLowerCase().contains(texto) ||
                            item.getModelo().toLowerCase().contains(texto) ||
                            item.getPlaca().toLowerCase().contains(texto);

            boolean coincideFecha =
                    fechaFiltro.isEmpty() ||
                            item.getFecha().split("T")[0].equals(fechaFiltro);

            if (coincideTexto && coincideFecha) {
                listaFiltrada.add(item);
            }
        }

        adapter.notifyDataSetChanged();
        binding.tvTotalListas.setText("Total: " + listaFiltrada.size());
    }

    private void obtenerListasChequeo() {
        String token = prefsManager.getToken();

        if (token == null || token.isEmpty()) {
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
                        JSONArray datos = response.getJSONArray("data");

                        listaOriginal.clear();

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            listaOriginal.add(
                                    new Item_listaChequeo(
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
                                    )
                            );
                        }

                        listaFiltrada.clear();
                        listaFiltrada.addAll(listaOriginal);

                        adapter.notifyDataSetChanged();
                        binding.tvTotalListas.setText("Total: " + listaFiltrada.size());

                    } catch (Exception e) {
                        Log.e("LISTA_ERR", "Error parseando datos", e);
                        Toast.makeText(this, "Error procesando datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("LISTA_API_ERR", "Error API", error);
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

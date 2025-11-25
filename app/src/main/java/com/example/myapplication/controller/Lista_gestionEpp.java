package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Lista_gestionEpp extends AppCompatActivity {

    private ActivityListaGestionEppBinding binding;
    private Adapter_gestionEpp adapter;
    private List<Item_gestionEpp> listaOriginal = new ArrayList<>();
    private List<Item_gestionEpp> listaFiltrada = new ArrayList<>();

    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    // FILTROS
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private static final String URL_API =
            "https://unreproaching-rancorously-evelina.ngrok-free.dev/listarGestiones";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListaGestionEppBinding.inflate(getLayoutInflater());
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
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        configurarCalendario();
        configurarEventosFiltro();

        // Recycler
        binding.recyclerViewListaEPP.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_gestionEpp(this, listaFiltrada);
        binding.recyclerViewListaEPP.setAdapter(adapter);

        obtenerGestionesEpp();

        binding.imgButtonCrearlista.setOnClickListener(v -> {
            startActivity(new Intent(Lista_gestionEpp.this, Form_gestionEpp.class));
        });

        ImageView btnVolverLogin = findViewById(R.id.imgButton_VolverInicio);
        btnVolverLogin.setOnClickListener(v -> {
            startActivity(new Intent(Lista_gestionEpp.this, Menu.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtenerGestionesEpp();
    }

    private void configurarCalendario() {
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String fecha = dateFormatter.format(calendar.getTime());
            binding.etFiltrarFecha.setText(fecha);

            filtrarPorFecha(fecha);
            binding.btnLimpiarFiltro.setVisibility(View.VISIBLE);
        };

        datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    private void configurarEventosFiltro() {
        binding.etFiltrarFecha.setOnClickListener(v -> datePickerDialog.show());

        binding.btnLimpiarFiltro.setOnClickListener(v -> limpiarFiltroFecha());

        binding.etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarTexto(s.toString());
            }
        });
    }

    private void filtrarPorFecha(String fecha) {
        listaFiltrada.clear();

        for (Item_gestionEpp item : listaOriginal) {
            if (item.getFecha_creacion() != null &&
                    item.getFecha_creacion().contains("T")) {

                String soloFecha = item.getFecha_creacion().split("T")[0];
                if (soloFecha.equals(fecha)) {
                    listaFiltrada.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
        actualizarContador();
    }

    private void filtrarTexto(String texto) {
        listaFiltrada.clear();

        String fechaFiltro = binding.etFiltrarFecha.getText().toString();

        for (Item_gestionEpp item : listaOriginal) {

            boolean coincideTexto =
                    item.getCedula().toLowerCase().contains(texto.toLowerCase()) ||
                            item.getArea().toLowerCase().contains(texto.toLowerCase()) ||
                            item.getCargo().toLowerCase().contains(texto.toLowerCase());

            boolean coincideFecha = fechaFiltro.isEmpty() ||
                    (item.getFecha_creacion() != null &&
                            item.getFecha_creacion().contains(fechaFiltro));

            if (coincideTexto && coincideFecha) {
                listaFiltrada.add(item);
            }
        }

        adapter.notifyDataSetChanged();
        actualizarContador();
    }

    private void limpiarFiltroFecha() {
        binding.etFiltrarFecha.setText("");
        binding.btnLimpiarFiltro.setVisibility(View.GONE);

        filtrarTexto(binding.etBuscar.getText().toString());
        actualizarContador();
    }

    private void actualizarContador() {
        binding.tvTotalGestiones.setText("Total de gestiones: " + listaFiltrada.size());
    }

    private void obtenerGestionesEpp() {
        String token = prefsManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_LONG).show();
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
                        listaOriginal.clear();

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            Item_gestionEpp item = new Item_gestionEpp(
                                    obj.optInt("id", 0),
                                    obj.optString("cedula", ""),
                                    obj.optString("importancia", ""),
                                    obj.optString("estado", ""),
                                    obj.optString("fechaCreacion", ""),
                                    obj.optString("productos", ""),
                                    obj.optString("cargo", ""),
                                    obj.optString("area", ""),
                                    obj.optInt("cantidad", 0)
                            );

                            listaOriginal.add(item);
                        }

                        listaFiltrada.clear();
                        listaFiltrada.addAll(listaOriginal);
                        adapter.notifyDataSetChanged();
                        actualizarContador();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando datos", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
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

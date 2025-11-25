package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Calendar;
import java.util.List;

public class Lista_reportes extends AppCompatActivity {

    private ActivityListaReportesBinding binding;
    private Adapter_reportes adapter;
    private List<ItemReporte> listaReportes = new ArrayList<>();
    private PrefsManager prefsManager;

    private static final String URL_API = "https://unreproaching-rancorously-evelina.ngrok-free.dev/listarUsu";

    // Recargar reporte al volver del formulario
    private final ActivityResultLauncher<Intent> formLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            obtenerReportes();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        // Ajuste de insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets s = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(s.left, s.top, s.right, s.bottom);
            return insets;
        });

        // Configuración del RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewReportes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter_reportes(this, listaReportes);
        recyclerView.setAdapter(adapter);

        // Cargar datos iniciales
        obtenerReportes();

        // -------------------------------
        // BOTÓN: Crear nuevo reporte
        // -------------------------------
        findViewById(R.id.imgButton_crearReporte).setOnClickListener(v -> {
            Intent intent = new Intent(Lista_reportes.this, Form_reportes.class);
            formLauncher.launch(intent);
        });

        // -------------------------------
        // BOTÓN: Volver al menú
        // -------------------------------
        ImageView btnVolver = findViewById(R.id.imgButton_VolverInicio);
        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(Lista_reportes.this, Menu.class));
            finish();
        });

        // -------------------------------
        // BUSCAR TEXTO
        // -------------------------------
        EditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarLista(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // -------------------------------
        // FILTRO DE FECHA
        // -------------------------------
        EditText etFecha = findViewById(R.id.etFiltrarFecha);
        ImageButton btnLimpiarFiltro = findViewById(R.id.btnLimpiarFiltro);

        etFecha.setOnClickListener(v -> mostrarDatePicker(etFecha, btnLimpiarFiltro));

        btnLimpiarFiltro.setOnClickListener(v -> {
            etFecha.setText("");
            btnLimpiarFiltro.setVisibility(android.view.View.GONE);
            adapter.filtrar(listaReportes);
        });
    }


    // ---------------------------------------------------
    // FILTRAR FECHA
    // ---------------------------------------------------
    private void mostrarDatePicker(EditText etFecha, ImageButton btnLimpiar) {
        final Calendar c = Calendar.getInstance();
        int año = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fechaSel = year + "-" + (month + 1) + "-" + dayOfMonth;
                    etFecha.setText(fechaSel);
                    btnLimpiar.setVisibility(android.view.View.VISIBLE);
                    filtrarFecha(fechaSel);
                },
                año, mes, dia
        );

        dp.show();
    }


    // ---------------------------------------------------
    // FILTRADO POR FECHA
    // ---------------------------------------------------
    private void filtrarFecha(String fecha) {
        List<ItemReporte> filtrada = new ArrayList<>();

        for (ItemReporte i : listaReportes) {
            if (i.getFecha().contains(fecha)) {
                filtrada.add(i);
            }
        }

        adapter.filtrar(filtrada);
    }

    // ---------------------------------------------------
    // FILTRADO POR TEXTO
    // ---------------------------------------------------
    private void filtrarLista(String texto) {
        List<ItemReporte> filtrada = new ArrayList<>();

        for (ItemReporte item : listaReportes) {
            if (item.getNombreUsuario().toLowerCase().contains(texto.toLowerCase()) ||
                    item.getDescripcion().toLowerCase().contains(texto.toLowerCase()) ||
                    item.getLugar().toLowerCase().contains(texto.toLowerCase()))
            {
                filtrada.add(item);
            }
        }

        adapter.filtrar(filtrada);
    }


    // ---------------------------------------------------
    // CONSULTA A LA API
    // ---------------------------------------------------
    private void obtenerReportes() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String token = prefsManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "⚠ Debes iniciar sesión primero", Toast.LENGTH_LONG).show();
            return;
        }

        int page = 1, perPage = 20;
        String url = URL_API + "?page=" + page + "&perPage=" + perPage;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray datos = response.getJSONArray("data");
                        listaReportes.clear();

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            String cargo = "No disponible";
                            if (obj.has("cargo") && !obj.isNull("cargo")) {
                                if (obj.get("cargo") instanceof String) {
                                    cargo = obj.getString("cargo");
                                } else if (obj.get("cargo") instanceof JSONObject) {
                                    cargo = obj.getJSONObject("cargo").optString("nombre", "No disponible");
                                }
                            }

                            String cedula = obj.optString("cedula", "");

                            ItemReporte item = new ItemReporte(
                                    obj.getInt("idReporte"),
                                    obj.getString("nombreUsuario"),
                                    cargo,
                                    cedula,
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

                        // Mostrar total
                        binding.tvTotalReportes.setText("Total de reportes: " + listaReportes.size());

                    } catch (Exception e) {
                        Toast.makeText(this, "Error parseando: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error API: " + error.getMessage(), Toast.LENGTH_LONG).show()
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

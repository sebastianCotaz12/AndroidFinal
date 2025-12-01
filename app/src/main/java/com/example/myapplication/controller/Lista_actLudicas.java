package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Lista_actLudicas extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_actLudica adapter;
    private List<Item_actLudicas> listaOriginal = new ArrayList<>();
    private List<Item_actLudicas> listaFiltrada = new ArrayList<>();
    private PrefsManager prefsManager;

    // Variables para el filtrado
    private TextInputEditText etFiltrarFecha;
    private ImageButton btnLimpiarFiltro;
    private EditText etBuscar;
    private TextView tvTotalActividades;

    // Variables para el calendario
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private final String URL_API = "https://unreproaching-rancorously-evelina.ngrok-free.dev/listarActis";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_act_ludicas);

        prefsManager = new PrefsManager(this);

        // Inicializar vistas del filtro
        inicializarVistasFiltro();
        configurarCalendario();
        configurarEventosFiltro();

        recyclerView = findViewById(R.id.recyclerViewListaChequeo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter_actLudica(this, listaFiltrada);
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

    private void inicializarVistasFiltro() {
        etFiltrarFecha = findViewById(R.id.etFiltrarFecha);
        btnLimpiarFiltro = findViewById(R.id.btnLimpiarFiltro);
        etBuscar = findViewById(R.id.etBuscar);
        tvTotalActividades = findViewById(R.id.tvTotalActividades);
    }

    private void configurarCalendario() {
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String fechaSeleccionada = dateFormatter.format(calendar.getTime());
                etFiltrarFecha.setText(fechaSeleccionada);
                filtrarPorFecha(fechaSeleccionada);
                btnLimpiarFiltro.setVisibility(View.VISIBLE);
            }
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
        // Al hacer clic en el campo de fecha, mostrar calendario
        etFiltrarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Botón para limpiar filtro
        btnLimpiarFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarFiltroFecha();
            }
        });

        // Búsqueda por texto
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarActividades(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filtrarPorFecha(String fecha) {
        listaFiltrada.clear();

        for (Item_actLudicas actividad : listaOriginal) {
            // Comparar solo la parte de la fecha (sin la hora)
            String fechaActividad = actividad.getFechaActividad();
            if (fechaActividad != null && fechaActividad.contains("T")) {
                String soloFecha = fechaActividad.split("T")[0];
                if (soloFecha.equals(fecha)) {
                    listaFiltrada.add(actividad);
                }
            }
        }

        adapter.notifyDataSetChanged();
        actualizarContador();

        if (listaFiltrada.isEmpty()) {
            Toast.makeText(this, "No hay actividades para la fecha seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void filtrarActividades(String texto) {
        listaFiltrada.clear();

        if (texto.isEmpty()) {
            // Si no hay texto de búsqueda, aplicar solo filtro de fecha si existe
            String fechaFiltro = etFiltrarFecha.getText().toString();
            if (!fechaFiltro.isEmpty()) {
                filtrarPorFecha(fechaFiltro);
            } else {
                listaFiltrada.addAll(listaOriginal);
            }
        } else {
            // Filtrar por texto y fecha si existe
            String fechaFiltro = etFiltrarFecha.getText().toString();
            for (Item_actLudicas actividad : listaOriginal) {
                boolean coincideTexto = actividad.getNombreActividad().toLowerCase().contains(texto.toLowerCase()) ||
                        actividad.getDescripcion().toLowerCase().contains(texto.toLowerCase()) ||
                        actividad.getNombreUsuario().toLowerCase().contains(texto.toLowerCase());

                boolean coincideFecha = fechaFiltro.isEmpty() ||
                        (actividad.getFechaActividad() != null &&
                                actividad.getFechaActividad().contains(fechaFiltro));

                if (coincideTexto && coincideFecha) {
                    listaFiltrada.add(actividad);
                }
            }
        }

        adapter.notifyDataSetChanged();
        actualizarContador();
    }

    private void limpiarFiltroFecha() {
        etFiltrarFecha.setText("");
        btnLimpiarFiltro.setVisibility(View.GONE);

        // Restaurar lista completa o aplicar filtro de texto si existe
        String textoBusqueda = etBuscar.getText().toString();
        if (textoBusqueda.isEmpty()) {
            listaFiltrada.clear();
            listaFiltrada.addAll(listaOriginal);
        } else {
            filtrarActividades(textoBusqueda);
        }

        adapter.notifyDataSetChanged();
        actualizarContador();
    }

    private void actualizarContador() {
        if (tvTotalActividades != null) {
            String texto = "Total de actividades: " + listaFiltrada.size();
            tvTotalActividades.setText(texto);
        }
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
                        listaOriginal.clear();
                        JSONArray datos = response.getJSONArray("data");

                        Log.d("ACTLUDICAS_API", "Respuesta recibida: " + datos.length() + " elementos");

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            Log.d("ACTLUDICAS_API", "Elemento " + i + ": " + obj.toString());

                            Item_actLudicas item = new Item_actLudicas(
                                    obj.getInt("id"),
                                    obj.getString("nombreUsuario"),
                                    obj.getString("nombreActividad"),
                                    obj.getString("fechaActividad"),
                                    obj.getString("descripcion"),
                                    obj.optString("archivoAdjunto", ""),
                                    obj.optString("imagenVideo", "")
                            );
                            listaOriginal.add(item);
                        }

                        // Inicializar lista filtrada con todos los datos
                        listaFiltrada.clear();
                        listaFiltrada.addAll(listaOriginal);
                        adapter.notifyDataSetChanged();
                        actualizarContador();

                        if (listaOriginal.isEmpty()) {
                            Toast.makeText(this, "No hay actividades disponibles", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Actividades cargadas: " + listaOriginal.size(), Toast.LENGTH_SHORT).show();
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
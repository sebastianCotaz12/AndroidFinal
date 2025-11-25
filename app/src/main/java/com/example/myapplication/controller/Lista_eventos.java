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
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class Lista_eventos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_eventos adapter;
    private List<Item_eventos> listaOriginal = new ArrayList<>();
    private List<Item_eventos> listaFiltrada = new ArrayList<>();

    private PrefsManager prefsManager;

    // FILTROS
    private TextInputEditText etFiltrarFecha;
    private ImageButton btnLimpiarFiltro;
    private EditText etBuscar;
    private TextView tvTotalEventos;

    // FECHA
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private final String URL_API = "https://unreproaching-rancorously-evelina.ngrok-free.app/eventos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        prefsManager = new PrefsManager(this);

        inicializarVistas();
        configurarCalendario();
        configurarEventosFiltro();

        recyclerView = findViewById(R.id.recyclerViewEventos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter_eventos(this, listaFiltrada);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        ImageButton btnCrear = findViewById(R.id.imgButtonCrearEvento);
        btnCrear.setOnClickListener(v -> {
            startActivity(new Intent(Lista_eventos.this, Form_eventos.class));
        });

        ImageView btnVolver = findViewById(R.id.imgButton_VolverInicio);
        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(Lista_eventos.this, Menu.class));
            finish();
        });

        obtenerEventos();
    }

    private void inicializarVistas() {
        etFiltrarFecha = findViewById(R.id.etFiltrarFechaEvento);
        btnLimpiarFiltro = findViewById(R.id.btnLimpiarFiltroEvento);
        etBuscar = findViewById(R.id.etBuscarEvento);
        tvTotalEventos = findViewById(R.id.tvTotalEventos);
    }

    private void configurarCalendario() {
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String fecha = dateFormatter.format(calendar.getTime());

                    etFiltrarFecha.setText(fecha);
                    filtrarPorFecha(fecha);

                    btnLimpiarFiltro.setVisibility(View.VISIBLE);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    private void configurarEventosFiltro() {
        etFiltrarFecha.setOnClickListener(v -> datePickerDialog.show());

        btnLimpiarFiltro.setOnClickListener(v -> limpiarFiltroFecha());

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarEventos(s.toString());
            }
        });
    }

    private void filtrarPorFecha(String fecha) {
        listaFiltrada.clear();

        for (Item_eventos ev : listaOriginal) {
            if (ev.getFechaActividad() != null && ev.getFechaActividad().contains(fecha)) {
                listaFiltrada.add(ev);
            }
        }

        adapter.notifyDataSetChanged();
        actualizarContador();
    }

    private void filtrarEventos(String texto) {
        listaFiltrada.clear();
        texto = texto.toLowerCase();

        String fechaFiltro = etFiltrarFecha.getText().toString();

        for (Item_eventos ev : listaOriginal) {
            boolean coincideTexto =
                    ev.getTitulo().toLowerCase().contains(texto) ||
                            ev.getDescripcion().toLowerCase().contains(texto) ||
                            ev.getNombreUsuario().toLowerCase().contains(texto);

            boolean coincideFecha =
                    fechaFiltro.isEmpty() ||
                            (ev.getFechaActividad() != null && ev.getFechaActividad().contains(fechaFiltro));

            if (coincideTexto && coincideFecha) {
                listaFiltrada.add(ev);
            }
        }

        adapter.notifyDataSetChanged();
        actualizarContador();
    }

    private void limpiarFiltroFecha() {
        etFiltrarFecha.setText("");
        btnLimpiarFiltro.setVisibility(View.GONE);

        filtrarEventos(etBuscar.getText().toString());
    }

    private void actualizarContador() {
        tvTotalEventos.setText("Total de eventos: " + listaFiltrada.size());
    }

    private void obtenerEventos() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String token = prefsManager.getToken();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        listaOriginal.clear();

                        JSONArray datos = response.getJSONArray("data");

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject obj = datos.getJSONObject(i);

                            Item_eventos item = new Item_eventos(
                                    obj.getInt("id"),
                                    obj.optInt("idUsuario", 0),
                                    obj.optString("nombreUsuario", "Usuario"),
                                    obj.optString("titulo", ""),
                                    obj.optString("fechaActividad", ""),
                                    obj.optString("descripcion", ""),
                                    obj.optString("imagen", ""),
                                    obj.optString("archivo", ""),
                                    obj.optInt("idEmpresa", 0)
                            );

                            listaOriginal.add(item);
                        }

                        listaFiltrada.clear();
                        listaFiltrada.addAll(listaOriginal);

                        adapter.notifyDataSetChanged();
                        actualizarContador();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error en la API: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + token);
                return h;
            }
        };

        queue.add(request);
    }
}

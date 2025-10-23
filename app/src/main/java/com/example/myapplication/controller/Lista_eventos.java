package com.example.myapplication.controller;

import android.annotation.SuppressLint;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lista_eventos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter_eventos adapter;
    private final List<Item_eventos> lista = new ArrayList<>();
    private PrefsManager prefsManager;

    // URL del backend (ajústala si es necesario)
    private static final String URL_API = "https://backsst.onrender.com/eventos";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        prefsManager = new PrefsManager(this);

        recyclerView = findViewById(R.id.recyclerViewEventos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter_eventos(this, lista);
        recyclerView.setAdapter(adapter);

        // Ajuste de márgenes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botón para crear un nuevo evento
        ImageButton btnCrear = findViewById(R.id.imgButton_crearlista);
        btnCrear.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_eventos.this, Form_eventos.class);
            startActivity(intent);
        });
        // 🔹 Botón de regresar al inicio de sesión
        ImageView btnVolverLogin = findViewById(R.id.imgButton_VolverInicio);
        btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Lista_eventos.this, Menu.class);
            startActivity(intent);
            finish();
        });

        // Cargar lista de eventos
        obtenerEventos();
    }

    private void obtenerEventos() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String token = prefsManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "⚠️ Debes iniciar sesión primero", Toast.LENGTH_LONG).show();
            return;
        }

        JsonArrayRequest request = new JsonArrayRequest(
                URL_API,
                response -> {
                    try {
                        lista.clear();
                        Log.d("EVENTOS_API", "Respuesta recibida: " + response.length() + " elementos");

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            // DEBUG: Ver el JSON completo
                            Log.d("EVENTOS_API", "Elemento " + i + ": " + obj.toString());

                            // Obtener campos principales
                            int id = obj.getInt("id");
                            int idUsuario = obj.optInt("idUsuario");
                            String titulo = obj.optString("titulo", "Sin título");
                            String fechaActividad = obj.optString("fechaActividad", "");
                            String descripcion = obj.optString("descripcion", "");
                            String imagen = obj.optString("imagen", "");
                            String archivo = obj.optString("archivo", "");
                            int idEmpresa = obj.optInt("idEmpresa");

                            // Obtener nombre de usuario desde el objeto "usuario"
                            String nombreUsuario = "Usuario no disponible";
                            if (obj.has("usuario") && !obj.isNull("usuario")) {
                                JSONObject usuarioObj = obj.getJSONObject("usuario");
                                String nombre = usuarioObj.optString("nombre", "");
                                String apellido = usuarioObj.optString("apellido", "");

                                if (!nombre.isEmpty() && !apellido.isEmpty()) {
                                    nombreUsuario = nombre + " " + apellido;
                                } else if (!nombre.isEmpty()) {
                                    nombreUsuario = nombre;
                                } else {
                                    nombreUsuario = usuarioObj.optString("nombreUsuario", "Usuario no disponible");
                                }
                            }

                            // DEBUG: Verificar campos específicos
                            Log.d("EVENTOS_API", "Usuario: " + nombreUsuario + ", Fecha: " + fechaActividad + ", Título: " + titulo);

                            // Crear objeto para el RecyclerView
                            Item_eventos item = new Item_eventos(
                                    id,
                                    idUsuario,
                                    nombreUsuario,
                                    titulo,
                                    fechaActividad,
                                    descripcion,
                                    imagen,
                                    archivo,
                                    idEmpresa
                            );

                            lista.add(item);
                        }
                        adapter.notifyDataSetChanged();

                        if (lista.isEmpty()) {
                            Toast.makeText(this, "No hay eventos disponibles", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Eventos cargados: " + lista.size(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("EVENTOS_API", "Error al procesar datos: " + e.getMessage());
                        Toast.makeText(this, "Error al procesar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("EVENTOS_API", "Error en la petición: " + error.getMessage());
                    Toast.makeText(this, "Error al obtener eventos: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
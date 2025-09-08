package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityFormGestionEppBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_gestionEpp extends AppCompatActivity {

    private ActivityFormGestionEppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormGestionEppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGuardarEpp.setOnClickListener(v -> guardarGestion());
    }

    private void guardarGestion() {
        // Obtener valores de los EditText
        String idEpp = binding.etIdEpp.getText().toString().trim();
        String nombre = binding.etNombre.getText().toString().trim();
        String apellido = binding.etApellido.getText().toString().trim();
        String cedula = binding.etCedula.getText().toString().trim();
        String cargo = binding.etCargo.getText().toString().trim();
        String productos = binding.etProductos.getText().toString().trim();
        String cantidad = binding.etCantidad.getText().toString().trim();
        String importancia = binding.etImportancia.getText().toString().trim();
        String estado = binding.etEstado.getText().toString().trim();
        String fechaCreacion = binding.etFechaCreacion.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty() ||
                cargo.isEmpty() || productos.isEmpty() || cantidad.isEmpty() ||
                importancia.isEmpty() || estado.isEmpty() || fechaCreacion.isEmpty()) {

            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!cedula.matches("\\d+")) {
            Toast.makeText(this, "La cédula solo debe contener números.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cantidad.matches("\\d+")) {
            Toast.makeText(this, "La cantidad debe ser numérica.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto de gestión
        Crear_gestionEpp nuevaGestion = new Crear_gestionEpp();
        nuevaGestion.setIdUsuario(1); // Temporal
        nuevaGestion.setNombre(nombre);
        nuevaGestion.setApellido(apellido);
        nuevaGestion.setCedula(cedula);
        nuevaGestion.setCargo(cargo);
        nuevaGestion.setProductos(productos);
        nuevaGestion.setCantidad(cantidad);
        nuevaGestion.setImportancia(importancia);
        nuevaGestion.setEstado(estado);
        nuevaGestion.setFechaCreacion(fechaCreacion);

        // Token (debería obtenerse dinámicamente)
        String token = "TOKEN_JWT_VALIDO";

        ApiService apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<ApiResponse<Crear_gestionEpp>> call = apiService.crearGestion(nuevaGestion);

        call.enqueue(new Callback<ApiResponse<Crear_gestionEpp>>() {
            @Override
            public void onResponse(Call<ApiResponse<Crear_gestionEpp>> call, Response<ApiResponse<Crear_gestionEpp>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Form_gestionEpp.this, "Guardado: " + response.body().getMsj(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Form_gestionEpp.this, Lista_gestionEpp.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Form_gestionEpp.this, "Error al guardar la gestión.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Crear_gestionEpp>> call, Throwable t) {
                Toast.makeText(Form_gestionEpp.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

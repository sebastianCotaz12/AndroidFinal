package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityFormReportesBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_reportes extends AppCompatActivity {

    private ActivityFormReportesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Evento para guardar el reporte
        binding.btnEnviarReporte.setOnClickListener(v -> guardarReporte());
    }

    private void guardarReporte() {
        // Obtener valores de los EditText
        String nombreUsuario = binding.etNombreUsuarioR.getText().toString().trim();
        String cargo = binding.etCargo.getText().toString().trim();
        String cedula = binding.etCedula.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String lugar = binding.etLugar.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String imagen = binding.etImagen.getText().toString().trim();
        String archivos = binding.etArchivos.getText().toString().trim();
        String estado = binding.etEstado.getText().toString().trim();

        // Validaciones
        if (nombreUsuario.isEmpty() || cargo.isEmpty() || cedula.isEmpty() ||
                fecha.isEmpty() || lugar.isEmpty() || descripcion.isEmpty() || estado.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!cedula.matches("\\d+")) {
            Toast.makeText(this, "La cédula debe ser numérica.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto reporte
        Crear_reportes nuevoReporte = new Crear_reportes();
        nuevoReporte.setIdUsuario(1); // Temporal hasta login
        nuevoReporte.setNombreUsuario(nombreUsuario);
        nuevoReporte.setCargo(cargo);
        nuevoReporte.setCedula(cedula);
        nuevoReporte.setFecha(fecha);
        nuevoReporte.setLugar(lugar);
        nuevoReporte.setDescripcion(descripcion);
        nuevoReporte.setImagen(imagen);
        nuevoReporte.setArchivos(archivos);
        nuevoReporte.setEstado(estado);

        // Token temporal (reemplazar luego con el del login)
        String token = "TOKEN_JWT_VALIDO";

        ApiService apiService = ApiClient.getClient(token).create(ApiService.class);
        Call<ApiResponse<Crear_reportes>> call = apiService.crearReporte(nuevoReporte);

        call.enqueue(new Callback<ApiResponse<Crear_reportes>>() {
            @Override
            public void onResponse(Call<ApiResponse<Crear_reportes>> call, Response<ApiResponse<Crear_reportes>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Form_reportes.this, "Reporte guardado: " + response.body().getMsj(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Form_reportes.this, Lista_reportes.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Form_reportes.this, "Error al guardar el reporte.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Crear_reportes>> call, Throwable t) {
                Toast.makeText(Form_reportes.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

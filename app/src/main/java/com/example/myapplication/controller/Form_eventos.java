package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityFormEventosBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_eventos extends AppCompatActivity {

    private ActivityFormEventosBinding binding;
    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    private Uri imagenUri = null;

    // Selector de imagen
    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    if (imagenUri != null) {
                        binding.ivPreview.setVisibility(android.view.View.VISIBLE);
                        binding.ivPreview.setImageURI(imagenUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormEventosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // --- Verificar sesión ---
        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        // --- Listeners ---
        binding.etFecha.setOnClickListener(v -> abrirDatePicker());
        binding.ivAdjuntar.setOnClickListener(v -> seleccionarImagen());
        binding.btnEnviarEvidencia.setOnClickListener(v -> guardarEventosBase64());
    }

    private void abrirDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, day);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    binding.etFecha.setText(sdf.format(selected.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        seleccionarImagenLauncher.launch(Intent.createChooser(intent, "Seleccionar Imagen"));
    }

    private void guardarEventosBase64() {
        String token = prefsManager.getToken();

        String titulo = binding.etTituloEvento.getText().toString().trim();
        String fecha_actividad = binding.etFecha.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();

        if (titulo.isEmpty() || fecha_actividad.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        if (imagenUri == null) {
            Toast.makeText(this, "⚠ Selecciona una imagen.", Toast.LENGTH_LONG).show();
            return;
        }

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "🚫 No hay token. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            return;
        }

        try {
            // Convertir imagen a Base64
            InputStream inputStream = getContentResolver().openInputStream(imagenUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String imagenBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

            // Obtener extensión
            String extension = getContentResolver().getType(imagenUri).split("/")[1];

            // Llamada API
            ApiService apiService = ApiClient.getClient(prefsManager).create(ApiService.class);
            Log.d("TOKEN_DEBUG", "Token usado al crear evento: " + token);

            Call<ApiResponse<Object>> call = apiService.crearEventoBase64(
                    titulo,
                    fecha_actividad,
                    descripcion,
                    imagenBase64,
                    extension
            );

            call.enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(Form_eventos.this, "✅ " + response.body().getMsj(), Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String errorMsg = "⚠ Error API (" + response.code() + ")";
                        try {
                            if (response.errorBody() != null)
                                errorMsg += " → " + response.errorBody().string();
                        } catch (Exception ignored) {}
                        Log.e("EVENTO_ERR", errorMsg);
                        Toast.makeText(Form_eventos.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    Log.e("EVENTO_FAIL", "Error conexión: " + t.getMessage());
                    Toast.makeText(Form_eventos.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error leyendo la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
package com.example.myapplication.controller;

import android.app.DatePickerDialog;
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
import com.example.myapplication.databinding.ActivityFormActLudicasBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_actLudicas extends AppCompatActivity {

    private ActivityFormActLudicasBinding binding;
    private PrefsManager prefsManager;
    private SesionManager sesionManager;
    private Uri imagenUri = null;

    // Selector de imagen
    private final ActivityResultLauncher<android.content.Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    if (imagenUri != null) {
                        binding.ivPreview.setVisibility(android.view.View.VISIBLE);
                        binding.ivPreview.setImageURI(imagenUri);
                        binding.tvImagenSeleccionada.setText("Imagen seleccionada ✅");
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormActLudicasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // --- Verificar sesión activa ---
        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        // --- Mostrar nombre y cargo desde login ---
        String nombre = prefsManager.getNombreUsuario();
        String cargo = prefsManager.getCargo(); // asegúrate que este método existe en PrefsManager

        binding.etNombreUsuario.setText(nombre != null ? nombre : "No disponible");
        binding.etCargoUsuario.setText(cargo != null ? cargo : "No disponible");

        // Evitar edición manual
        binding.etNombreUsuario.setEnabled(false);
        binding.etCargoUsuario.setEnabled(false);

        // --- Eventos ---
        binding.etFecha.setOnClickListener(v -> abrirDatePicker());
        binding.ivAdjuntar.setOnClickListener(v -> seleccionarImagen());
        binding.btnEnviarEvidencia.setOnClickListener(v -> guardarActividadBase64());
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
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        seleccionarImagenLauncher.launch(android.content.Intent.createChooser(intent, "Seleccionar Imagen"));
    }

    private void guardarActividadBase64() {
        int idUsuario = prefsManager.getIdUsuario();
        String token = prefsManager.getToken();

        String nombreActividad = binding.etNombreActividad.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();

        if (nombreActividad.isEmpty() || fecha.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠️ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        if (imagenUri == null) {
            Toast.makeText(this, "⚠️ Selecciona una imagen.", Toast.LENGTH_LONG).show();
            return;
        }

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "🚫 No hay token. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(imagenUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String imagenBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

            String extension = getContentResolver().getType(imagenUri).split("/")[1];

            ApiService apiService = ApiClient.getClient(prefsManager).create(ApiService.class);
            Log.d("TOKEN_DEBUG", "Token usado al crear actividad: " + token);

            Call<ApiResponse<Object>> call = apiService.crearActividadBase64(
                    idUsuario,
                    nombreActividad,
                    fecha,
                    descripcion,
                    imagenBase64,
                    extension
            );

            call.enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(Form_actLudicas.this, "✅ " + response.body().getMsj(), Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String errorMsg = "⚠️ Error API (" + response.code() + ")";
                        try {
                            if (response.errorBody() != null)
                                errorMsg += " → " + response.errorBody().string();
                        } catch (Exception ignored) {}
                        Log.e("ACTLUDICA_ERR", errorMsg);
                        Toast.makeText(Form_actLudicas.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    Log.e("ACTLUDICA_FAIL", "Error conexión: " + t.getMessage());
                    Toast.makeText(Form_actLudicas.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error leyendo la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}

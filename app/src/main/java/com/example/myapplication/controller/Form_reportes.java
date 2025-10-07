package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityFormReportesBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_reportes extends AppCompatActivity {

    private ActivityFormReportesBinding binding;
    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    private Uri imagenUri = null;
    private Uri archivoUri = null;

    // ---- Selectores ----
    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    if (imagenUri != null)
                        binding.tvImagenSeleccionada.setText(imagenUri.getLastPathSegment());
                }
            });

    private final ActivityResultLauncher<Intent> seleccionarArchivoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    archivoUri = result.getData().getData();
                    if (archivoUri != null)
                        binding.tvArchivoSeleccionado.setText(archivoUri.getLastPathSegment());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // --- Verificar sesión ---
        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        // --- Autocompletar datos de sesión ---
        binding.etCargo.setText(prefsManager.getNombreArea());
        binding.etCargo.setEnabled(false);

        binding.etCedula.setText(String.valueOf(prefsManager.getIdUsuario()));
        binding.etCedula.setEnabled(false);

        // --- Spinner de estado ---
        String[] opcionesEstado = {"Pendiente", "En Proceso", "Realizado"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesEstado);
        binding.spEstado.setAdapter(adapterEstado);

        // --- Listeners ---
        binding.etFecha.setOnClickListener(v -> abrirDatePicker());
        binding.btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());
        binding.btnSeleccionarArchivo.setOnClickListener(v -> seleccionarArchivo());
        binding.btnEnviarReporte.setOnClickListener(v -> guardarReporteMultipart());
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

    private void seleccionarArchivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        seleccionarArchivoLauncher.launch(Intent.createChooser(intent, "Seleccionar Archivo"));
    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(value != null ? value : "", MediaType.parse("text/plain"));
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        if (fileUri == null) return null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            File tempFile = File.createTempFile("upload_", null, getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);

            inputStream.close();
            outputStream.close();

            RequestBody requestFile = RequestBody.create(tempFile, MediaType.parse("multipart/form-data"));
            return MultipartBody.Part.createFormData(partName, tempFile.getName(), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparando archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private void guardarReporteMultipart() {
        String nombreUsuario = prefsManager.getNombreUsuario();
        int idUsuario = prefsManager.getIdUsuario();
        int idEmpresa = prefsManager.getIdEmpresa();
        String token = prefsManager.getToken();

        String cargo = binding.etCargo.getText().toString().trim();
        String cedula = binding.etCedula.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String lugar = binding.etLugar.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String estado = binding.spEstado.getSelectedItem().toString();

        if (fecha.isEmpty() || lugar.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠️ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "🚫 No hay token. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            return;
        }

        // 🔹 Cliente con token JWT
        ApiService apiService = ApiClient.getClient(prefsManager).create(ApiService.class);
        Log.d("TOKEN_DEBUG", "Token usado al crear reporte: " + token);

        MultipartBody.Part imagenPart = prepareFilePart("imagen", imagenUri);
        MultipartBody.Part archivoPart = prepareFilePart("archivos", archivoUri);

        Call<ApiResponse<Crear_reportes>> call = apiService.crearReporteMultipart(
                createPartFromString(String.valueOf(idUsuario)),
                createPartFromString(String.valueOf(idEmpresa)),
                createPartFromString(nombreUsuario),
                createPartFromString(cargo),
                createPartFromString(cedula),
                createPartFromString(fecha),
                createPartFromString(lugar),
                createPartFromString(descripcion),
                createPartFromString(estado),
                imagenPart,
                archivoPart
        );

        call.enqueue(new Callback<ApiResponse<Crear_reportes>>() {
            @Override
            public void onResponse(Call<ApiResponse<Crear_reportes>> call, Response<ApiResponse<Crear_reportes>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Crear_reportes> apiResponse = response.body();
                    Toast.makeText(Form_reportes.this, "✅ " + apiResponse.getMsj(), Toast.LENGTH_LONG).show();
                    Log.d("REPORTE_OK", "Reporte creado correctamente: " + apiResponse.getMsj());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "⚠️ Error API (" + response.code() + ")";
                    try {
                        if (response.errorBody() != null)
                            errorMsg += " → " + response.errorBody().string();
                    } catch (Exception ignored) {}
                    Log.e("REPORTE_ERR", errorMsg);
                    Toast.makeText(Form_reportes.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Crear_reportes>> call, Throwable t) {
                Log.e("REPORTE_FAIL", "Error conexión: " + t.getMessage());
                Toast.makeText(Form_reportes.this, "🚫 Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

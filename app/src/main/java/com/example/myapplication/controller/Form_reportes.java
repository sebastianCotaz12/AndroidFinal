package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private Uri imagenUri = null;
    private Uri archivoUri = null;

    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    if (imagenUri != null) {
                        binding.tvImagenSeleccionada.setText(imagenUri.getLastPathSegment());
                    }
                }
            });

    private final ActivityResultLauncher<Intent> seleccionarArchivoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    archivoUri = result.getData().getData();
                    if (archivoUri != null) {
                        binding.tvArchivoSeleccionado.setText(archivoUri.getLastPathSegment());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        // Autocompletar campos
        binding.etCargo.setText(String.valueOf(prefsManager.getIdArea()));
        binding.etCargo.setEnabled(false);

        binding.etCedula.setText(String.valueOf(prefsManager.getIdUsuario()));
        binding.etCedula.setEnabled(false);

        // Spinner estado
        String[] opcionesEstado = {"Pendiente", "En Proceso", "Realizado"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesEstado);
        binding.spEstado.setAdapter(adapterEstado);

        // Seleccionar imagen
        binding.btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            seleccionarImagenLauncher.launch(Intent.createChooser(intent, "Seleccionar Imagen"));
        });

        // Seleccionar archivo
        binding.btnSeleccionarArchivo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            seleccionarArchivoLauncher.launch(Intent.createChooser(intent, "Seleccionar Archivo"));
        });

        // Seleccionar fecha
        binding.etFecha.setOnClickListener(v -> abrirDatePicker());

        // Enviar reporte
        binding.btnEnviarReporte.setOnClickListener(v -> guardarReporteMultipart());
    }

    private void abrirDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar sel = Calendar.getInstance();
                    sel.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    binding.etFecha.setText(sdf.format(sel.getTime()));
                },
                year, month, day
        );
        datePicker.show();
    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(value, MediaType.parse("multipart/form-data"));
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        if (fileUri == null) return null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) return null;

            File tempFile = File.createTempFile("upload", null, getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(tempFile, MediaType.parse("multipart/form-data"));
            return MultipartBody.Part.createFormData(partName, tempFile.getName(), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void guardarReporteMultipart() {
        int idUsuario = prefsManager.getIdUsuario();
        String nombreUsuario = prefsManager.getNombreUsuario();
        String token = prefsManager.getToken();

        if (idUsuario == -1 || token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Error: no se encontraron datos de sesión. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            return;
        }

        String cargo = binding.etCargo.getText().toString().trim();
        String cedula = binding.etCedula.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String lugar = binding.etLugar.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String estado = binding.spEstado.getSelectedItem().toString();

        if (fecha.isEmpty() || lugar.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService apiService = ApiClient.getClient(token).create(ApiService.class);

        MultipartBody.Part imagenPart = prepareFilePart("imagen", imagenUri);
        MultipartBody.Part archivoPart = prepareFilePart("archivo", archivoUri);

        Call<ApiResponse<Crear_reportes>> call = apiService.crearReporteMultipart(
                createPartFromString(String.valueOf(idUsuario)),
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
                    Toast.makeText(Form_reportes.this, "Reporte guardado: " + response.body().getMsj(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // 🔹 avisar a Lista_reportes que recargue
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

package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_eventos extends AppCompatActivity {

    private ActivityFormEventosBinding binding;
    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    private Uri imagenUri = null;
    private Uri archivoUri = null;

    // Selector de imagen
    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    if (imagenUri != null) {
                        binding.ivPreview.setVisibility(android.view.View.VISIBLE);
                        binding.llPlaceholder.setVisibility(android.view.View.GONE);
                        binding.ivPreview.setImageURI(imagenUri);
                        binding.tvImagenSeleccionada.setText("Imagen seleccionada");
                    }
                }
            });

    // Selector de archivo
    private final ActivityResultLauncher<Intent> seleccionarArchivoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    archivoUri = result.getData().getData();
                    if (archivoUri != null) {
                        String fileName = obtenerNombreArchivo(archivoUri);
                        binding.tvNombreArchivo.setText(fileName);
                        binding.tvTipoArchivo.setVisibility(android.view.View.VISIBLE);
                        binding.tvTipoArchivo.setText(obtenerTipoArchivo(fileName));
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
        binding.cardImagen.setOnClickListener(v -> seleccionarImagen());
        binding.cardArchivo.setOnClickListener(v -> seleccionarArchivo());
        binding.btnEnviarEvidencia.setOnClickListener(v -> guardarEventoConArchivos());
        binding.btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(Form_eventos.this, Lista_eventos.class);
            startActivity(intent);
            finish();
        });
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
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        seleccionarArchivoLauncher.launch(Intent.createChooser(intent, "Seleccionar Archivo"));
    }

    private String obtenerNombreArchivo(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String obtenerTipoArchivo(String fileName) {
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return "PDF Document";
        } else if (fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".docx")) {
            return "Word Document";
        } else if (fileName.toLowerCase().endsWith(".txt")) {
            return "Text File";
        } else {
            return "Document";
        }
    }

    // 🔹 Método para subir evento con imagen y archivo
    private void guardarEventoConArchivos() {
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

        try {
            // 🔹 Convertir la imagen seleccionada a bytes
            InputStream imagenInputStream = getContentResolver().openInputStream(imagenUri);
            byte[] imagenBytes = new byte[imagenInputStream.available()];
            imagenInputStream.read(imagenBytes);
            imagenInputStream.close();

            // 🔹 Crear el cuerpo del archivo de imagen
            RequestBody requestImagen = RequestBody.create(imagenBytes, MediaType.parse("image/*"));
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen", "evento.jpg", requestImagen);

            // 🔹 Crear los demás campos
            RequestBody tituloBody = RequestBody.create(titulo, MultipartBody.FORM);
            RequestBody fechaBody = RequestBody.create(fecha_actividad, MultipartBody.FORM);
            RequestBody descripcionBody = RequestBody.create(descripcion, MultipartBody.FORM);

            // 🔹 Llamada a la API
            Call<ApiResponse<Object>> call;
            ApiService apiService = ApiClient.getClient(prefsManager).create(ApiService.class);

            if (archivoUri != null) {
                // 🔹 Si hay archivo, prepararlo también
                InputStream archivoInputStream = getContentResolver().openInputStream(archivoUri);
                byte[] archivoBytes = new byte[archivoInputStream.available()];
                archivoInputStream.read(archivoBytes);
                archivoInputStream.close();

                String archivoNombre = obtenerNombreArchivo(archivoUri);
                MediaType archivoMediaType = obtenerMediaTypeArchivo(archivoNombre);

                RequestBody requestArchivo = RequestBody.create(archivoBytes, archivoMediaType);
                MultipartBody.Part archivoPart = MultipartBody.Part.createFormData("archivo", archivoNombre, requestArchivo);

                // 🔹 Llamada con imagen y archivo
                call = apiService.crearEventoMultipart(
                        tituloBody,
                        fechaBody,
                        descripcionBody,
                        imagenPart,
                        archivoPart
                );
            } else {
                // 🔹 Llamada solo con imagen - pasar null para el archivo
                call = apiService.crearEventoMultipart(
                        tituloBody,
                        fechaBody,
                        descripcionBody,
                        imagenPart,
                        null  // Pasar null cuando no hay archivo
                );
            }

            call.enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(Form_eventos.this, "✅ " + response.body().getMsj(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Error procesando archivos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private MediaType obtenerMediaTypeArchivo(String fileName) {
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return MediaType.parse("application/pdf");
        } else if (fileName.toLowerCase().endsWith(".doc")) {
            return MediaType.parse("application/msword");
        } else if (fileName.toLowerCase().endsWith(".docx")) {
            return MediaType.parse("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else if (fileName.toLowerCase().endsWith(".txt")) {
            return MediaType.parse("text/plain");
        } else {
            return MediaType.parse("application/octet-stream");
        }
    }
    private void enviarNotificacionPrueba() {
        ApiService api = ApiClient.getClient(prefsManager).create(ApiService.class);

        RequestBody titulo = RequestBody.create("Ping desde Android", MultipartBody.FORM);
        RequestBody descripcion = RequestBody.create("Desde backend", MultipartBody.FORM);
        RequestBody fecha = RequestBody.create("2025-10-27", MultipartBody.FORM);
        MultipartBody.Part imagenVacia = MultipartBody.Part.createFormData("imagen", "");
        MultipartBody.Part archivoVacio = MultipartBody.Part.createFormData("archivo", "");

        Call<ApiResponse<Object>> call = api.crearNoti(titulo, descripcion, fecha, imagenVacia, archivoVacio);
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Form_eventos.this, "Notificación enviada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Form_eventos.this, "Error al enviar notificación", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(Form_eventos.this, "⚠️ Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
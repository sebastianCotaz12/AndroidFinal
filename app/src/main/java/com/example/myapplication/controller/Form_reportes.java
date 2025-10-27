package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
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

    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    if (imagenUri != null) {
                        binding.ivPreview.setVisibility(View.VISIBLE);
                        binding.llPlaceholder.setVisibility(View.GONE);
                        binding.ivPreview.setImageURI(imagenUri);
                        binding.tvImagenSeleccionada.setText("Imagen seleccionada");
                    }
                }
            });

    private final ActivityResultLauncher<Intent> seleccionarArchivoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    archivoUri = result.getData().getData();
                    if (archivoUri != null) {
                        String fileName = obtenerNombreArchivo(archivoUri);
                        binding.tvNombreArchivo.setText(fileName);
                        binding.tvTipoArchivo.setVisibility(View.VISIBLE);
                        binding.tvTipoArchivo.setText(obtenerTipoArchivo(fileName));
                    }
                }
            });

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        binding.etNombreUsuario.setText(prefsManager.getNombreUsuario());
        binding.etNombreUsuario.setEnabled(false);

        binding.etCargoUsuario.setText(prefsManager.getCargo());
        binding.etCargoUsuario.setEnabled(false);

        String[] opcionesEstado = {"Pendiente", "En Proceso", "Realizado"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesEstado);
        binding.spEstado.setAdapter(adapterEstado);

        // Configurar listeners para las nuevas áreas de carga
        binding.cardImagen.setOnClickListener(v -> seleccionarImagen());
        binding.cardArchivo.setOnClickListener(v -> seleccionarArchivo());

        binding.etFecha.setOnClickListener(v -> abrirDatePicker());
        binding.btnEnviarReporte.setOnClickListener(v -> guardarReporteConArchivos());
        binding.btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(Form_reportes.this, Lista_reportes.class);
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

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(value != null ? value : "", MediaType.parse("text/plain"));
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        if (fileUri == null) return null;

        try {
            String fileName = "upload_" + System.currentTimeMillis();
            String mimeType = getContentResolver().getType(fileUri);

            if (mimeType != null && mimeType.contains("/")) {
                String extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                fileName += "." + extension;
            }

            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            File tempFile = new File(getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);

            inputStream.close();
            outputStream.close();

            RequestBody requestFile = RequestBody.create(tempFile, MediaType.parse(mimeType != null ? mimeType : "application/octet-stream"));
            return MultipartBody.Part.createFormData(partName, tempFile.getName(), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparando archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    // 🔹 Método para subir reporte con imagen y archivo
    private void guardarReporteConArchivos() {
        int idUsuario = prefsManager.getIdUsuario();
        int idEmpresa = prefsManager.getIdEmpresa();
        String token = prefsManager.getToken();

        String nombreUsuario = binding.etNombreUsuario.getText().toString().trim();
        String cargoTextoPlano = binding.etCargoUsuario.getText().toString().trim();
        String cargoJsonArray = "[\"" + cargoTextoPlano + "\"]";

        // 🔹 OBTENER CÉDULA COMO STRING (no como int)
        String cedula = binding.etCedula.getText().toString().trim();
        if (cedula.isEmpty()) {
            Toast.makeText(this, "⚠️ Ingrese su cédula", Toast.LENGTH_LONG).show();
            return;
        }

        String fecha = binding.etFecha.getText().toString().trim();
        String lugar = binding.etLugar.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String estado = binding.spEstado.getSelectedItem().toString();

        // Validación de campos obligatorios
        if (cedula.isEmpty() || fecha.isEmpty() || lugar.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠️ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "🚫 No hay token. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            return;
        }

        ApiService apiService = ApiClient.getClient(prefsManager).create(ApiService.class);
        Log.d("TOKEN_DEBUG", "Token usado al crear reporte: " + token);

        // Preparar imagen
        MultipartBody.Part imagenPart = prepareFilePart("imagen", imagenUri);

        // Preparar archivo
        MultipartBody.Part archivoPart = null;
        if (archivoUri != null) {
            archivoPart = prepareFilePart("archivos", archivoUri);
        }

        Call<ApiResponse<Crear_reportes>> call = apiService.crearReporteMultipart(
                createPartFromString(String.valueOf(idUsuario)),
                createPartFromString(String.valueOf(idEmpresa)),
                createPartFromString(nombreUsuario),
                createPartFromString(cargoJsonArray),
                createPartFromString(cedula), // 🔹 ENVIAR CÉDULA COMO STRING
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
                Toast.makeText(Form_reportes.this, " Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
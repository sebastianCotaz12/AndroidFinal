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
import com.example.myapplication.databinding.ActivityFormActLudicasBinding;
import com.example.myapplication.utils.FileUtils;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_actLudicas extends AppCompatActivity {

    private ActivityFormActLudicasBinding binding;
    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    private Uri imagenUri = null;
    private Uri archivoUri = null;

    // Selector de imagen o video
    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    if (imagenUri != null) {
                        binding.ivPreview.setVisibility(android.view.View.VISIBLE);
                        binding.ivPreview.setImageURI(imagenUri);
                        binding.tvImagenSeleccionada.setText("Archivo multimedia seleccionado ✅");
                    }
                }
            });

    // Selector de archivo adjunto
   /* private final ActivityResultLauncher<Intent> seleccionarArchivoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    archivoUri = result.getData().getData();
                    if (archivoUri != null) {
                        binding.tvArchivoSeleccionado.setText("Archivo adjunto seleccionado ✅");
                    }
                }
            });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormActLudicasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        // Mostrar nombre y cargo
        String nombre = prefsManager.getNombreUsuario();
        String cargo = prefsManager.getCargo();
        binding.etNombreUsuario.setText(nombre != null ? nombre : "No disponible");
        binding.etCargoUsuario.setText(cargo != null ? cargo : "No disponible");
        binding.etNombreUsuario.setEnabled(false);
        binding.etCargoUsuario.setEnabled(false);

        // Listeners
        binding.etFecha.setOnClickListener(v -> abrirDatePicker());
        binding.ivAdjuntar.setOnClickListener(v -> seleccionarImagen());
      //  binding.ivArchivo.setOnClickListener(v -> seleccionarArchivo());
        binding.btnEnviarEvidencia.setOnClickListener(v -> guardarActividadMultipart());
        binding.btnCancelar.setOnClickListener(v -> {
            startActivity(new Intent(Form_actLudicas.this, Lista_actLudicas.class));
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
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        seleccionarImagenLauncher.launch(Intent.createChooser(intent, "Seleccionar imagen o video"));
    }



    private void guardarActividadMultipart() {
        String nombreActividad = binding.etNombreActividad.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();

        if (nombreActividad.isEmpty() || fecha.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠️ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        if (imagenUri == null) {
            Toast.makeText(this, "⚠️ Debes seleccionar una imagen o video.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            // Convertir campos a RequestBody
            RequestBody nombreBody = RequestBody.create(MediaType.parse("text/plain"), nombreActividad);
            RequestBody fechaBody = RequestBody.create(MediaType.parse("text/plain"), fecha);
            RequestBody descripcionBody = RequestBody.create(MediaType.parse("text/plain"), descripcion);

            // Imagen/video obligatorio
            String imagenPath = FileUtils.getPath(this, imagenUri);
            File imagenFile = new File(imagenPath);
            RequestBody imagenBody = RequestBody.create(MediaType.parse(getContentResolver().getType(imagenUri)), imagenFile);
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen_video", imagenFile.getName(), imagenBody);

            // Archivo adjunto opcional
            MultipartBody.Part archivoPart = null;
            if (archivoUri != null) {
                String archivoPath = FileUtils.getPath(this, archivoUri);
                File archivoFile = new File(archivoPath);
                RequestBody archivoBody = RequestBody.create(MediaType.parse(getContentResolver().getType(archivoUri)), archivoFile);
                archivoPart = MultipartBody.Part.createFormData("archivo_adjunto", archivoFile.getName(), archivoBody);
            }

            ApiService apiService = ApiClient.getClient(prefsManager).create(ApiService.class);

            Call<ApiResponse<Object>> call = apiService.crearActividadMultipart(
                    nombreBody,
                    fechaBody,
                    descripcionBody,
                    imagenPart,
                    archivoPart
            );

            call.enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(Form_actLudicas.this, "✅ " + response.body().getMsj(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Error al procesar archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

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
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.controller.Crear_reportes;
import com.example.myapplication.databinding.ActivityFormReportesBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;

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

    // Biometría
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    // === Seleccionar Imagen ===
    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    if (imagenUri != null) {
                        binding.ivPreview.setVisibility(android.view.View.VISIBLE);
                        binding.llPlaceholder.setVisibility(android.view.View.GONE);
                        binding.ivPreview.setImageURI(imagenUri);
                        binding.tvImagenSeleccionada.setText("Imagen seleccionada ✅");
                    }
                }
            });

    // === Seleccionar Archivo ===
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
        binding = ActivityFormReportesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // DIAGNÓSTICO INMEDIATO
        diagnosticarBiometria();

        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        // Configurar biometría
        configurarBiometria();

        configurarCampos();
        configurarBotones();
    }

    private void configurarBiometria() {
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);

                        // No mostrar toast para cancelación manual
                        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                                errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                            Toast.makeText(Form_reportes.this,
                                    "Error: " + errString, Toast.LENGTH_SHORT).show();
                        }

                        // Re-enable el botón si se cancela
                        binding.btnEnviarReporte.setEnabled(true);
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        // Autenticación exitosa - proceder a enviar el reporte
                        Toast.makeText(Form_reportes.this,
                                "✅ Identidad verificada, enviando reporte...", Toast.LENGTH_SHORT).show();
                        enviarReporteFinal();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(Form_reportes.this,
                                "❌ Autenticación fallida, intenta de nuevo", Toast.LENGTH_SHORT).show();
                        binding.btnEnviarReporte.setEnabled(true);
                    }
                });

        // PRIMERO: Intentar con BIOMETRIC_STRONG (reconocimiento facial 3D)
        BiometricManager biometricManager = BiometricManager.from(this);
        int strongAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        if (strongAuth == BiometricManager.BIOMETRIC_SUCCESS) {
            // El dispositivo soporta reconocimiento facial 3D
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Verificación de identidad")
                    .setSubtitle("Usa reconocimiento facial o huella")
                    .setDescription("Mira la cámara frontal o coloca tu dedo en el sensor")
                    .setNegativeButtonText("Cancelar")
                    .setConfirmationRequired(true)
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build();
        } else {
            // SEGUNDO: Intentar con combinación o credenciales del dispositivo
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Verificación de identidad")
                    .setSubtitle("Confirma tu identidad para enviar el reporte")
                    .setDescription("Usa huella, PIN, patrón o contraseña")
                    .setNegativeButtonText("Cancelar")
                    .setConfirmationRequired(true)
                    .setAllowedAuthenticators(
                            BiometricManager.Authenticators.BIOMETRIC_WEAK |
                                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build();
        }
    }

    private void configurarCampos() {
        binding.etNombreUsuario.setText(prefsManager.getNombreUsuario());
        binding.etNombreUsuario.setEnabled(false);

        binding.etCargoUsuario.setText(prefsManager.getCargo());
        binding.etCargoUsuario.setEnabled(false);

        String[] opcionesEstado = {"Pendiente", "En Proceso", "Realizado"};
        ArrayAdapter<String> adapterEstado =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesEstado);
        binding.spEstado.setAdapter(adapterEstado);
    }

    private void configurarBotones() {
        binding.cardImagen.setOnClickListener(v -> seleccionarImagen());
        binding.cardArchivo.setOnClickListener(v -> seleccionarArchivo());
        binding.etFecha.setOnClickListener(v -> abrirDatePicker());

        // Botón de enviar reporte con biometría
        binding.btnEnviarReporte.setOnClickListener(v -> validarYEnviarReporte());

        binding.btnCancelar.setOnClickListener(v -> {
            startActivity(new Intent(Form_reportes.this, Lista_reportes.class));
            finish();
        });
    }

    // ====================================
    // VALIDACIÓN Y BIOMETRÍA
    // ====================================
    private void validarYEnviarReporte() {
        // Deshabilitar botón para evitar múltiples clics
        binding.btnEnviarReporte.setEnabled(false);

        // Validar campos requeridos
        String cedula = binding.etCedula.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String lugar = binding.etLugar.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();

        if (cedula.isEmpty() || fecha.isEmpty() || lugar.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "⚠️ Completa todos los campos requeridos.", Toast.LENGTH_LONG).show();
            binding.btnEnviarReporte.setEnabled(true);
            return;
        }

        // Verificar disponibilidad de autenticación
        BiometricManager biometricManager = BiometricManager.from(this);

        // Probar diferentes métodos en orden de preferencia
        int authStatus = BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;

        // 1. Primero intentar con BIOMETRIC_STRONG (reconocimiento facial 3D)
        authStatus = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        // 2. Si no funciona, intentar con combinación
        if (authStatus != BiometricManager.BIOMETRIC_SUCCESS) {
            authStatus = biometricManager.canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_WEAK |
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
            );
        }

        // 3. Si aún no funciona, intentar solo con BIOMETRIC_WEAK
        if (authStatus != BiometricManager.BIOMETRIC_SUCCESS) {
            authStatus = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        }

        switch (authStatus) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // Autenticación disponible - mostrar prompt
                mostrarBiometriaParaReporte();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this,
                        "📝 No hay métodos biométricos configurados. Ve a Configuración > Seguridad para configurar huella o reconocimiento facial.",
                        Toast.LENGTH_LONG).show();
                binding.btnEnviarReporte.setEnabled(true);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this,
                        "❌ Este dispositivo no tiene sensor biométrico. Enviando reporte sin verificación...",
                        Toast.LENGTH_LONG).show();
                enviarReporteFinal();
                break;

            default:
                Toast.makeText(this,
                        "⚠️ Autenticación no disponible. Enviando reporte...",
                        Toast.LENGTH_LONG).show();
                enviarReporteFinal();
                break;
        }
    }

    private void mostrarBiometriaParaReporte() {
        // Mostrar mensaje informativo
        Toast.makeText(this, "🔐 Verificando identidad...", Toast.LENGTH_SHORT).show();

        // Mostrar prompt después de un breve delay
        binding.getRoot().postDelayed(() -> {
            biometricPrompt.authenticate(promptInfo);
        }, 500);
    }

    // ====================================
    // ENVÍO FINAL DEL REPORTE
    // ====================================
    private void enviarReporteFinal() {
        int idUsuario = prefsManager.getIdUsuario();
        int idEmpresa = prefsManager.getIdEmpresa();
        String token = prefsManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "🚫 Sesión inválida. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            binding.btnEnviarReporte.setEnabled(true);
            return;
        }

        String nombreUsuario = binding.etNombreUsuario.getText().toString().trim();
        String cargo = binding.etCargoUsuario.getText().toString().trim();
        String cedula = binding.etCedula.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String lugar = binding.etLugar.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String estado = binding.spEstado.getSelectedItem().toString();

        // Mostrar progreso
        binding.btnEnviarReporte.setText("Enviando...");
        binding.btnEnviarReporte.setEnabled(false);

        ApiService apiService = ApiClient.getClient(prefsManager).create(ApiService.class);
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
                // Restaurar botón
                binding.btnEnviarReporte.setText("Enviar Reporte");
                binding.btnEnviarReporte.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Form_reportes.this, " " + response.body().getMsj(), Toast.LENGTH_LONG).show();
                    Log.d("REPORTE_OK", "Reporte creado exitosamente");

                    // Redirigir después de éxito
                    binding.getRoot().postDelayed(() -> {
                        startActivity(new Intent(Form_reportes.this, Lista_reportes.class));
                        finish();
                    }, 1500);

                } else {
                    String errorMsg = "⚠️ Error al enviar (" + response.code() + ")";
                    try {
                        if (response.errorBody() != null)
                            errorMsg += ": " + response.errorBody().string();
                    } catch (Exception ignored) {}
                    Log.e("REPORTE_ERROR", errorMsg);
                    Toast.makeText(Form_reportes.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Crear_reportes>> call, Throwable t) {
                // Restaurar botón
                binding.btnEnviarReporte.setText("Enviar Reporte");
                binding.btnEnviarReporte.setEnabled(true);

                Log.e("REPORTE_FAIL", "Error conexión: " + t.getMessage());
                Toast.makeText(Form_reportes.this, "🚫 Falló la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ====================================
    // MÉTODOS EXISTENTES (sin cambios)
    // ====================================
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
        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        seleccionarArchivoLauncher.launch(Intent.createChooser(intent, "Seleccionar Archivo"));
    }

    private String obtenerNombreArchivo(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor =
                         getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e("FILE_ERROR", "Error al obtener nombre: " + e.getMessage());
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private String obtenerTipoArchivo(String fileName) {
        if (fileName.toLowerCase().endsWith(".pdf")) return "PDF Document";
        if (fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".docx"))
            return "Word Document";
        if (fileName.toLowerCase().endsWith(".txt")) return "Text File";
        return "Archivo";
    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(value != null ? value : "", MediaType.parse("text/plain"));
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        if (fileUri == null) return null;

        try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
            String mimeType = getContentResolver().getType(fileUri);
            String fileName = obtenerNombreArchivo(fileUri);

            File tempFile = new File(getCacheDir(), fileName);
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1)
                    outputStream.write(buffer, 0, bytesRead);
            }

            RequestBody requestFile =
                    RequestBody.create(tempFile, MediaType.parse(mimeType != null ? mimeType : "application/octet-stream"));
            return MultipartBody.Part.createFormData(partName, tempFile.getName(), requestFile);

        } catch (Exception e) {
            Log.e("FILE_PREPARE_ERR", "Error preparando archivo: " + e.getMessage());
            Toast.makeText(this, "Error al procesar archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
    private void diagnosticarBiometria() {
        BiometricManager biometricManager = BiometricManager.from(this);

        int strong = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        int weak = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        int deviceCredential = biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL);
        int combined = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
        );

        Log.d("BIOMETRIA_DIAG", "=== DIAGNÓSTICO BIOMETRÍA ===");
        Log.d("BIOMETRIA_DIAG", "BIOMETRIC_STRONG: " + getAuthStatus(strong) + " (Reconocimiento facial 3D/Iris)");
        Log.d("BIOMETRIA_DIAG", "BIOMETRIC_WEAK: " + getAuthStatus(weak) + " (Reconocimiento facial 2D/Huellas básicas)");
        Log.d("BIOMETRIA_DIAG", "DEVICE_CREDENTIAL: " + getAuthStatus(deviceCredential) + " (PIN/Patrón/Contraseña)");
        Log.d("BIOMETRIA_DIAG", "COMBINED: " + getAuthStatus(combined));

        // Mostrar resultado en Toast para debugging
        String mensaje = "Diagnóstico:\n" +
                "Facial 3D: " + getAuthStatus(strong) + "\n" +
                "Facial 2D/Huella: " + getAuthStatus(weak);
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    private String getAuthStatus(int status) {
        switch (status) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return " DISPONIBLE";
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return " NO HAY HARDWARE";
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                return "⚠ HARDWARE NO DISPONIBLE";
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return " NO CONFIGURADO";
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                return " ACTUALIZACIÓN REQUERIDA";
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                return " NO SOPORTADO";
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                return " DESCONOCIDO";
            default:
                return " CÓDIGO: " + status;
        }
    }
}
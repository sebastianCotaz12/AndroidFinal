package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.LoginRequest;
import com.example.myapplication.api.LoginResponse;
import com.example.myapplication.databinding.ActivityInicioSesionBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.FcmHelper;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioSesion extends AppCompatActivity {

    // ====================================
    // VARIABLES Y CONSTANTES
    // ====================================
    private ActivityInicioSesionBinding binding;
    private PrefsManager prefsManager;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    // ====================================
    // CICLO DE VIDA
    // ====================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInicioSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        inicializarComponentes();
        configurarBiometria();
        verificarSesionActiva();
        configurarListeners();
    }

    // ====================================
    // INICIALIZACIÓN
    // ====================================
    private void inicializarComponentes() {
        diagnosticarBiometria();
    }

    private void verificarSesionActiva() {
        if (prefsManager.isLoggedIn()) {
            Log.d("BIOMETRIA", "Sesión activa encontrada, mostrando biometría automática");
            mostrarBiometriaAutomatica();
        }
    }

    private void configurarListeners() {
        // Login normal
        binding.btnSignIn.setOnClickListener(view -> iniciarSesion());

        // Navegación
        binding.txtRegister.setOnClickListener(v ->
                startActivity(new Intent(InicioSesion.this, Registro.class)));

        binding.txtForgot.setOnClickListener(v ->
                startActivity(new Intent(InicioSesion.this, Olvidaste_contrasenia.class)));

        // Biometría manual
        binding.btnFaceId.setOnClickListener(v -> {
            if (prefsManager.isLoggedIn()) {
                mostrarBiometria();
            } else {
                Toast.makeText(this,
                        "Primero inicia sesión normalmente para activar la biometría",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // ====================================
    // DIAGNÓSTICO DE BIOMETRÍA
    // ====================================
    private void diagnosticarBiometria() {
        BiometricManager biometricManager = BiometricManager.from(this);

        int strong = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        int weak = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        int deviceCredential = biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL);
        int combined = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
        );

        Log.d("BIOMETRIA_DIAG", "=== DIAGNÓSTICO BIOMETRÍA INICIO SESIÓN ===");
        Log.d("BIOMETRIA_DIAG", "BIOMETRIC_STRONG: " + getAuthStatus(strong) + " (Reconocimiento facial 3D/Iris)");
        Log.d("BIOMETRIA_DIAG", "BIOMETRIC_WEAK: " + getAuthStatus(weak) + " (Reconocimiento facial 2D/Huellas básicas)");
        Log.d("BIOMETRIA_DIAG", "DEVICE_CREDENTIAL: " + getAuthStatus(deviceCredential) + " (PIN/Patrón/Contraseña)");
        Log.d("BIOMETRIA_DIAG", "COMBINED: " + getAuthStatus(combined));

        // Mostrar resultado en Toast para debugging
        String mensaje = "Diagnóstico Biometría:\n" +
                "Facial 3D: " + getAuthStatus(strong) + "\n" +
                "Facial 2D/Huella: " + getAuthStatus(weak) + "\n" +
                "PIN/Patrón: " + getAuthStatus(deviceCredential);
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    private String getAuthStatus(int status) {
        switch (status) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return "✅ DISPONIBLE";
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return "❌ NO HAY HARDWARE";
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                return "⚠️ HARDWARE NO DISPONIBLE";
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return "📝 NO CONFIGURADO";
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                return "🔒 ACTUALIZACIÓN REQUERIDA";
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                return "🚫 NO SOPORTADO";
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                return "❓ DESCONOCIDO";
            default:
                return "� CÓDIGO: " + status;
        }
    }

    // ====================================
    // CONFIGURACIÓN DE BIOMETRÍA
    // ====================================
    private void configurarBiometria() {
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(InicioSesion.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        manejarErrorBiometrico(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        manejarAutenticacionExitosa();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(InicioSesion.this,
                                "No reconocido, intenta de nuevo", Toast.LENGTH_SHORT).show();
                    }
                });

        configurarPromptBiometrico();
    }

    private void manejarErrorBiometrico(int errorCode, CharSequence errString) {
        // No mostrar toast para cancelación manual
        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {

            Log.e("BIOMETRIA", "Error código: " + errorCode + " - " + errString);

            String errorMsg = "Error de autenticación";
            if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                errorMsg = "Demasiados intentos. Espera 30 segundos";
            } else if (errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT) {
                errorMsg = "Bloqueo permanente. Usa PIN/patrón";
            }

            Toast.makeText(InicioSesion.this, errorMsg, Toast.LENGTH_LONG).show();
        } else {
            Log.d("BIOMETRIA", "Usuario canceló autenticación");
        }
    }

    private void manejarAutenticacionExitosa() {
        if (prefsManager.isLoggedIn()) {
            Log.d("BIOMETRIA", "✅ Autenticación biométrica exitosa");
            Log.d("BIOMETRIA", "Usuario: " + prefsManager.getNombreCompleto());
            Log.d("BIOMETRIA", "Empresa: " + prefsManager.getNombreEmpresa());
            irAlMenu();
        } else {
            Toast.makeText(InicioSesion.this,
                    "Sesión expirada, ingresa nuevamente", Toast.LENGTH_LONG).show();
        }
    }

    private void configurarPromptBiometrico() {
        try {
            BiometricManager biometricManager = BiometricManager.from(this);
            int strongAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

            if (strongAuth == BiometricManager.BIOMETRIC_SUCCESS) {
                // Dispositivo soporta reconocimiento facial 3D
                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Inicio de sesión biométrico")
                        .setSubtitle("Usa reconocimiento facial o huella")
                        .setDescription("Mira la cámara frontal o coloca tu dedo en el sensor")
                        .setNegativeButtonText("Cancelar")
                        .setConfirmationRequired(true)
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                        .build();
            } else {
                // Usar solo huellas
                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Inicio de sesión biométrico")
                        .setSubtitle("Usa tu huella digital")
                        .setDescription("Coloca tu dedo en el sensor")
                        .setNegativeButtonText("Cancelar")
                        .setConfirmationRequired(true)
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                        .build();
            }
        } catch (Exception e) {
            Log.e("BIOMETRIA", "Error configurando biometría: " + e.getMessage());
            // Configuración por defecto si hay error
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Verificación de identidad")
                    .setSubtitle("Confirma tu identidad")
                    .setNegativeButtonText("Cancelar")
                    .build();
        }
    }

    // ====================================
    // MÉTODOS DE BIOMETRÍA
    // ====================================
    private void mostrarBiometriaAutomatica() {
        if (!prefsManager.isLoggedIn()) {
            return;
        }

        int estadoBiometria = verificarDisponibilidadBiometrica();

        if (estadoBiometria == BiometricManager.BIOMETRIC_SUCCESS) {
            ejecutarBiometriaAutomatica();
        } else {
            manejarBiometriaNoDisponible(estadoBiometria);
        }
    }

    private int verificarDisponibilidadBiometrica() {
        BiometricManager biometricManager = BiometricManager.from(this);

        // Probar múltiples métodos
        int canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);

        // Si WEAK no funciona, probar STRONG
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        }

        return canAuth;
    }

    private void ejecutarBiometriaAutomatica() {
        Toast.makeText(this, "Verificando identidad...", Toast.LENGTH_SHORT).show();

        binding.getRoot().postDelayed(() -> {
            try {
                biometricPrompt.authenticate(promptInfo);
            } catch (Exception e) {
                Log.e("BIOMETRIA", "Error al mostrar biometría: " + e.getMessage());
                irAlMenu(); // Fallback
            }
        }, 1000);
    }

    private void manejarBiometriaNoDisponible(int estado) {
        String mensaje = obtenerMensajeErrorBiometrico(estado);
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        Log.d("BIOMETRIA", "Biometría no disponible: " + mensaje);
        irAlMenu();
    }

    private String obtenerMensajeErrorBiometrico(int estado) {
        switch (estado) {
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return "Configura huella/rostro en ajustes del dispositivo";
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return "Dispositivo sin sensor biométrico";
            default:
                return "Método biométrico no disponible";
        }
    }

    private void mostrarBiometria() {
        if (!prefsManager.isLoggedIn()) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }

        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuth = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
        );

        switch (canAuth) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Este dispositivo no tiene sensor biométrico", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Sensor biométrico no disponible", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No hay huellas/rostros registrados en el dispositivo", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, "Biometría no disponible", Toast.LENGTH_LONG).show();
                break;
        }
    }

    // ====================================
    // LOGIN NORMAL
    // ====================================
    private void iniciarSesion() {
        String correo = binding.edtUsername.getText().toString().trim();
        String contrasena = binding.edtPassword.getText().toString().trim();

        if (!validarCampos(correo, contrasena)) {
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        realizarLogin(correo, contrasena);
    }

    private boolean validarCampos(String correo, String contrasena) {
        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void realizarLogin(String correo, String contrasena) {
        ApiService apiService = ApiClient.getApiService();
        LoginRequest request = new LoginRequest(correo, contrasena);

        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                manejarRespuestaLogin(response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(InicioSesion.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void manejarRespuestaLogin(Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            LoginResponse loginResponse = response.body();
            Usuario user = loginResponse.getUser();

            if (user != null) {
                guardarDatosUsuario(loginResponse, user);
                FcmHelper.subscribeToTenantTopic(InicioSesion.this);
                irAlMenu();
            } else {
                Toast.makeText(InicioSesion.this,
                        "Error: usuario no encontrado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(InicioSesion.this,
                    "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarDatosUsuario(LoginResponse loginResponse, Usuario user) {
        prefsManager.setToken(loginResponse.getToken());
        prefsManager.setIdUsuario(user.getId());
        prefsManager.setNombreUsuario(user.getNombreUsuario() != null ? user.getNombreUsuario() : "");
        prefsManager.setNombre(user.getNombre() != null ? user.getNombre() : "");
        prefsManager.setApellidoUsuario(user.getApellido() != null ? user.getApellido() : "");
        prefsManager.setIdEmpresa(user.getIdEmpresa());
        prefsManager.setIdArea(user.getIdArea());
        prefsManager.setNombreEmpresa(user.getNombreEmpresa() != null ? user.getNombreEmpresa() : "");
        prefsManager.setNombreArea(user.getNombreArea() != null ? user.getNombreArea() : "");
        prefsManager.setCargo(user.getCargo() != null ? user.getCargo() : "");
        prefsManager.setCorreoElectronico(user.getCorreoElectronico() != null ? user.getCorreoElectronico() : "");

        // DEBUG
        Log.d("LOGIN", "✅ Login exitoso - Usuario: " + prefsManager.getNombreUsuario());
        Log.d("LOGIN", "✅ Empresa: " + prefsManager.getNombreEmpresa());
        Log.d("LOGIN", "✅ Área: " + prefsManager.getNombreArea());
    }

    // ====================================
    // NAVEGACIÓN
    // ====================================
    private void irAlMenu() {
        Log.d("NAVEGACION", "🔀 Redirigiendo al Menu...");
        Intent intent = new Intent(InicioSesion.this, Menu.class);
        startActivity(intent);
        finish();
    }
}


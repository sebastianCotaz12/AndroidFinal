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

    private ActivityInicioSesionBinding binding;
    private PrefsManager prefsManager;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private boolean tieneReconocimientoFacial = false;

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
        binding.btnSignIn.setOnClickListener(view -> iniciarSesion());
        binding.txtRegister.setOnClickListener(v ->
                startActivity(new Intent(InicioSesion.this, Registro.class)));
        binding.txtForgot.setOnClickListener(v ->
                startActivity(new Intent(InicioSesion.this, Olvidaste_contrasenia.class)));

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

    private void diagnosticarBiometria() {
        BiometricManager biometricManager = BiometricManager.from(this);

        int strong = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        int weak = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);

        Log.d("BIOMETRIA_DIAG", "=== DIAGNÓSTICO BIOMETRÍA ===");
        Log.d("BIOMETRIA_DIAG", "BIOMETRIC_STRONG: " + getAuthStatus(strong));
        Log.d("BIOMETRIA_DIAG", "BIOMETRIC_WEAK: " + getAuthStatus(weak));

        tieneReconocimientoFacial = (strong == BiometricManager.BIOMETRIC_SUCCESS);

        String mensaje = "Diagnóstico:\n" +
                "Facial 3D: " + getAuthStatus(strong) + "\n" +
                "Huella: " + getAuthStatus(weak) + "\n" +
                "Método: " + (tieneReconocimientoFacial ? "FACIAL" : "HUELLA");

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    private String getAuthStatus(int status) {
        switch (status) {
            case BiometricManager.BIOMETRIC_SUCCESS: return "✅ DISPONIBLE";
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE: return "❌ NO HAY HARDWARE";
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE: return "⚠️ NO DISPONIBLE";
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: return "📝 NO CONFIGURADO";
            default: return "� CÓDIGO: " + status;
        }
    }

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
        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {

            Log.e("BIOMETRIA", "Error: " + errorCode + " - " + errString);
            String errorMsg = "Error: " + errString;
            Toast.makeText(InicioSesion.this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void manejarAutenticacionExitosa() {
        if (prefsManager.isLoggedIn()) {
            Log.d("BIOMETRIA", "✅ Autenticación exitosa");
            irAlMenu();
        } else {
            Toast.makeText(InicioSesion.this,
                    "Sesión expirada", Toast.LENGTH_LONG).show();
        }
    }

    private void configurarPromptBiometrico() {
        try {
            BiometricManager biometricManager = BiometricManager.from(this);
            int strongAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
            int weakAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);

            Log.d("BIOMETRIA_CONFIG", "STRONG: " + strongAuth + ", WEAK: " + weakAuth);

            if (strongAuth == BiometricManager.BIOMETRIC_SUCCESS) {
                // FORZAR RECONOCIMIENTO FACIAL - Solo permitir BIOMETRIC_STRONG
                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Inicio de sesión biométrico")
                        .setSubtitle("Reconocimiento facial")
                        .setDescription("Mira directamente a la cámara frontal")
                        .setNegativeButtonText("Cancelar")
                        .setConfirmationRequired(false)
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG) // SOLO FACIAL
                        .build();

                Log.d("BIOMETRIA", "✅ CONFIGURADO EXCLUSIVAMENTE PARA RECONOCIMIENTO FACIAL");
                Toast.makeText(this, "✅ Configurado para reconocimiento facial", Toast.LENGTH_SHORT).show();

            } else if (weakAuth == BiometricManager.BIOMETRIC_SUCCESS) {
                // Solo huella digital
                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Inicio de sesión biométrico")
                        .setSubtitle("Huella digital")
                        .setDescription("Toca el sensor de huella")
                        .setNegativeButtonText("Cancelar")
                        .setConfirmationRequired(false)
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                        .build();

                Log.d("BIOMETRIA", "⚠️ Configurado para huella digital");
            } else {
                Log.w("BIOMETRIA", "❌ Sin métodos biométricos");
                promptInfo = null;
            }

        } catch (Exception e) {
            Log.e("BIOMETRIA", "Error: " + e.getMessage());
            promptInfo = null;
        }
    }

    private void mostrarBiometriaAutomatica() {
        if (!prefsManager.isLoggedIn()) return;
        if (promptInfo == null) {
            irAlMenu();
            return;
        }

        int estado = verificarDisponibilidadBiometrica();
        if (estado == BiometricManager.BIOMETRIC_SUCCESS) {
            ejecutarBiometriaAutomatica();
        } else {
            manejarBiometriaNoDisponible(estado);
        }
    }

    private int verificarDisponibilidadBiometrica() {
        BiometricManager biometricManager = BiometricManager.from(this);

        // Si tenemos reconocimiento facial configurado, verificar solo eso
        if (tieneReconocimientoFacial) {
            int facial = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
            Log.d("BIOMETRIA_CHECK", "Verificando solo facial: " + facial);
            return facial;
        } else {
            int huella = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
            Log.d("BIOMETRIA_CHECK", "Verificando solo huella: " + huella);
            return huella;
        }
    }

    private void ejecutarBiometriaAutomatica() {
        if (promptInfo == null) {
            irAlMenu();
            return;
        }

        Toast.makeText(this, "Verificando identidad...", Toast.LENGTH_SHORT).show();

        binding.getRoot().postDelayed(() -> {
            try {
                biometricPrompt.authenticate(promptInfo);
                Log.d("BIOMETRIA", "✅ Diálogo biométrico lanzado");
            } catch (Exception e) {
                Log.e("BIOMETRIA", "❌ Error: " + e.getMessage());

                // Si falla el facial, intentar con método alternativo
                if (tieneReconocimientoFacial) {
                    Toast.makeText(this, "Error con facial, intentando método alternativo", Toast.LENGTH_SHORT).show();
                    configurarMetodoAlternativo();
                } else {
                    irAlMenu();
                }
            }
        }, 1000);
    }

    private void configurarMetodoAlternativo() {
        try {
            // Configurar para permitir cualquier método biométrico
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Verificación de identidad")
                    .setSubtitle("Usa tu método biométrico")
                    .setDescription("Autentica con facial o huella")
                    .setNegativeButtonText("Cancelar")
                    .setConfirmationRequired(false)
                    .setAllowedAuthenticators(
                            BiometricManager.Authenticators.BIOMETRIC_STRONG |
                                    BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
                    .build();

            biometricPrompt.authenticate(promptInfo);
            Log.d("BIOMETRIA", "🔀 Método alternativo configurado");

        } catch (Exception e) {
            Log.e("BIOMETRIA", "❌ Error método alternativo: " + e.getMessage());
            irAlMenu();
        }
    }

    private void manejarBiometriaNoDisponible(int estado) {
        String mensaje = "Biometría no disponible: " + estado;
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        Log.d("BIOMETRIA", mensaje);
        irAlMenu();
    }

    private void mostrarBiometria() {
        if (!prefsManager.isLoggedIn()) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (promptInfo == null) {
            Toast.makeText(this, "Biometría no disponible", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            biometricPrompt.authenticate(promptInfo);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("BIOMETRIA", "Error mostrarBiometria: " + e.getMessage());
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

        Log.d("LOGIN", "");
    }

    private void irAlMenu() {
        Log.d("NAVEGACION", "");
        Intent intent = new Intent(InicioSesion.this, Menu.class);
        startActivity(intent);
        finish();
    }
}
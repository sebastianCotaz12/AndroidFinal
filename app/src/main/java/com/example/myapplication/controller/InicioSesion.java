package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.LoginRequest;
import com.example.myapplication.api.LoginResponse;
import com.example.myapplication.databinding.ActivityInicioSesionBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.FcmHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioSesion extends AppCompatActivity {

    private ActivityInicioSesionBinding binding;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInicioSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        // Si ya hay token guardado → saltar al menú directamente
        if (prefsManager.getToken() != null && !prefsManager.getToken().isEmpty()) {
            irAlMenu();
            return;
        }

        // Botón de iniciar sesión
        binding.btnSignIn.setOnClickListener(view -> iniciarSesion());

        // Ir a pantalla de registro
        binding.txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesion.this, Registro.class);
            startActivity(intent);
        });

        // Ir a pantalla de "Olvidaste contraseña"
        binding.txtForgot.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesion.this, Olvidaste_contrasenia.class);
            startActivity(intent);
        });
    }

    private void iniciarSesion() {
        String correo = binding.edtUsername.getText().toString().trim();
        String contrasena = binding.edtPassword.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getApiService();
        LoginRequest request = new LoginRequest(correo, contrasena);

        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Usuario user = loginResponse.getUser();

                    if (user != null) {
                        // Guardar datos del usuario
                        prefsManager.setToken(loginResponse.getToken());
                        prefsManager.setIdUsuario(user.getId());
                        prefsManager.setNombreUsuario(user.getNombreUsuario());
                        prefsManager.setNombre(user.getNombre());
                        prefsManager.setApellidoUsuario(user.getApellido());
                        prefsManager.setIdEmpresa(user.getIdEmpresa());
                        prefsManager.setIdArea(user.getIdArea());
                        prefsManager.setNombreEmpresa(user.getNombreEmpresa());
                        prefsManager.setNombreArea(user.getNombreArea());
                        prefsManager.setCargo(user.getCargo());
                        prefsManager.setCorreoElectronico(user.getCorreoElectronico());
                    } else {
                        Toast.makeText(InicioSesion.this, "Error: usuario no encontrado en la respuesta", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Suscribirse al topic del tenant
                    FcmHelper.subscribeToTenantTopic(InicioSesion.this);

                    // Ir al menú
                    irAlMenu();
                } else {
                    Toast.makeText(InicioSesion.this, "Credenciales incorrectas o error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(InicioSesion.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void irAlMenu() {
        Intent intent = new Intent(InicioSesion.this, Menu.class);
        startActivity(intent);
        finish();
    }


}

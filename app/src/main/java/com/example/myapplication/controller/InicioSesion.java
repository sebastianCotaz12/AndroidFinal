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

        binding.txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesion.this, Registro.class);
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

        // Llamada al backend
        ApiService apiService = ApiClient.getApiService();
        LoginRequest request = new LoginRequest(correo, contrasena);

        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Guardar token y datos del usuario en PrefsManager
                    prefsManager.setToken(loginResponse.getToken());
                    prefsManager.setIdUsuario(loginResponse.getUser().getId());
                    prefsManager.setNombreUsuario(loginResponse.getUser().getNombre());
                    prefsManager.setIdEmpresa(loginResponse.getUser().getIdEmpresa());
                    prefsManager.setIdArea(loginResponse.getUser().getIdArea());
                    prefsManager.setNombreEmpresa(loginResponse.getUser().getNombreEmpresa());
                    prefsManager.setNombreArea(loginResponse.getUser().getNombreArea());

                    Toast.makeText(InicioSesion.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

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

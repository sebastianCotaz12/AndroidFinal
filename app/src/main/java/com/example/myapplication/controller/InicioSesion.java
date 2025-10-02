package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
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

        binding.btnSignIn.setOnClickListener(v -> {
            String correo = binding.edtUsername.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            hacerLogin(correo, password);
        });
    }

    private void hacerLogin(String correo, String password) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(correo, password);

        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Guardar token
                    prefsManager.setToken(loginResponse.getToken());

                    // Guardar datos del usuario
                    if (loginResponse.getUser() != null) {
                        LoginResponse.Usuario usuario = loginResponse.getUser();

                        prefsManager.setIdUsuario(usuario.getId());
                        prefsManager.setNombreUsuario(usuario.getNombre());
                        prefsManager.setIdEmpresa(usuario.getIdEmpresa());
                        prefsManager.setIdArea(usuario.getIdArea());

                    }

                    Toast.makeText(InicioSesion.this, "Login correcto", Toast.LENGTH_SHORT).show();
                    irAlMenu();
                } else {
                    Toast.makeText(InicioSesion.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(InicioSesion.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void irAlMenu() {
        Intent intent = new Intent(this, Menu.class); // Tu Activity principal
        startActivity(intent);
        finish();
    }
}

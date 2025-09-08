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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inicioSesion extends AppCompatActivity {

    private ActivityInicioSesionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInicioSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔹 Botón login
        binding.btnSignIn.setOnClickListener(v -> hacerLogin());

        // 🔹 Botón registrarse
        binding.txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(inicioSesion.this, Registro.class);
            startActivity(intent);
        });

        // 🔹 Botón olvidó contraseña
        binding.txtForgot.setOnClickListener(v -> {
            Intent intent = new Intent(inicioSesion.this, Olvidaste_contrasenia.class);
            startActivity(intent);
        });
    }

    private void hacerLogin() {
        String correo = binding.edtUsername.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();

        if (correo.isEmpty()) {
            binding.edtUsername.setError("Ingrese su correo");
            return;
        }
        if (password.isEmpty()) {
            binding.edtPassword.setError("Ingrese su contraseña");
            return;
        }

        LoginRequest request = new LoginRequest(correo, password);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.getToken() != null && !loginResponse.getToken().isEmpty()) {
                        // ✅ Token válido
                        String token = loginResponse.getToken();
                        Toast.makeText(inicioSesion.this, "Bienvenido " + correo, Toast.LENGTH_SHORT).show();

                        // Guardar token en SharedPreferences
                        getSharedPreferences("auth", MODE_PRIVATE)
                                .edit()
                                .putString("token", token)
                                .apply();

                        // Ir a menú principal
                        startActivity(new Intent(inicioSesion.this, Menu.class));
                        finish();
                    } else {
                        String mensaje = (loginResponse.getMsj() != null && !loginResponse.getMsj().trim().isEmpty())
                                ? loginResponse.getMsj()
                                : "Credenciales incorrectas";
                        Toast.makeText(inicioSesion.this, mensaje, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(inicioSesion.this, "Error en la respuesta: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(inicioSesion.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

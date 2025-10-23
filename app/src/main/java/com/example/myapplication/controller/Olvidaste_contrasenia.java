package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityOlvidasteContraseniaBinding;
import com.example.myapplication.api.ForgotPasswordRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Olvidaste_contrasenia extends AppCompatActivity {

    private ActivityOlvidasteContraseniaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOlvidasteContraseniaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Botón para enviar correo de recuperación
        binding.btnOlvido.setOnClickListener(v -> {
            String correo = binding.edtCorreoRe.getText().toString().trim();

            if (correo.isEmpty()) {
                binding.edtCorreoRe.setError("Ingrese su correo");
                return;
            }

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            ForgotPasswordRequest request = new ForgotPasswordRequest(correo);

            Call<ApiResponse<Void>> call = apiService.forgotPassword(request);
            call.enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(Olvidaste_contrasenia.this, "Correo enviado correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Olvidaste_contrasenia.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(Olvidaste_contrasenia.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Botón para volver al inicio de sesión
        binding.btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Olvidaste_contrasenia.this, MainActivity.class); // MainActivity = tu pantalla de login
            startActivity(intent);
            finish(); // Para cerrar esta actividad
        });
    }
}

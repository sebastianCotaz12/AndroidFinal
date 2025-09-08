package com.example.myapplication.controller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityOlvidasteContraseniaBinding;

public class Olvidaste_contrasenia extends AppCompatActivity {

    private ActivityOlvidasteContraseniaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el layout
        binding = ActivityOlvidasteContraseniaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ahora ya puedes usar binding.*
        binding.edtCorreoRe.setOnClickListener(v -> {
            String correo = binding.edtCorreoRe.getText().toString().trim();
            if (correo.isEmpty()) {
                binding.edtCorreoRe.setError("Ingrese su correo");
            }
        });
    }
}

package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class inicioSesion extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnSignIn;
    TextView txtForgot, txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        // Referencias a la vista
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn   = findViewById(R.id.btnSignIn);
        txtForgot   = findViewById(R.id.txtForgot);
        txtRegister = findViewById(R.id.txtRegister);

        // 🔹 Evento botón "Iniciar Sesión"
        btnSignIn.setOnClickListener(v -> {
            String user = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Aquí iría tu validación real (BD, API, etc.)
                if (user.equals("admin") && pass.equals("1234")) {
                    Toast.makeText(this, "Inicio de sesión exitoso 🎉", Toast.LENGTH_SHORT).show();

                    // Pasar a otra Activity (ejemplo DashboardActivity)
                    Intent intent = new Intent(inicioSesion.this, Menu.class);
                    startActivity(intent);
                    finish(); // evita volver con el botón atrás
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 🔹 Evento "Olvidó su contraseña"
        txtForgot.setOnClickListener(v -> {
            Intent intent = new Intent(inicioSesion.this, registro.class);
            startActivity(intent);
        });

        // 🔹 Evento "Registrarse"
        //txtRegister.setOnClickListener(v -> {
         //   Intent intent = new Intent(inicioSesion.this, .class);
          //  startActivity(intent);
        //});
    }
}

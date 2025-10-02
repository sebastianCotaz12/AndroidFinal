package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class Nueva_Contrasenia extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_contrasenia);

        // Ajuste de padding para status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los campos
        EditText edtNueva = findViewById(R.id.edtNuevaContrasenia);
        EditText edtConfirmar = findViewById(R.id.edtConfirmarContrasenia);

        // Botones
        Button btnGuardar = findViewById(R.id.btnGuardarContrasenia);
        Button btnVolverLogin = findViewById(R.id.btnVolverLogin);

        // Acción del botón Guardar
        btnGuardar.setOnClickListener(v -> {
            String nueva = edtNueva.getText().toString().trim();
            String confirmar = edtConfirmar.getText().toString().trim();

            // Validaciones
            if (TextUtils.isEmpty(nueva) || TextUtils.isEmpty(confirmar)) {
                Toast.makeText(this, "Debe llenar ambos campos", Toast.LENGTH_SHORT).show();
                return; // No continúa
            }

            if (!nueva.equals(confirmar)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return; // No continúa
            }

            // Si pasa validaciones, regresa al login
            Toast.makeText(this, "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Nueva_Contrasenia.this, InicioSesion.class);
            startActivity(intent);
            finish();
        });

        // Acción del botón Volver al login (sin cambiar contraseña)
        btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Nueva_Contrasenia.this, InicioSesion.class);
            startActivity(intent);
            finish();
        });
    }
}

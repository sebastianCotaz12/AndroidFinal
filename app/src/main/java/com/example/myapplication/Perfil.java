package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.controller.Menu;
import com.example.myapplication.utils.PrefsManager;
import com.google.android.material.snackbar.Snackbar;

public class Perfil extends AppCompatActivity {

    EditText etNombre, etCargo, etCorreo, etArea, etEmpresa, etConfirmarPassword;
    Button btnCerrarSesion;
    ImageView ivPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        PrefsManager prefs = new PrefsManager(this);

        // Referencias UI
        ivPerfil = findViewById(R.id.ivPerfil);
        etNombre = findViewById(R.id.etNombre);
        etCargo = findViewById(R.id.etCargo);
        etCorreo = findViewById(R.id.etCorreo);
        etArea = findViewById(R.id.etArea);
        etEmpresa = findViewById(R.id.etEmpresa);
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Mostrar datos guardados del usuario
        etNombre.setText(prefs.getNombreUsuario());
        etEmpresa.setText(prefs.getNombreEmpresa());
        etArea.setText(prefs.getNombreArea());
        etCorreo.setText(prefs.getCorreoElectronico());
        etCargo.setText(prefs.getCargo());

        // Bloquear edición
        setEditable(false);

        // Acción del botón "Cerrar Sesión"
        btnCerrarSesion.setOnClickListener(v -> {

            Snackbar.make(v, "Sesión cerrada correctamente 🚪", Snackbar.LENGTH_SHORT).show();

            // Redirigir al menú principal o login
            Intent intent = new Intent(Perfil.this, Menu.class);
            startActivity(intent);
            finish();
        });
    }

    // Método para habilitar o deshabilitar los campos
    private void setEditable(boolean enabled) {
        etNombre.setEnabled(enabled);
        etCargo.setEnabled(enabled);
        etCorreo.setEnabled(enabled);
        etArea.setEnabled(enabled);
        etEmpresa.setEnabled(enabled);
        etConfirmarPassword.setEnabled(enabled);
    }
}

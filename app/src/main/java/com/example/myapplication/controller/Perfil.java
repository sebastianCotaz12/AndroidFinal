package com.example.myapplication.controller;

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

import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;

public class Perfil extends AppCompatActivity {

    EditText etNombre, etCargo, etCorreo, etArea, etEmpresa;
    Button btnCerrarSesion;
    ImageView ivPerfil, imgButton_VolverInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        PrefsManager prefsManager = new PrefsManager(this);

        // Ajuste de márgenes para pantallas edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar preferencias
        PrefsManager prefs = new PrefsManager(this);

        // Referencias UI
        ivPerfil = findViewById(R.id.ivPerfil);
        etNombre = findViewById(R.id.etNombre);
        etCargo = findViewById(R.id.etCargo);
        etCorreo = findViewById(R.id.etCorreo);
        etArea = findViewById(R.id.etArea);
        etEmpresa = findViewById(R.id.etEmpresa);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        imgButton_VolverInicio = findViewById(R.id.imgButton_VolverInicio);

        // Mostrar datos guardados del usuario
        etNombre.setText(prefs.getNombreUsuario());
        etEmpresa.setText(prefs.getNombreEmpresa());
        etArea.setText(prefs.getNombreArea());
        etCorreo.setText(prefs.getCorreoElectronico());
        etCargo.setText(prefs.getCargo());

        // Bloquear edición de campos
        setEditable(false);

        // Acción del botón "Cerrar Sesión"
        btnCerrarSesion.setOnClickListener(v -> {
            prefsManager.clearPrefs();
            Intent intent = new Intent(Perfil.this, InicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 🔙 Acción de la flecha para volver al menú
        imgButton_VolverInicio.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, Menu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
    }
}

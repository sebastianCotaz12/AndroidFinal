package com.example.myapplication.controller;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditarPerfil extends AppCompatActivity {


    private TextInputEditText etNombre, etApellido, etEmpresa, etArea, etCargo, etNombreUsuario, etCorreo, etContrasena;
    private MaterialButton btnGuardarCambios, btnVolver;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);

        // Ajuste de bordes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new PrefsManager(this);

        // Referencias
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etEmpresa = findViewById(R.id.etEmpresa);
        etArea = findViewById(R.id.etArea);
        etCargo = findViewById(R.id.etCargo);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnVolver = findViewById(R.id.btnVolver);

        // Cargar datos actuales
        cargarDatosGuardados();

        // Desactivar campos no editables
        etNombre.setEnabled(false);
        etApellido.setEnabled(false);
        etEmpresa.setEnabled(false);
        etArea.setEnabled(false);
        etCargo.setEnabled(false);

        // Guardar cambios
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        // Botón volver
        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarDatosGuardados() {
        etNombre.setText(prefsManager.getNombre());
        etApellido.setText(prefsManager.getApellidoUsuario());
        etEmpresa.setText(prefsManager.getNombreEmpresa());
        etArea.setText(prefsManager.getNombreArea());
        etCargo.setText(prefsManager.getCargo());
        etNombreUsuario.setText(prefsManager.getNombreUsuario());
        etCorreo.setText(prefsManager.getCorreoElectronico()); // ✅ CORREGIDO: ahora usa el correo real
    }

    private void guardarCambios() {
        String nuevoUsuario = etNombreUsuario.getText().toString().trim();
        String nuevoCorreo = etCorreo.getText().toString().trim();
        String nuevaContrasena = etContrasena.getText().toString().trim();

        if (nuevoUsuario.isEmpty() || nuevoCorreo.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos editables", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar en preferencias
        prefsManager.setNombreUsuario(nuevoUsuario);
        prefsManager.setCorreoElectronico(nuevoCorreo); // ✅ ahora sí guarda correctamente el correo

        Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();

        etContrasena.setText("");
    }


}

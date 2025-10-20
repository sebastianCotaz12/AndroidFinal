package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityFormGestionEppBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_gestionEpp extends AppCompatActivity {

    private ActivityFormGestionEppBinding binding;
    private PrefsManager prefsManager;
    private SesionManager sesionManager;
    private int areaUsuario; // ID del área

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormGestionEppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // Validar sesión activa
        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        // === Asignar datos automáticos del usuario ===
        String nombreUsuario = prefsManager.getNombreUsuario();
        String cargoUsuario = prefsManager.getCargo();
        String nombreArea = prefsManager.getNombreArea();
        areaUsuario = prefsManager.getIdArea();

        binding.etNombreUsuario.setText(nombreUsuario != null ? nombreUsuario : "No disponible");
        binding.etCargoUsuario.setText(cargoUsuario != null ? cargoUsuario : "No disponible");
        binding.etArea.setText(nombreArea != null ? nombreArea : "Área no asignada");

        binding.etNombreUsuario.setEnabled(false);
        binding.etCargoUsuario.setEnabled(false);
        binding.etArea.setEnabled(false);

        // === Configurar spinners ===
        String[] importancia = {"Alta", "Media", "Baja"};
        ArrayAdapter<String> adapterImp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, importancia);
        binding.spImportancia.setAdapter(adapterImp);

        String[] estados = {"Activo", "Pendiente", "Inactivo"};
        ArrayAdapter<String> adapterEst = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        binding.spEstado.setAdapter(adapterEst);

        // === Fecha ===
        binding.etFechaEntrega.setOnClickListener(v -> abrirDatePicker());

        // === Botón enviar ===
        binding.btnEnviarGestion.setOnClickListener(v -> guardarGestion());
    }

    private void abrirDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, day);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    binding.etFechaEntrega.setText(sdf.format(selected.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private void guardarGestion() {
        String cedula = binding.etCedula.getText().toString().trim();
        String cargoManual = binding.etCargo.getText().toString().trim();
        String importancia = binding.spImportancia.getSelectedItem().toString();
        String estado = binding.spEstado.getSelectedItem().toString();
        String cantidadStr = binding.etCantidad.getText().toString().trim();
        String productosStr = binding.etProductos.getText().toString().trim();

        if (cedula.isEmpty() || cargoManual.isEmpty() || cantidadStr.isEmpty() || productosStr.isEmpty()) {
            Toast.makeText(this, "⚠ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);

        // 🔹 Crear objeto de gestión
        Crear_gestionEpp gestion = new Crear_gestionEpp(
                cedula,
                cargoManual,
                importancia,
                estado,
                cantidad,
                areaUsuario,
                new int[]{1} // Ejemplo, reemplazar con IDs válidos desde backend si aplica
        );

        ApiService api = ApiClient.getClient(prefsManager).create(ApiService.class);
        Call<ApiResponse<Crear_gestionEpp>> call = api.crearGestionEpp(gestion);

        call.enqueue(new Callback<ApiResponse<Crear_gestionEpp>>() {
            @Override
            public void onResponse(Call<ApiResponse<Crear_gestionEpp>> call, Response<ApiResponse<Crear_gestionEpp>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Form_gestionEpp.this, "✅ Gestión EPP registrada correctamente", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(Form_gestionEpp.this, "⚠ Error en la API (" + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Crear_gestionEpp>> call, Throwable t) {
                Log.e("GESTION_FAIL", "Error conexión: " + t.getMessage());
                Toast.makeText(Form_gestionEpp.this, "❌ Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }
}

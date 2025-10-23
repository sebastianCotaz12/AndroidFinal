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

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_gestionEpp extends AppCompatActivity {

    private ActivityFormGestionEppBinding binding;
    private PrefsManager prefsManager;
    private SesionManager sesionManager;

    private int areaUsuario; // 🔹 Variable global para el id del área

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormGestionEppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // === Validar sesión activa ===
        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        // === Llenar campos automáticos visibles ===
        binding.etIdUsuario.setText(String.valueOf(prefsManager.getIdUsuario()));
        binding.etIdUsuario.setEnabled(false);

        // Área automática
        areaUsuario = prefsManager.getIdArea();
        String nombreArea = prefsManager.getNombreArea();
        if (nombreArea != null && areaUsuario != -1) {
            binding.etArea.setText(nombreArea);
        } else {
            binding.etArea.setText("Área no asignada");
        }
        binding.etArea.setEnabled(false); // No editable

        // === Campos manuales ===
        binding.etCargo.setEnabled(true);
        binding.etCedula.setEnabled(true);

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

    private RequestBody createPart(String value) {
        return RequestBody.create(value != null ? value : "", MediaType.parse("text/plain"));
    }

    private void guardarGestion() {
        String cedula = binding.etCedula.getText().toString().trim();
        String cargo = binding.etCargo.getText().toString().trim();
        String importancia = binding.spImportancia.getSelectedItem().toString();
        String estado = binding.spEstado.getSelectedItem().toString();
        String cantidadStr = binding.etCantidad.getText().toString().trim();
        String productosStr = binding.etProductos.getText().toString().trim();

        if (cedula.isEmpty() || cargo.isEmpty() || cantidadStr.isEmpty() || productosStr.isEmpty()) {
            Toast.makeText(this, "⚠️ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        int[] productos = {Integer.parseInt(productosStr)};

        // ✅ Usar idArea dinámico del usuario logueado
        Crear_gestionEpp gestion = new Crear_gestionEpp(
                cedula,
                Integer.parseInt(cargo),
                importancia,
                estado,
                cantidad,
                areaUsuario,
                productos
        );

        ApiService api = ApiClient.getClient(prefsManager).create(ApiService.class);
        Call<ApiResponse<Crear_gestionEpp>> call = api.crearGestionEpp(gestion);

        call.enqueue(new Callback<ApiResponse<Crear_gestionEpp>>() {
            @Override
            public void onResponse(Call<ApiResponse<Crear_gestionEpp>> call, Response<ApiResponse<Crear_gestionEpp>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Form_gestionEpp.this, "✅ Gestión creada correctamente", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(Form_gestionEpp.this, "⚠️ Error en la API (" + response.code() + ")", Toast.LENGTH_LONG).show();
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
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormGestionEppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        binding.etCargo.setText(prefsManager.getNombreArea());
        binding.etCargo.setEnabled(false);

        binding.etCedula.setText(String.valueOf(prefsManager.getIdUsuario()));
        binding.etCedula.setEnabled(false);

        // Spinners
        String[] importancia = {"Alta", "Media", "Baja"};
        ArrayAdapter<String> adapterImp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, importancia);
        binding.spImportancia.setAdapter(adapterImp);

        String[] estados = {"Activo", "Pendiente", "Inactivo"};
        ArrayAdapter<String> adapterEst = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        binding.spEstado.setAdapter(adapterEst);

        binding.etFechaEntrega.setOnClickListener(v -> abrirDatePicker());
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
        String fecha = binding.etFechaEntrega.getText().toString().trim();
        String cantidad = binding.etCantidad.getText().toString().trim();
        String productos = binding.etProductos.getText().toString().trim();

        if (fecha.isEmpty() || cantidad.isEmpty() || productos.isEmpty()) {
            Toast.makeText(this, "⚠️ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService api = ApiClient.getClient(prefsManager).create(ApiService.class);
        Call<ApiResponse<Crear_gestionEpp>> call = api.crearGestionEpp(
                createPart(String.valueOf(prefsManager.getIdUsuario())),
                createPart(String.valueOf(prefsManager.getIdEmpresa())),
                createPart(prefsManager.getNombreUsuario()),
                createPart(prefsManager.getNombreArea()),
                createPart(binding.etCedula.getText().toString()),
                createPart(fecha),
                createPart(productos),
                createPart("Entrega de EPP"),
                createPart(binding.spEstado.getSelectedItem().toString())
        );

        call.enqueue(new Callback<ApiResponse<Crear_gestionEpp>>() {
            @Override
            public void onResponse(Call<ApiResponse<Crear_gestionEpp>> call, Response<ApiResponse<Crear_gestionEpp>> response) {
                if (response.isSuccessful() && response.body() != null) {
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

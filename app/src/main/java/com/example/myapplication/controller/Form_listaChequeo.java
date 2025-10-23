package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityFormListaChequeoBinding;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.SesionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_listaChequeo extends AppCompatActivity {

    private ActivityFormListaChequeoBinding binding;
    private PrefsManager prefsManager;
    private SesionManager sesionManager;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormListaChequeoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);
        calendar = Calendar.getInstance();

        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠️ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        String nombreUsuario = prefsManager.getNombreUsuario();
        String cargoUsuario = prefsManager.getCargo();

        binding.etUsuarioNombre.setText(nombreUsuario);
        binding.etUsuarioCargo.setText(cargoUsuario);

        binding.etUsuarioNombre.setEnabled(false);
        binding.etUsuarioCargo.setEnabled(false);

        binding.etFecha.setOnClickListener(v -> mostrarDatePicker());
        binding.etHora.setOnClickListener(v -> mostrarTimePicker());

        binding.btnGuardarlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });
        binding.btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(Form_listaChequeo.this, Lista_listaChequeo.class);
            startActivity(intent);
            finish();
        });
    }

    private void mostrarDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    actualizarFecha();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void mostrarTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    actualizarHora();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void actualizarFecha() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        binding.etFecha.setText(dateFormat.format(calendar.getTime()));
    }

    private void actualizarHora() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        binding.etHora.setText(timeFormat.format(calendar.getTime()));
    }

    private void guardarDatos() {
        int idUsuario = prefsManager.getIdUsuario();
        int idEmpresa = prefsManager.getIdEmpresa();
        String token = prefsManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "🚫 Token inválido. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            return;
        }

        String nombreUsuario = prefsManager.getNombreUsuario();
        String cargoUsuario = prefsManager.getCargo();

        String fecha = binding.etFecha.getText().toString().trim();
        String hora = binding.etHora.getText().toString().trim();
        String modelo = binding.etModelo.getText().toString().trim();
        String marca = binding.etMarca.getText().toString().trim();
        String kilometraje = binding.etKilometraje.getText().toString().trim();
        String soat = getRadioValue(binding.rbSoatSi, binding.rbSoatNo);
        String tecnico = getRadioValue(binding.rbTecnicoSi, binding.rbTecnicoNo);
        String placa = binding.etPlaca.getText().toString().trim();
        String observaciones = binding.etObservaciones.getText().toString().trim();

        if (fecha.isEmpty() || hora.isEmpty() || modelo.isEmpty() || marca.isEmpty() ||
                kilometraje.isEmpty() || placa.isEmpty() || soat == null || tecnico == null) {
            Toast.makeText(this, "⚠️ Por favor completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        Crear_listaChequeo nuevaLista = new Crear_listaChequeo();
        nuevaLista.setIdUsuario(idUsuario);
        nuevaLista.setUsuarioNombre(nombreUsuario);
        nuevaLista.setCargo(cargoUsuario);
        nuevaLista.setFecha(fecha);
        nuevaLista.setHora(hora);
        nuevaLista.setModelo(modelo);
        nuevaLista.setMarca(marca);
        nuevaLista.setKilometraje(kilometraje);
        nuevaLista.setSoat(soat);
        nuevaLista.setTecnico(tecnico);
        nuevaLista.setPlaca(placa);
        nuevaLista.setObservaciones(observaciones);

        ApiService apiService = ApiClient.getClient(prefsManager).create(ApiService.class);
        Call<ApiResponse<Crear_listaChequeo>> call = apiService.crearListaChequeo(nuevaLista);

        call.enqueue(new Callback<ApiResponse<Crear_listaChequeo>>() {
            @Override
            public void onResponse(Call<ApiResponse<Crear_listaChequeo>> call, Response<ApiResponse<Crear_listaChequeo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Form_listaChequeo.this, "✅ Guardado correctamente.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Form_listaChequeo.this, Lista_listaChequeo.class));
                    finish();
                } else {
                    Toast.makeText(Form_listaChequeo.this, "⚠️ Error al guardar la lista.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Crear_listaChequeo>> call, Throwable t) {
                Toast.makeText(Form_listaChequeo.this, "🚫 Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRadioValue(RadioButton rbSi, RadioButton rbNo) {
        if (rbSi.isChecked()) return "Si";
        else if (rbNo.isChecked()) return "No";
        else return null;
    }
}
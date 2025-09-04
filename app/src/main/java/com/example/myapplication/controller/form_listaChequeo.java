package com.example.myapplication.controller;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class form_listaChequeo extends AppCompatActivity {

    private ActivityFormListaChequeoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el layout con ViewBinding
        binding = ActivityFormListaChequeoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Acción al hacer clic en el botón Guardar
        binding.btnGuardarlista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });
    }

    private void guardarDatos() {
        String nombreUsuario = binding.etUsuarioNombre.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String hora = binding.etHora.getText().toString().trim();
        String modelo = binding.etModelo.getText().toString().trim();
        String marca = binding.etMarca.getText().toString().trim();
        String kilometraje = binding.etKilometraje.getText().toString().trim();

        String soat = getRadioValue(binding.rbSoatSi, binding.rbSoatNo);
        String tecnico = getRadioValue(binding.rbTecnicoSi, binding.rbTecnicoNo);

        if (nombreUsuario.isEmpty() || fecha.isEmpty() || hora.isEmpty() ||
                modelo.isEmpty() || marca.isEmpty() || kilometraje.isEmpty() ||
                soat == null || tecnico == null) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_LONG).show();
            return;
        }

        // Crear objeto con los mismos campos que espera el backend
        crear_listaChequeo nuevaLista = new crear_listaChequeo();
        nuevaLista.setIdUsuario(5); // ejemplo, hasta que uses login real
        nuevaLista.setUsuarioNombre(nombreUsuario);
        nuevaLista.setFecha(fecha);
        nuevaLista.setHora(hora);
        nuevaLista.setModelo(modelo);
        nuevaLista.setMarca(marca);
        nuevaLista.setKilometraje(kilometraje);
        nuevaLista.setSoat(soat);
        nuevaLista.setTecnico(tecnico);

        String token = "TOKEN_JWT_VALIDO";

        ApiService apiService = ApiClient.getClient(token).create(ApiService.class);

        Call<ApiResponse<crear_listaChequeo>> call = apiService.crearListaChequeo(nuevaLista);

        call.enqueue(new Callback<ApiResponse<crear_listaChequeo>>() {
            @Override
            public void onResponse(Call<ApiResponse<crear_listaChequeo>> call, Response<ApiResponse<crear_listaChequeo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(form_listaChequeo.this, "Guardado: " + response.body().getMsj(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(form_listaChequeo.this, lista_listaChequeo.class));
                } else {
                    Toast.makeText(form_listaChequeo.this, "Error: No se pudo guardar.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<crear_listaChequeo>> call, Throwable t) {
                Toast.makeText(form_listaChequeo.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRadioValue(RadioButton rbSi, RadioButton rbNo) {
        if (rbSi.isChecked()) return "Si";
        else if (rbNo.isChecked()) return "No";
        else return null;
    }
}

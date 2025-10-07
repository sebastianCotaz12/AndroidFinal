package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.RegistroResponse;
import com.example.myapplication.databinding.ActivityRegistroBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registro extends AppCompatActivity {

    private ActivityRegistroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔹 Llenar Empresas desde backend
        EmpresaController empresaController = new EmpresaController();
        empresaController.listarEmpresas(this, empresas -> {
            List<String> nombresEmpresas = new ArrayList<>();
            List<Integer> idsEmpresas = new ArrayList<>();

            nombresEmpresas.add("Selecciona empresa");
            idsEmpresas.add(0);

            for (Empresa e : empresas) {
                nombresEmpresas.add(e.getNombre());
                idsEmpresas.add(e.getIdEmpresa());
            }

            ArrayAdapter<String> adapterEmpresas = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, nombresEmpresas);
            binding.spinnerEmpresa.setAdapter(adapterEmpresas);

            // Guardar IDs en tag para luego recuperar el seleccionado
            binding.spinnerEmpresa.setTag(idsEmpresas);
        });

        // 🔹 Llenar Áreas desde backend
        AreaController areaController = new AreaController(this);
        areaController.listarAreas(this, areas -> {
            List<String> nombresAreas = new ArrayList<>();
            List<Integer> idsAreas = new ArrayList<>();

            nombresAreas.add("Selecciona área");
            idsAreas.add(0);

            for (Area a : areas) {
                nombresAreas.add(a.getDescripcion());
                idsAreas.add(a.getIdArea());
            }

            ArrayAdapter<String> adapterAreas = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, nombresAreas);
            binding.spinnerArea.setAdapter(adapterAreas);

            // Guardar IDs en tag para luego recuperar el seleccionado
            binding.spinnerArea.setTag(idsAreas);
        });

        // Botones
        binding.btnRegistrarse.setOnClickListener(v -> registrarUsuario());
        binding.btnVolver.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        String nombre = binding.etNombre.getText().toString().trim();
        String apellido = binding.etApellido.getText().toString().trim();
        String nombreUsuario = binding.etNombreUsuario.getText().toString().trim();
        String correo = binding.etCorreo.getText().toString().trim();
        String cargo = binding.etCargo.getText().toString().trim();
        String contrasena = binding.etContrasena.getText().toString();
        String confirmContrasena = binding.etConfirmContrasena.getText().toString();

        int posEmpresa = binding.spinnerEmpresa.getSelectedItemPosition();
        int posArea = binding.spinnerArea.getSelectedItemPosition();

        List<Integer> idsEmpresas = (List<Integer>) binding.spinnerEmpresa.getTag();
        List<Integer> idsAreas = (List<Integer>) binding.spinnerArea.getTag();

        int empresaId = idsEmpresas != null ? idsEmpresas.get(posEmpresa) : 0;
        int areaId = idsAreas != null ? idsAreas.get(posArea) : 0;

        // validaciones...
        if (nombre.isEmpty() || apellido.isEmpty() || nombreUsuario.isEmpty() ||
                correo.isEmpty() || contrasena.isEmpty() || confirmContrasena.isEmpty() ||
                empresaId == 0 || areaId == 0) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!contrasena.equals(confirmContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        Crear_registro nuevoUsuario = new Crear_registro();
        nuevoUsuario.setIdEmpresa(empresaId);
        nuevoUsuario.setIdArea(areaId);
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setNombreUsuario(nombreUsuario);
        nuevoUsuario.setCorreoElectronico(correo);
        nuevoUsuario.setCargo(cargo);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setConfirmacion(confirmContrasena); // <<< MUY IMPORTANTE

        // Logear JSON a enviar (requiere dependency de Gson)
        String json = new com.google.gson.Gson().toJson(nuevoUsuario);
        android.util.Log.d("REQ_REGISTER", json);

        // Usar cliente público (SIN token)
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegistroResponse> call = apiService.registrarUsuario(nuevoUsuario);

        call.enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegistroResponse res = response.body();
                    Toast.makeText(Registro.this, "✅ " + res.getMensaje(), Toast.LENGTH_SHORT).show();

                    if (res.getUser() != null) {
                        String nombreResp = res.getUser().getNombre();
                        Toast.makeText(Registro.this, "Bienvenido " + nombreResp, Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(Registro.this, InicioSesion.class));
                    finish();
                } else {
                    // leer errorBody (ayuda mucho para saber qué devolvió el backend)
                    String err = "Error al registrar usuario.";
                    try {
                        if (response.errorBody() != null) {
                            err = response.errorBody().string();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    Toast.makeText(Registro.this, err, Toast.LENGTH_LONG).show();
                    android.util.Log.e("REQ_REGISTER_ERR", err);
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                Toast.makeText(Registro.this, "⚠️ Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                android.util.Log.e("REQ_REGISTER_FAIL", t.toString());
            }
        });
    }


}

package com.example.myapplication.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

    private int areaUsuario; // Id del área
    private Producto[] listaProductos; // Para guardar productos cargados
    private Producto productoSeleccionado; // Producto seleccionado en el spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormGestionEppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        sesionManager = new SesionManager(this);

        // === Validar sesión ===
        if (!sesionManager.haySesionActiva()) {
            Toast.makeText(this, "⚠ Sesión expirada. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show();
            sesionManager.cerrarSesion();
            finish();
            return;
        }

        // === Datos del usuario ===
        binding.etnombreUsuario.setText(prefsManager.getNombreUsuario());
        binding.etnombreUsuario.setEnabled(false);

        // === Área ===
        areaUsuario = prefsManager.getIdArea();
        String nombreArea = prefsManager.getNombreArea();
        if (nombreArea != null && areaUsuario != -1) {
            binding.etArea.setText(nombreArea);
        } else {
            binding.etArea.setText("Área no asignada");
        }
        binding.etArea.setEnabled(false);

        // === Spinner importancia ===
        String[] importancia = {"Alta", "Media", "Baja"};
        ArrayAdapter<String> adapterImp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, importancia);
        binding.spImportancia.setAdapter(adapterImp);

        // === Spinner estado ===
        String[] estados = {"Activo", "Pendiente", "Inactivo"};
        ArrayAdapter<String> adapterEst = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        binding.spEstado.setAdapter(adapterEst);

        // === Spinner productos ===
        ArrayAdapter<String> adapterProductosInicial = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Seleccione un producto (opcional)"});
        binding.spinnerProductos.setAdapter(adapterProductosInicial);
        binding.spinnerProductos.setEnabled(false);

        // === Listener para selección ===
        binding.spinnerProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && listaProductos != null && listaProductos.length >= position) {
                    productoSeleccionado = listaProductos[position - 1]; // -1 por el hint
                } else {
                    productoSeleccionado = null; // No seleccionó nada
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                productoSeleccionado = null;
            }
        });

        // === Fecha ===
        binding.etFechaEntrega.setOnClickListener(v -> abrirDatePicker());

        // === Cargar productos ===
        listarTodosLosProductos();

        // === Botones ===
        binding.btnEnviarGestion.setOnClickListener(v -> guardarGestion());
        binding.btnCancelar.setOnClickListener(v -> {
            startActivity(new Intent(Form_gestionEpp.this, Lista_gestionEpp.class));
            finish();
        });
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

    private void listarTodosLosProductos() {
        ApiService api = ApiClient.getClient(prefsManager).create(ApiService.class);
        Call<Producto[]> call = api.listarTodosLosProductos();

        call.enqueue(new Callback<Producto[]>() {
            @Override
            public void onResponse(Call<Producto[]> call, Response<Producto[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProductos = response.body();

                    String[] nombresProductos = new String[listaProductos.length + 1];
                    nombresProductos[0] = "Seleccione un producto (opcional)";
                    for (int i = 0; i < listaProductos.length; i++) {
                        nombresProductos[i + 1] = listaProductos[i].getNombre();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Form_gestionEpp.this,
                            android.R.layout.simple_spinner_dropdown_item, nombresProductos);
                    binding.spinnerProductos.setAdapter(adapter);
                    binding.spinnerProductos.setEnabled(true);
                } else {
                    Toast.makeText(Form_gestionEpp.this, "⚠ No se pudieron cargar los productos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto[]> call, Throwable t) {
                Log.e("PRODUCTOS_FAIL", "Error: " + t.getMessage());
                Toast.makeText(Form_gestionEpp.this, "❌ Error al conectar con el servidor.", Toast.LENGTH_SHORT).show();
            }
        });
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

        if (cedula.isEmpty() || cargo.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "⚠ Completa todos los campos obligatorios.", Toast.LENGTH_LONG).show();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "⚠ Cantidad inválida.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 👇 Si no seleccionó producto, se envía arreglo vacío
        int[] productos = (productoSeleccionado != null)
                ? new int[]{productoSeleccionado.getIdProducto()}
                : new int[]{}; // arreglo vacío

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
                    Toast.makeText(Form_gestionEpp.this, "✅ Gestión creada correctamente.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(Form_gestionEpp.this, "⚠ Error en la API (" + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Crear_gestionEpp>> call, Throwable t) {
                Log.e("GESTION_FAIL", "Error conexión: " + t.getMessage());
                Toast.makeText(Form_gestionEpp.this, "❌ Error de conexión al guardar.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
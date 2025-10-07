package com.example.myapplication.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.databinding.ActivityFormActLudicasBinding;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Form_actLudicas extends AppCompatActivity {

    private ActivityFormActLudicasBinding binding;
    private Uri imagenUri = null;
    private String imgBase64 = null;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormActLudicasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar lanzadores
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imagenUri = result.getData().getData();
                        binding.ivPreview.setImageURI(imagenUri);
                        binding.ivPreview.setVisibility(View.VISIBLE);
                        imgBase64 = convertirUriABase64(imagenUri);
                        Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
                    }
                });

        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imagenUri = result.getData().getData();
                        binding.ivPreview.setImageURI(imagenUri);
                        binding.ivPreview.setVisibility(View.VISIBLE);
                        imgBase64 = convertirUriABase64(imagenUri);
                        Toast.makeText(this, "Foto tomada", Toast.LENGTH_SHORT).show();
                    }
                });

        // Evento de adjuntar
        binding.ivAdjuntar.setOnClickListener(v -> mostrarOpcionesAdjunto());

        // Botón enviar
        binding.btnEnviarEvidencia.setOnClickListener(v -> guardarActividad());
    }

    private void mostrarOpcionesAdjunto() {
        String[] opciones = {"Elegir de galería", "Tomar foto"};
        new AlertDialog.Builder(this)
                .setTitle("Adjuntar evidencia")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        abrirGaleria();
                    } else {
                        abrirCamara();
                    }
                }).show();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoLauncher.launch(intent);
    }

    private String convertirUriABase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = getBytes(inputStream);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void guardarActividad() {
        if (imgBase64 == null) {
            Toast.makeText(this, "Debe seleccionar o tomar una foto", Toast.LENGTH_SHORT).show();
            return;
        }


        String usuario = binding.etUsuario.getText().toString().trim();
        String nombreActividad = binding.etNombreActividad.getText().toString().trim();
        String fecha = binding.etFecha.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        int idUsuario = 1;    //binding.et.getText().toString().trim();


        if (usuario.isEmpty() || nombreActividad.isEmpty() || fecha.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiY29ycmVvRWxlY3Ryb25pY28iOiJhbmRyZXNAZXhhbXBsZS5jb20iLCJ0aW1lc3RhbXAiOjE3NTcwMDIyMTI0NzMsImlhdCI6MTc1NzAwMjIxMiwiZXhwIjoxNzU3MDA1ODEyfQ.a_X1YeJMXLWsFFzzPAdx6rGRp1jjXo7GGYq1U0RINl8";
        Crear_actLudica actividad = new Crear_actLudica();
        actividad.setIdUsuario(idUsuario);
        actividad.setNombreActividad(nombreActividad);
        actividad.setFechaActividad(fecha);
        actividad.setDescripcion(descripcion);
        actividad.setNombreUsuario(usuario);
        actividad.setImagenVideo(imgBase64);
        System.out.println(imgBase64);
        actividad.setEvidencia(imgBase64);
        Gson gson = new Gson();
        String jsonBody = gson.toJson(actividad);
        System.out.println("Body que se enviará: " + jsonBody);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Crear_actLudica>> call = apiService.crearActividad(actividad);
        //ListaActividadesItem actividad = new ListaActividadesItem(0, usuario, nombreActividad, fecha, descripcion, imgBase64);

        //long resultado = managerDb.insertarActividad(actividad);


        call.enqueue(new Callback<ApiResponse<Crear_actLudica>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Crear_actLudica>> call, @NonNull Response<ApiResponse<Crear_actLudica>> response) {
                System.out.println(response.toString());
                if (response.isSuccessful() && response.body() != null) {

                    Toast.makeText(Form_actLudicas.this, "Guardado: " + response.body().getMsj(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Form_actLudicas.this, Lista_actLudicas.class));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Crear_actLudica>> call, Throwable t) {
                Toast.makeText(Form_actLudicas.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        Toast.makeText(this, "Actividad guardada con evidencia", Toast.LENGTH_SHORT).show();
    }
}
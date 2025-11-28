package com.example.myapplication.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.utils.PpeApi;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.utils.ImageUtils;
import com.example.myapplication.utils.PpeResponse;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import okhttp3.RequestBody;

public class Detector_obj extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private ImageView imagePreview;
    private View llPlaceholder;
    private TextView resultText;
    private TextView tvMissingItems;
    private LinearLayout llMissingItems;
    private MaterialButton btnSelectImage, btnTakePhoto, btnDetect, btnCancelar, btnLimpiar;
    private Bitmap selectedBitmap;
    private PpeApi ppeApi;

    // ActivityResultLauncher para permisos de cámara
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permiso concedido, abrir cámara
                    abrirCamara();
                } else {
                    // Permiso denegado
                    Toast.makeText(this, "Se necesita permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detector_obj);

        // Configurar insets para edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        initViews();

        // Inicializar Retrofit
        try {
            ppeApi = RetrofitClient.getClient().create(PpeApi.class);
        } catch (Exception e) {
            Log.e("RETROFIT", "Error inicializando Retrofit: " + e.getMessage());
        }

        setupClickListeners();
    }

    private void initViews() {
        imagePreview = findViewById(R.id.imagePreview);
        llPlaceholder = findViewById(R.id.llPlaceholder);
        resultText = findViewById(R.id.resultText);
        tvMissingItems = findViewById(R.id.tvMissingItems);
        llMissingItems = findViewById(R.id.llMissingItems);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnDetect = findViewById(R.id.btnDetect);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnLimpiar = findViewById(R.id.btnLimpiar);

        // Estado inicial
        imagePreview.setVisibility(View.GONE);
        llPlaceholder.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        btnSelectImage.setOnClickListener(v -> selectImageFromGallery());
        btnTakePhoto.setOnClickListener(v -> verificarPermisoCamara());
        btnDetect.setOnClickListener(v -> detectObjects());
        btnCancelar.setOnClickListener(v -> finish());
        btnLimpiar.setOnClickListener(v -> limpiarDatos());
    }

    private void selectImageFromGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e("GALERIA", "Error: " + e.getMessage());
            Toast.makeText(this, "Error al abrir galería", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarPermisoCamara() {
        // Verificar si ya tenemos permiso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            // Solicitar permiso
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Verificar que hay una app de cámara disponible
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "No hay aplicación de cámara disponible", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("CAMARA", "Error: " + e.getMessage());
            Toast.makeText(this, "Error al abrir cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void detectObjects() {
        if (selectedBitmap == null) {
            Toast.makeText(this, "Primero selecciona o toma una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ppeApi == null) {
            Toast.makeText(this, "Error: Conexión no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        enviarPpe(selectedBitmap);
    }

    private void limpiarDatos() {
        selectedBitmap = null;
        imagePreview.setVisibility(View.GONE);
        llPlaceholder.setVisibility(View.VISIBLE);
        imagePreview.setImageBitmap(null);
        resultText.setText("Selecciona o toma una foto para detectar EPP");
        llMissingItems.setVisibility(View.GONE);
        resultText.setTextColor(getResources().getColor(android.R.color.darker_gray));

        Toast.makeText(this, "Datos limpiados", Toast.LENGTH_SHORT).show();
    }

    public void enviarPpe(Bitmap bitmap) {
        // Mostrar loading
        btnDetect.setEnabled(false);
        btnDetect.setText("Analizando...");

        try {
            MultipartBody.Part imagePart = ImageUtils.bitmapToMultipart(bitmap, "image");

            // CREAR EL CONTEXTO "medical" como RequestBody
            RequestBody context = RequestBody.create(
                    MultipartBody.FORM,
                    "medical"
            );

            // Pasar ambos parámetros a la API
            Call<PpeResponse> call = ppeApi.checkPpe(imagePart, context);

            call.enqueue(new Callback<PpeResponse>() {
                @Override
                public void onResponse(Call<PpeResponse> call, Response<PpeResponse> response) {
                    btnDetect.setEnabled(true);
                    btnDetect.setText("🔍 Detectar EPP");

                    if (!response.isSuccessful() || response.body() == null) {
                        resultText.setText("Error en la respuesta del servidor: " + response.code());
                        resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        llMissingItems.setVisibility(View.GONE);
                        Toast.makeText(Detector_obj.this,
                                "Error en la respuesta del servidor",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    PpeResponse body = response.body();
                    if (body.ok) {
                        // MENSAJE MEJORADO - Cambiado de "null" a mensaje positivo
                        resultText.setText("✅ ¡Tienes todos los elementos de protección necesarios!");
                        resultText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        llMissingItems.setVisibility(View.GONE);
                        Toast.makeText(Detector_obj.this,
                                "EPP completos y correctos",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        resultText.setText("❌ Se detectaron problemas con el EPP");
                        resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                        if (body.missing != null && !body.missing.isEmpty()) {
                            StringBuilder missingText = new StringBuilder();
                            for (String item : body.missing) {
                                // Mostrar texto original (sin traducción)
                                missingText.append("• ").append(item).append("\n");
                            }
                            tvMissingItems.setText(missingText.toString());
                            llMissingItems.setVisibility(View.VISIBLE);
                        } else {
                            llMissingItems.setVisibility(View.GONE);
                        }

                        Toast.makeText(Detector_obj.this,
                                "Faltan uno o más EPP",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PpeResponse> call, Throwable t) {
                    btnDetect.setEnabled(true);
                    btnDetect.setText("🔍 Detectar EPP");

                    resultText.setText("Error de conexión: " + t.getMessage());
                    resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    llMissingItems.setVisibility(View.GONE);
                    Toast.makeText(Detector_obj.this,
                            "Error de red: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            btnDetect.setEnabled(true);
            btnDetect.setText("🔍 Detectar EPP");
            Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("ACTIVITY_RESULT", "requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    try {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        mostrarImagenSeleccionada();
                        resultText.setText("Imagen seleccionada. Presiona 'Detectar EPP' para analizar");
                        resultText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        llMissingItems.setVisibility(View.GONE);
                        Toast.makeText(this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("IMAGE_LOAD", "Error: " + e.getMessage());
                        Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == CAMERA_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    selectedBitmap = (Bitmap) extras.get("data");
                    if (selectedBitmap != null) {
                        mostrarImagenSeleccionada();
                        resultText.setText("Foto tomada. Presiona 'Detectar EPP' para analizar");
                        resultText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        llMissingItems.setVisibility(View.GONE);
                        Toast.makeText(this, "Foto tomada correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: Foto no disponible", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarImagenSeleccionada() {
        if (selectedBitmap != null) {
            imagePreview.setVisibility(View.VISIBLE);
            llPlaceholder.setVisibility(View.GONE);
            imagePreview.setImageBitmap(selectedBitmap);
        }
    }

    // Manejar respuesta de permisos (para compatibilidad)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
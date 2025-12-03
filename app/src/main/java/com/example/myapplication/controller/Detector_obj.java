package com.example.myapplication.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.myapplication.utils.LocalAnnotationManager;
import com.example.myapplication.utils.VisualAnnotationEngine;
import com.example.myapplication.utils.AnnotatedItem;
import com.example.myapplication.utils.PrefsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detector_obj extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;

    // Views del XML
    private ImageView imagePreview;
    private View llPlaceholder;
    private TextView resultText;
    private TextView tvMissingItems;
    private LinearLayout llMissingItems; // Contenedor de la lista
    private MaterialCardView cardResults; // Card de resultados
    private MaterialButton btnSelectImage, btnTakePhoto, btnDetect, btnCancelar, btnLimpiar;
    private AutoCompleteTextView contextDropdown;
    private TextInputLayout contextLayout;

    // Variables
    private Bitmap selectedBitmap;
    private Bitmap originalBitmap;
    private PpeApi ppeApi;
    private String selectedContext = "welder";
    private PrefsManager prefsManager;

    // Managers
    private LocalAnnotationManager annotationManager;
    private VisualAnnotationEngine visualEngine;

    // Contextos disponibles
    private final String[] availableContexts = {
            "welder",           // Soldador
            "medical",          // Médico
            "security_guard",   // Guardia de seguridad
            "construction"      // Construcción
    };

    // ActivityResultLauncher para permisos de cámara
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    abrirCamara();
                } else {
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

        // Inicializar PrefsManager
        prefsManager = new PrefsManager(this);

        // Inicializar vistas
        initViews();

        // Configurar dropdown de contextos
        setupContextDropdown();

        // Inicializar Retrofit con la URL de ngrok
        try {
            ppeApi = RetrofitClient.getClient().create(PpeApi.class);
        } catch (Exception e) {
            Log.e("RETROFIT", "Error inicializando Retrofit: " + e.getMessage());
            Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
        }

        // Inicializar managers
        annotationManager = LocalAnnotationManager.getInstance();
        visualEngine = new VisualAnnotationEngine();

        setupClickListeners();

        // Verificar autenticación
        checkAuthentication();
    }

    private void checkAuthentication() {
        String token = prefsManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Debes iniciar sesión para usar esta función", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.d("AUTH", "Token encontrado, longitud: " + token.length());
            Log.d("AUTH", "Cargo del usuario: " + prefsManager.getCargo());

            // Configurar contexto automático basado en el cargo
            autoSetContextFromCargo();
        }
    }

    private void autoSetContextFromCargo() {
        String cargo = prefsManager.getCargo();
        if (cargo != null && !cargo.isEmpty()) {
            cargo = cargo.toLowerCase().trim();

            if (cargo.contains("soldador")) {
                selectedContext = "welder";
                contextDropdown.setText("welder", false);
            } else if (cargo.contains("médico") || cargo.contains("medico") ||
                    cargo.contains("doctor") || cargo.contains("enfermero") ||
                    cargo.contains("enfermera")) {
                selectedContext = "medical";
                contextDropdown.setText("medical", false);
            } else if (cargo.contains("seguridad") || cargo.contains("guardia")) {
                selectedContext = "security_guard";
                contextDropdown.setText("security_guard", false);
            } else if (cargo.contains("ingeniero") || cargo.contains("operario") ||
                    cargo.contains("construcción") || cargo.contains("construccion") ||
                    cargo.contains("obra") || cargo.contains("administracion") ||
                    cargo.contains("usuario")) {
                selectedContext = "construction";
                contextDropdown.setText("construction", false);
            }

            Log.d("CONTEXTO", "Contexto automático desde cargo '" + cargo + "': " + selectedContext);
            Toast.makeText(this, "Contexto automático: " + getContextDisplayName(selectedContext),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        // Buscar todas las vistas usando los IDs del XML
        imagePreview = findViewById(R.id.imagePreview);
        llPlaceholder = findViewById(R.id.llPlaceholder);
        resultText = findViewById(R.id.resultText);
        tvMissingItems = findViewById(R.id.tvMissingItems);
        llMissingItems = findViewById(R.id.llMissingItems);
        cardResults = findViewById(R.id.cardResults); // La card de resultados del XML
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnDetect = findViewById(R.id.btnDetect);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        contextDropdown = findViewById(R.id.contextDropdown);
        contextLayout = findViewById(R.id.contextLayout);

        // Estado inicial - ocultar la imagen y mostrar placeholder
        imagePreview.setVisibility(View.GONE);
        llPlaceholder.setVisibility(View.VISIBLE);
        llMissingItems.setVisibility(View.GONE); // Ocultar lista de faltantes inicialmente
    }

    private void setupContextDropdown() {
        // Crear adapter para el dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                availableContexts
        );

        contextDropdown.setAdapter(adapter);
        contextDropdown.setText(adapter.getItem(0), false); // Establecer "welder" por defecto

        // Listener para cuando se selecciona un contexto
        contextDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedContext = adapter.getItem(position);
            Log.d("CONTEXTO", "Contexto seleccionado manualmente: " + selectedContext);
            Toast.makeText(this, "Contexto: " + getContextDisplayName(selectedContext),
                    Toast.LENGTH_SHORT).show();
        });
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
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e("GALERIA", "Error: " + e.getMessage());
            Toast.makeText(this, "Error al abrir galería", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
        // Verificar que hay imagen seleccionada
        if (selectedBitmap == null) {
            Toast.makeText(this, "Primero selecciona o toma una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar conexión API
        if (ppeApi == null) {
            Toast.makeText(this, "Error: Conexión no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar token de autenticación
        String token = prefsManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sesión expirada. Vuelve a iniciar sesión", Toast.LENGTH_LONG).show();
            // Si tienes LoginActivity, redirige aquí
            // startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Enviar imagen para detección
        enviarPpe(selectedBitmap, token);
    }

    private void limpiarDatos() {
        selectedBitmap = null;
        originalBitmap = null;
        selectedContext = "welder";
        contextDropdown.setText(availableContexts[0], false);
        imagePreview.setVisibility(View.GONE);
        llPlaceholder.setVisibility(View.VISIBLE);
        imagePreview.setImageBitmap(null);
        resultText.setText("Selecciona o toma una foto para detectar EPP");
        resultText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        llMissingItems.setVisibility(View.GONE);
        Toast.makeText(this, "Datos limpiados", Toast.LENGTH_SHORT).show();
    }

    private void enviarPpe(Bitmap bitmap, String token) {
        // Guardar copia original para mostrar después
        originalBitmap = bitmap.copy(bitmap.getConfig(), true);

        // Mostrar estado de carga
        btnDetect.setEnabled(false);
        btnDetect.setText("Analizando...");

        try {
            // Convertir bitmap a MultipartBody.Part
            MultipartBody.Part imagePart = ImageUtils.bitmapToMultipart(bitmap, "image");

            // Crear el header Authorization con el formato "Bearer <token>"
            String authHeader = "Bearer " + token;

            // Llamada a la API con token - usando el endpoint correcto
            Call<PpeResponse> call = ppeApi.checkPpe(
                    authHeader,        // Header Authorization: Bearer <token>
                    "local",           // Parámetro model
                    selectedContext,   // Parámetro context
                    imagePart          // Archivo de imagen
            );

            call.enqueue(new Callback<PpeResponse>() {
                @Override
                public void onResponse(Call<PpeResponse> call, Response<PpeResponse> response) {
                    btnDetect.setEnabled(true);
                    btnDetect.setText("Detectar EPP");

                    // Manejar error 401 (token inválido/vencido)
                    if (response.code() == 401) {
                        handleTokenExpired();
                        return;
                    }

                    // Manejar otros errores HTTP
                    if (!response.isSuccessful()) {
                        handleError("Error del servidor: " + response.code());
                        return;
                    }

                    // Manejar respuesta vacía
                    if (response.body() == null) {
                        handleError("Respuesta vacía del servidor");
                        return;
                    }

                    // Procesar respuesta exitosa
                    PpeResponse body = response.body();
                    procesarRespuesta(body);
                }

                @Override
                public void onFailure(Call<PpeResponse> call, Throwable t) {
                    btnDetect.setEnabled(true);
                    btnDetect.setText("Detectar EPP");
                    handleError("Error de conexión: " + t.getMessage());
                    Log.e("API_ERROR", "Error en la llamada API: ", t);
                }
            });
        } catch (Exception e) {
            btnDetect.setEnabled(true);
            btnDetect.setText("Detectar EPP");
            Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show();
            Log.e("ENVIAR_PPE", "Error: " + e.getMessage(), e);
        }
    }

    private void handleTokenExpired() {
        Toast.makeText(this, "Sesión expirada. Vuelve a iniciar sesión", Toast.LENGTH_LONG).show();
        prefsManager.clearAll();
        // Si tienes LoginActivity, redirige aquí
        // startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void procesarRespuesta(PpeResponse response) {
        if (response.ok) {
            // ✅ TODO CORRECTO - EPP completo
            String contextName = getContextDisplayName(selectedContext);
            resultText.setText(Html.fromHtml("✅ <b>¡Tienes todos los elementos de protección necesarios!</b><br/><small>Contexto: " + contextName + "</small>"));
            resultText.setTextColor(getResources().getColor(R.color.success_green));

            // Mostrar imagen original
            imagePreview.setImageBitmap(originalBitmap);

            // Ocultar lista de faltantes
            llMissingItems.setVisibility(View.GONE);

            Toast.makeText(Detector_obj.this,
                    "EPP completos para " + contextName,
                    Toast.LENGTH_SHORT).show();
        } else {
            // ❌ ELEMENTOS FALTANTES
            if (response.missing != null && !response.missing.isEmpty()) {
                // 1. Mostrar mensaje de resultados
                String contextName = getContextDisplayName(selectedContext);
                String missingCount = String.valueOf(response.missing.size());
                resultText.setText(Html.fromHtml("❌ <b>Se detectaron " + missingCount + " elementos faltantes</b><br/><small>Contexto: " + contextName + "</small>"));
                resultText.setTextColor(getResources().getColor(R.color.error_red));

                // 2. Anotar la imagen si es posible
                List<AnnotatedItem> missingAnnotations =
                        annotationManager.getAnnotationsForMissingItems(response.missing, selectedContext);

                Bitmap imagenAnotada = visualEngine.annotateImage(
                        originalBitmap,
                        missingAnnotations,
                        selectedContext
                );

                // Mostrar la imagen (anotada si existe, sino la original)
                if (imagenAnotada != null) {
                    imagePreview.setImageBitmap(imagenAnotada);
                } else {
                    imagePreview.setImageBitmap(originalBitmap);
                }

                // 3. Mostrar la lista de elementos faltantes en español
                mostrarListaFaltantes(response.missing);

                // 4. Toast informativo
                Toast.makeText(Detector_obj.this,
                        "Se detectaron " + missingCount + " elementos faltantes. Ver lista abajo.",
                        Toast.LENGTH_LONG).show();
            } else {
                handleError("No se especificaron elementos faltantes");
            }
        }
    }

    private void mostrarListaFaltantes(List<String> missingItems) {
        if (missingItems != null && !missingItems.isEmpty()) {
            // Limpiar lista anterior
            llMissingItems.removeAllViews();

            // Crear título
            TextView title = new TextView(this);
            title.setText("Elementos faltantes:");
            title.setTextColor(getResources().getColor(R.color.error_red));
            title.setTextSize(14);
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            title.setPadding(0, 0, 0, 8);
            llMissingItems.addView(title);

            // Agregar cada elemento faltante
            for (String item : missingItems) {
                TextView tvItem = new TextView(this);
                tvItem.setText("• " + traducirElemento(item));
                tvItem.setTextSize(14);
                tvItem.setTextColor(getResources().getColor(android.R.color.black));
                tvItem.setPadding(0, 4, 0, 4);
                llMissingItems.addView(tvItem);
            }

            // Mostrar la lista
            llMissingItems.setVisibility(View.VISIBLE);
        }
    }

    private String traducirElemento(String elemento) {
        // Traducciones de elementos comunes de EPP
        elemento = elemento.toLowerCase();

        if (elemento.contains("helmet")) return "Casco de seguridad";
        if (elemento.contains("goggles") || elemento.contains("glasses")) return "Gafas de protección";
        if (elemento.contains("gloves")) return "Guantes";
        if (elemento.contains("vest")) return "Chaleco reflectante";
        if (elemento.contains("boots") || elemento.contains("shoes")) return "Botas de seguridad";
        if (elemento.contains("mask")) return "Mascarilla";
        if (elemento.contains("ear") || elemento.contains("protection")) return "Protectores auditivos";
        if (elemento.contains("harness")) return "Arnés de seguridad";
        if (elemento.contains("apron")) return "Delantal";
        if (elemento.contains("gown")) return "Bata";
        if (elemento.contains("face") && elemento.contains("shield")) return "Protector facial";
        if (elemento.contains("respirator")) return "Respirador";
        if (elemento.contains("safety") && elemento.contains("glasses")) return "Lentes de seguridad";
        if (elemento.contains("hard") && elemento.contains("hat")) return "Casco de obra";
        if (elemento.contains("protective") && elemento.contains("clothing")) return "Ropa de protección";

        // Si no se reconoce, devolver el original formateado
        return elemento.substring(0, 1).toUpperCase() + elemento.substring(1).replace("_", " ");
    }

    private void handleError(String errorMessage) {
        resultText.setText(Html.fromHtml("⚠️ <b>" + errorMessage + "</b>"));
        resultText.setTextColor(getResources().getColor(R.color.warning_orange));
        llMissingItems.setVisibility(View.GONE);

        if (originalBitmap != null) {
            imagePreview.setImageBitmap(originalBitmap);
        }

        Toast.makeText(Detector_obj.this,
                "Error: " + errorMessage,
                Toast.LENGTH_SHORT).show();
    }

    private String getContextDisplayName(String context) {
        switch (context) {
            case "welder": return "Soldador";
            case "medical": return "Médico/Enfermera";
            case "security_guard": return "Guardia de Seguridad";
            case "construction": return "Construcción/Obra";
            default: return "General";
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
                        resetResultText();
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
                        // Mejorar la calidad de la foto de la cámara
                        selectedBitmap = Bitmap.createScaledBitmap(
                                selectedBitmap,
                                selectedBitmap.getWidth() * 2,
                                selectedBitmap.getHeight() * 2,
                                true
                        );

                        mostrarImagenSeleccionada();
                        resetResultText();
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

    private void resetResultText() {
        resultText.setText("Imagen seleccionada. Presiona 'Detectar EPP' para analizar");
        resultText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        llMissingItems.setVisibility(View.GONE);
    }

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
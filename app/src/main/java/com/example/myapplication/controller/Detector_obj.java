package com.example.myapplication.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
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
import com.example.myapplication.utils.LocalAnnotationManager;
import com.example.myapplication.utils.VisualAnnotationEngine;
import com.example.myapplication.utils.AnnotatedItem;
import com.example.myapplication.utils.PrefsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Detector_obj extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final String TAG = "Detector_obj";

    // Views del XML
    private ImageView imagePreview;
    private View llPlaceholder;
    private TextView resultText;
    private LinearLayout llMissingItems;
    private MaterialCardView cardResults;
    private MaterialButton btnSelectImage, btnTakePhoto, btnDetect, btnCancelar, btnLimpiar;

    // Variables
    private Bitmap selectedBitmap;
    private Bitmap originalBitmap;
    private PpeApi ppeApi;
    private String selectedContext = "construction";
    private PrefsManager prefsManager;

    // Para almacenar datos de detección
    private JSONArray lastDetectionData;

    // Managers
    private LocalAnnotationManager annotationManager;
    private VisualAnnotationEngine visualEngine;

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

        // Inicializar Retrofit
        try {
            ppeApi = RetrofitClient.getClient().create(PpeApi.class);
            Log.d("DEBUG_INIT", "Retrofit inicializado correctamente");
        } catch (Exception e) {
            Log.e("RETROFIT", "Error inicializando Retrofit: " + e.getMessage(), e);
            Toast.makeText(this, "Error de conexión con el servidor: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            } else if (cargo.contains("médico") || cargo.contains("medico") ||
                    cargo.contains("doctor") || cargo.contains("enfermero") ||
                    cargo.contains("enfermera") || cargo.contains("salud")) {
                selectedContext = "medical";
            } else if (cargo.contains("seguridad") || cargo.contains("guardia") ||
                    cargo.contains("vigilante") || cargo.contains("policía")) {
                selectedContext = "security_guard";
            } else if (cargo.contains("ingeniero") || cargo.contains("operario") ||
                    cargo.contains("construcción") || cargo.contains("construccion") ||
                    cargo.contains("obra") || cargo.contains("técnico") ||
                    cargo.contains("mecánico") || cargo.contains("electricista")) {
                selectedContext = "construction";
            } else {
                selectedContext = "construction";
            }

            Log.d("CONTEXTO", "Contexto automático desde cargo '" + cargo + "': " + selectedContext);
        } else {
            Log.w("CONTEXTO", "Cargo no definido, usando contexto por defecto: " + selectedContext);
        }
    }

    private void initViews() {
        imagePreview = findViewById(R.id.imagePreview);
        llPlaceholder = findViewById(R.id.llPlaceholder);
        resultText = findViewById(R.id.resultText);
        llMissingItems = findViewById(R.id.llMissingItems);
        cardResults = findViewById(R.id.cardResults);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnDetect = findViewById(R.id.btnDetect);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnLimpiar = findViewById(R.id.btnLimpiar);

        // Estado inicial
        imagePreview.setVisibility(View.GONE);
        llPlaceholder.setVisibility(View.VISIBLE);
        llMissingItems.setVisibility(View.GONE);
        cardResults.setVisibility(View.GONE);

        // Establecer texto inicial
        resultText.setText("Selecciona o toma una foto para detectar EPP");
        resultText.setTextColor(getResources().getColor(android.R.color.darker_gray));
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
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e("GALERIA", "Error: " + e.getMessage(), e);
            Toast.makeText(this, "Error al abrir galería", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Se necesita permiso de cámara para tomar fotos", Toast.LENGTH_LONG).show();
            }
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
            Log.e("CAMARA", "Error: " + e.getMessage(), e);
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

        String token = prefsManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sesión expirada. Vuelve a iniciar sesión", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d("DETECT", "Enviando imagen para detección. Contexto: " + selectedContext);

        String contextDisplayName = getContextDisplayName(selectedContext);
        Toast.makeText(this, "Analizando para: " + contextDisplayName, Toast.LENGTH_SHORT).show();

        enviarPpe(selectedBitmap, token);
    }

    private void limpiarDatos() {
        if (selectedBitmap != null && !selectedBitmap.isRecycled()) {
            selectedBitmap.recycle();
        }
        if (originalBitmap != null && !originalBitmap.isRecycled()) {
            originalBitmap.recycle();
        }

        selectedBitmap = null;
        originalBitmap = null;
        lastDetectionData = null;

        autoSetContextFromCargo();

        imagePreview.setVisibility(View.GONE);
        llPlaceholder.setVisibility(View.VISIBLE);
        imagePreview.setImageBitmap(null);
        cardResults.setVisibility(View.GONE);
        resultText.setText("Selecciona o toma una foto para detectar EPP");
        resultText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        llMissingItems.setVisibility(View.GONE);
        llMissingItems.removeAllViews();

        Toast.makeText(this, "Datos limpiados", Toast.LENGTH_SHORT).show();
    }

    private void enviarPpe(Bitmap bitmap, String token) {
        originalBitmap = bitmap.copy(bitmap.getConfig(), true);

        btnDetect.setEnabled(false);
        btnDetect.setText("Analizando...");

        try {
            Bitmap compressedBitmap = comprimirImagen(bitmap);
            if (compressedBitmap == null) {
                throw new Exception("Error al comprimir la imagen");
            }

            MultipartBody.Part imagePart = ImageUtils.bitmapToMultipart(compressedBitmap, "image");

            if (imagePart == null) {
                throw new Exception("Error al convertir imagen a formato multipart");
            }

            String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

            Log.d("API_REQUEST", "=== ENVIANDO SOLICITUD ===");
            Log.d("API_REQUEST", "Context: " + selectedContext);
            Log.d("API_REQUEST", "Imagen tamaño: " + compressedBitmap.getWidth() + "x" + compressedBitmap.getHeight());

            Call<PpeResponse> call = ppeApi.checkPpe(
                    authHeader,
                    "local",
                    selectedContext,
                    imagePart
            );

            call.enqueue(new Callback<PpeResponse>() {
                @Override
                public void onResponse(Call<PpeResponse> call, Response<PpeResponse> response) {
                    runOnUiThread(() -> {
                        btnDetect.setEnabled(true);
                        btnDetect.setText("Detectar EPP");
                    });

                    Log.d("API_RESPONSE", "Código HTTP: " + response.code());

                    if (response.code() == 401) {
                        handleTokenExpired();
                        return;
                    }

                    if (response.code() == 404) {
                        runOnUiThread(() -> {
                            Toast.makeText(Detector_obj.this,
                                    "Error 404: Endpoint no encontrado",
                                    Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    if (!response.isSuccessful()) {
                        String errorMsg = "Error del servidor: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                errorMsg = "Error " + response.code() + ": " +
                                        errorBody.substring(0, Math.min(100, errorBody.length()));
                            }
                        } catch (IOException e) {
                            Log.e("API_ERROR", "Error leyendo errorBody", e);
                        }

                        final String finalErrorMsg = errorMsg;
                        runOnUiThread(() -> handleError(finalErrorMsg));
                        return;
                    }

                    if (response.body() == null) {
                        runOnUiThread(() -> handleError("Respuesta vacía del servidor"));
                        return;
                    }

                    PpeResponse body = response.body();
                    runOnUiThread(() -> procesarRespuesta(body));
                }

                @Override
                public void onFailure(Call<PpeResponse> call, Throwable t) {
                    runOnUiThread(() -> {
                        btnDetect.setEnabled(true);
                        btnDetect.setText("Detectar EPP");
                    });

                    Log.e("API_FAILURE", "Error en la llamada API: ", t);

                    String errorMessage;
                    if (t.getMessage() != null) {
                        if (t.getMessage().contains("timeout")) {
                            errorMessage = "Timeout: El servidor tardó demasiado";
                        } else if (t.getMessage().contains("SSL")) {
                            errorMessage = "Error de seguridad SSL";
                        } else if (t.getMessage().contains("Unable to resolve host")) {
                            errorMessage = "No se puede conectar al servidor";
                        } else if (t.getMessage().contains("Connection refused")) {
                            errorMessage = "Conexión rechazada. Verifica el servidor";
                        } else {
                            errorMessage = "Error de conexión: " + t.getMessage();
                        }
                    } else {
                        errorMessage = "Error de conexión desconocido";
                    }

                    final String finalErrorMessage = errorMessage;
                    runOnUiThread(() -> handleError(finalErrorMessage));
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> {
                btnDetect.setEnabled(true);
                btnDetect.setText("Detectar EPP");
            });
            Log.e("ENVIAR_PPE", "Error general: ", e);
            Toast.makeText(this, "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap comprimirImagen(Bitmap original) {
        try {
            int maxWidth = 800;
            int maxHeight = 800;

            int width = original.getWidth();
            int height = original.getHeight();

            Log.d("COMPRESION", "Tamaño original: " + width + "x" + height);

            if (width > maxWidth || height > maxHeight) {
                float ratio = Math.min(
                        (float) maxWidth / width,
                        (float) maxHeight / height
                );

                int newWidth = (int) (width * ratio);
                int newHeight = (int) (height * ratio);

                Log.d("COMPRESION", "Redimensionando a: " + newWidth + "x" + newHeight);

                return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
            }

            return original.copy(original.getConfig(), true);
        } catch (Exception e) {
            Log.e("COMPRIMIR_IMAGEN", "Error: ", e);
            return original;
        }
    }

    private void handleTokenExpired() {
        runOnUiThread(() -> {
            Toast.makeText(Detector_obj.this, "Sesión expirada. Vuelve a iniciar sesión", Toast.LENGTH_LONG).show();
            prefsManager.clearAll();
            finish();
        });
    }

    private void procesarRespuesta(PpeResponse response) {
        Log.d(TAG, "=== PROCESANDO RESPUESTA ===");
        Log.d(TAG, "OK?: " + response.isOk());
        Log.d(TAG, "Contexto: " + selectedContext);
        Log.d(TAG, "Mensaje: " + response.getMessage());

        List<String> missingItems = response.getMissing();
        List<String> detectedItems = response.getDetected();

        Log.d(TAG, "Missing items: " + missingItems.size());
        Log.d(TAG, "Detected items: " + (detectedItems != null ? detectedItems.size() : 0));

        cardResults.setVisibility(View.VISIBLE);

        try {
            lastDetectionData = new JSONArray();
            for (String item : missingItems) {
                JSONObject obj = new JSONObject();
                JSONObject coords = new JSONObject();
                coords.put("x", 0.5);
                coords.put("y", 0.5);
                coords.put("width", 0.3);
                coords.put("height", 0.3);
                obj.put("coordinates", coords);
                obj.put("label", traducirElemento(item));
                obj.put("original_name", item);
                lastDetectionData.put(obj);
            }
            Log.d(TAG, "Datos de detección guardados: " + lastDetectionData.length() + " elementos");
        } catch (JSONException e) {
            Log.e(TAG, "Error creando JSON de detección: " + e.getMessage());
        }

        if (response.isOk()) {
            String contextName = getContextDisplayName(selectedContext);
            resultText.setText(Html.fromHtml("<b>✅ ¡Tienes todos los elementos de protección necesarios!</b><br/><small>Contexto: " + contextName + "</small>"));
            resultText.setTextColor(getResources().getColor(R.color.success_green));

            imagePreview.setImageBitmap(originalBitmap);

            llMissingItems.setVisibility(View.GONE);
            llMissingItems.removeAllViews();

            Toast.makeText(this,
                    "✅ EPP completos para " + contextName,
                    Toast.LENGTH_SHORT).show();

            // Actualizar estadísticas
            prefsManager.incrementEPPCompletos();
            prefsManager.incrementTotalDetections();

        } else {
            if (!missingItems.isEmpty()) {
                String contextName = getContextDisplayName(selectedContext);
                String missingCount = String.valueOf(missingItems.size());
                resultText.setText(Html.fromHtml("<b>⚠️ Se detectaron " + missingCount + " elementos faltantes</b><br/><small>Contexto: " + contextName + "</small>"));
                resultText.setTextColor(getResources().getColor(R.color.error_red));

                List<AnnotatedItem> missingAnnotations =
                        annotationManager.getAnnotationsForMissingItems(missingItems, selectedContext);

                Bitmap imagenAnotada = visualEngine.annotateImage(
                        originalBitmap,
                        missingAnnotations,
                        selectedContext
                );

                if (imagenAnotada != null) {
                    imagePreview.setImageBitmap(imagenAnotada);
                } else {
                    imagePreview.setImageBitmap(originalBitmap);
                }

                mostrarListaFaltantes(missingItems);

                // ⚠️ IMPORTANTE: NO necesitas enviar notificación desde Android
                // El backend YA lo hace automáticamente cuando detecta EPP faltante
                Log.d(TAG, "✅ El backend se encargará de enviar la notificación WebSocket automáticamente");

                Toast.makeText(this,
                        "⚠️ Se detectaron " + missingCount + " elementos faltantes",
                        Toast.LENGTH_LONG).show();

                // Actualizar estadísticas
                prefsManager.incrementEPPFaltantes();
                prefsManager.incrementTotalDetections();
                prefsManager.saveLastNotificationSent(selectedContext, missingItems.size());

            } else {
                handleError("No se detectaron elementos específicos faltantes");
            }
        }
    }

    private void mostrarListaFaltantes(List<String> missingItems) {
        if (missingItems != null && !missingItems.isEmpty()) {
            llMissingItems.removeAllViews();

            TextView title = new TextView(this);
            title.setText("Elementos faltantes:");
            title.setTextColor(getResources().getColor(R.color.error_red));
            title.setTextSize(16);
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            title.setPadding(0, 0, 0, 16);
            llMissingItems.addView(title);

            for (String item : missingItems) {
                TextView tvItem = new TextView(this);
                tvItem.setText("• " + traducirElemento(item));
                tvItem.setTextSize(14);
                tvItem.setTextColor(getResources().getColor(android.R.color.black));
                tvItem.setPadding(16, 8, 0, 8);
                llMissingItems.addView(tvItem);
            }

            llMissingItems.setVisibility(View.VISIBLE);
        } else {
            llMissingItems.setVisibility(View.GONE);
        }
    }

    private String traducirElemento(String elemento) {
        if (elemento == null || elemento.isEmpty()) {
            return "Elemento desconocido";
        }

        elemento = elemento.toLowerCase().trim();

        if (elemento.contains("helmet")) return "Casco de seguridad";
        if (elemento.contains("goggles") || elemento.contains("glasses")) return "Gafas de protección";
        if (elemento.contains("gloves")) return "Guantes";
        if (elemento.contains("vest")) return "Chaleco reflectante";
        if (elemento.contains("boots") || elemento.contains("shoes")) return "Botas de seguridad";
        if (elemento.contains("mask")) return "Mascarilla";
        if (elemento.contains("ear") && elemento.contains("protection")) return "Protectores auditivos";
        if (elemento.contains("harness")) return "Arnés de seguridad";
        if (elemento.contains("apron")) return "Delantal";
        if (elemento.contains("gown")) return "Bata";
        if (elemento.contains("face") && elemento.contains("shield")) return "Protector facial";
        if (elemento.contains("respirator")) return "Respirador";
        if (elemento.contains("safety") && elemento.contains("glasses")) return "Lentes de seguridad";
        if (elemento.contains("hard") && elemento.contains("hat")) return "Casco de obra";
        if (elemento.contains("protective") && elemento.contains("clothing")) return "Ropa de protección";
        if (elemento.contains("uniform")) return "Uniforme";
        if (elemento.contains("bulletproof")) return "Chaleco antibalas";
        if (elemento.contains("radio")) return "Radio de comunicación";
        if (elemento.contains("belt")) return "Cinturón de servicio";
        if (elemento.contains("cap")) return "Gorra/Elemento identificatorio";
        if (elemento.contains("welding")) return "Equipo de soldadura";
        if (elemento.contains("gear")) return "Equipo de protección";
        if (elemento.contains("helmet")) return "Casco";
        if (elemento.contains("goggles")) return "Gafas";
        if (elemento.contains("safety") && elemento.contains("boots")) return "Botas de seguridad";
        if (elemento.contains("protective") && elemento.contains("glasses")) return "Gafas protectoras";
        if (elemento.contains("ear") && elemento.contains("plug")) return "Tapones auditivos";

        return elemento.substring(0, 1).toUpperCase() +
                elemento.substring(1).replace("_", " ");
    }

    private void handleError(String errorMessage) {
        resultText.setText(Html.fromHtml("<b>⚠️ " + errorMessage + "</b>"));
        resultText.setTextColor(getResources().getColor(R.color.warning_orange));
        llMissingItems.setVisibility(View.GONE);
        llMissingItems.removeAllViews();

        if (originalBitmap != null) {
            imagePreview.setImageBitmap(originalBitmap);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private String getContextDisplayName(String context) {
        switch (context) {
            case "welder": return "Soldador";
            case "medical": return "Médico/Enfermería";
            case "security_guard": return "Seguridad";
            case "construction": return "Construcción/Obra";
            default: return context;
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
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        selectedBitmap = BitmapFactory.decodeStream(inputStream);

                        if (inputStream != null) {
                            inputStream.close();
                        }

                        if (selectedBitmap != null) {
                            mostrarImagenSeleccionada();
                            resetResultText();
                            Log.d(TAG, "Imagen cargada: " + selectedBitmap.getWidth() + "x" + selectedBitmap.getHeight());
                            Toast.makeText(this, "✅ Imagen cargada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e("IMAGE_LOAD", "Error: " + e.getMessage(), e);
                        Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                    } catch (SecurityException e) {
                        Log.e("IMAGE_LOAD", "Permiso denegado: " + e.getMessage());
                        Toast.makeText(this, "Permiso denegado para acceder a la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == CAMERA_REQUEST && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.get("data") instanceof Bitmap) {
                    selectedBitmap = (Bitmap) extras.get("data");
                    if (selectedBitmap != null) {
                        selectedBitmap = Bitmap.createScaledBitmap(
                                selectedBitmap,
                                selectedBitmap.getWidth() * 2,
                                selectedBitmap.getHeight() * 2,
                                true
                        );

                        mostrarImagenSeleccionada();
                        resetResultText();
                        Log.d(TAG, "Foto tomada: " + selectedBitmap.getWidth() + "x" + selectedBitmap.getHeight());
                        Toast.makeText(this, "✅ Foto tomada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error: Foto no disponible", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (data.getData() != null) {
                        try {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(),
                                    data.getData()
                            );
                            if (selectedBitmap != null) {
                                mostrarImagenSeleccionada();
                                resetResultText();
                                Toast.makeText(this, "✅ Foto cargada", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.e("CAMERA", "Error obteniendo imagen: " + e.getMessage());
                            Toast.makeText(this, "Error al obtener la foto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.d("ACTIVITY_RESULT", "Operación cancelada por el usuario");
        }
    }

    private void mostrarImagenSeleccionada() {
        if (selectedBitmap != null) {
            Log.d(TAG, "Mostrando imagen seleccionada");
            imagePreview.setVisibility(View.VISIBLE);
            llPlaceholder.setVisibility(View.GONE);
            imagePreview.setImageBitmap(selectedBitmap);

            cardResults.setVisibility(View.GONE);
        }
    }

    private void resetResultText() {
        resultText.setText("Imagen seleccionada. Presiona 'Detectar EPP' para analizar");
        resultText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        llMissingItems.setVisibility(View.GONE);
        llMissingItems.removeAllViews();
        cardResults.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (selectedBitmap != null && !selectedBitmap.isRecycled()) {
            selectedBitmap.recycle();
        }
        if (originalBitmap != null && !originalBitmap.isRecycled()) {
            originalBitmap.recycle();
        }
    }
}
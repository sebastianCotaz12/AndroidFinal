package com.example.myapplication.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.myapplication.utils.WebSocketClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
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
    private WebSocketClient webSocketClient;

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

        // DEBUG: Verificar token
        String token = prefsManager.getToken();
        Log.d("DEBUG_INIT", "Token disponible: " + (token != null && !token.isEmpty()));
        if (token != null) {
            Log.d("DEBUG_INIT", "Token longitud: " + token.length());
            Log.d("DEBUG_INIT", "Token primeros 20 chars: " + token.substring(0, Math.min(20, token.length())));
        }

        // Inicializar WebSocketClient
        webSocketClient = WebSocketClient.getInstance(this);

        // Inicializar vistas
        initViews();

        // Configurar dropdown de contextos
        setupContextDropdown();

        // Inicializar Retrofit con la URL de ngrok
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

        // Prueba de conexión WebSocket al iniciar
        testWebSocketOnStart();
    }

    private void testWebSocketOnStart() {
        new Handler().postDelayed(() -> {
            if (webSocketClient != null) {
                Log.d(TAG, "🔍 Probando conexión WebSocket al iniciar...");
                String status = webSocketClient.getConnectionStatus();
                Log.d(TAG, "📡 Estado WebSocket: " + status);

                // Enviar mensaje de prueba después de 3 segundos
                new Handler().postDelayed(() -> {
                    if (webSocketClient.isConnected()) {
                        webSocketClient.enviarNotificacionSimple("App iniciada - " + new Date());
                    }
                }, 3000);
            }
        }, 1000);
    }

    private void checkAuthentication() {
        String token = prefsManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Debes iniciar sesión para usar esta función", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.d("AUTH", "Token encontrado, longitud: " + token.length());
            Log.d("AUTH", "Token (primeros 30): " + token.substring(0, Math.min(30, token.length())) + "...");
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

        // Agregar listener largo al botón Detectar para probar WebSocket
        btnDetect.setOnLongClickListener(v -> {
            testWebSocketConnection();
            return true;
        });
    }

    private void selectImageFromGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
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
            finish();
            return;
        }

        // DEBUG: Mostrar token info
        Log.d("DETECT", "Token disponible, longitud: " + token.length());
        Log.d("DETECT", "Token (primeros 30): " + token.substring(0, Math.min(30, token.length())) + "...");

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
            // COMPRIMIR IMAGEN antes de enviar (IMPORTANTE)
            Bitmap compressedBitmap = comprimirImagen(bitmap);

            // Convertir bitmap a MultipartBody.Part
            MultipartBody.Part imagePart = ImageUtils.bitmapToMultipart(compressedBitmap, "image");

            if (imagePart == null) {
                throw new Exception("Error al convertir imagen a formato multipart");
            }

            // Crear el header Authorization con el formato "Bearer <token>"
            String authHeader;
            if (token.startsWith("Bearer ")) {
                authHeader = token;
            } else {
                authHeader = "Bearer " + token;
            }

            Log.d("API_REQUEST", "Enviando solicitud...");
            Log.d("API_REQUEST", "Auth Header: " + authHeader.substring(0, Math.min(30, authHeader.length())) + "...");
            Log.d("API_REQUEST", "Context: " + selectedContext);
            Log.d("API_REQUEST", "Imagen tamaño: " + compressedBitmap.getWidth() + "x" + compressedBitmap.getHeight());

            // Llamada a la API con token
            Call<PpeResponse> call = ppeApi.checkPpe(
                    authHeader,        // Header Authorization: Bearer <token>
                    "local",           // Parámetro model
                    selectedContext,   // Parámetro context
                    imagePart          // Archivo de imagen
            );

            call.enqueue(new Callback<PpeResponse>() {
                @Override
                public void onResponse(Call<PpeResponse> call, Response<PpeResponse> response) {
                    runOnUiThread(() -> {
                        btnDetect.setEnabled(true);
                        btnDetect.setText("Detectar EPP");
                    });

                    Log.d("API_RESPONSE", "Código HTTP: " + response.code());
                    Log.d("API_RESPONSE", "Mensaje: " + response.message());
                    Log.d("API_RESPONSE", "¿Éxito?: " + response.isSuccessful());

                    // ============== DEBUG: CAPTURAR RESPUESTA RAW ==============
                    try {
                        String rawResponse;
                        if (response.isSuccessful() && response.body() != null) {
                            Gson gson = new Gson();
                            rawResponse = gson.toJson(response.body());
                        } else if (response.errorBody() != null) {
                            rawResponse = response.errorBody().string();
                        } else {
                            rawResponse = "Respuesta vacía";
                        }

                        Log.d(TAG, "📥 RESPUESTA DEL SERVIDOR:");
                        Log.d(TAG, "Status: " + response.code());
                        Log.d(TAG, "Body: " + rawResponse.substring(0, Math.min(500, rawResponse.length())));

                        // Guardar para revisar después
                        prefsManager.saveLastApiResponse(rawResponse);

                    } catch (IOException e) {
                        Log.e(TAG, "Error leyendo respuesta: " + e.getMessage());
                    }
                    // ============== FIN DEBUG ==============

                    // Manejar error 401 (token inválido/vencido)
                    if (response.code() == 401) {
                        handleTokenExpired();
                        return;
                    }

                    // Manejar error 404 (endpoint no encontrado)
                    if (response.code() == 404) {
                        runOnUiThread(() -> {
                            Toast.makeText(Detector_obj.this,
                                    "Error 404: Endpoint no encontrado. Verifica la URL del backend.",
                                    Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    // Manejar otros errores HTTP
                    if (!response.isSuccessful()) {
                        String errorMsgFinal; // Usar nombre diferente
                        if (response.errorBody() != null) {
                            try {
                                String errorBody = response.errorBody().string();
                                errorMsgFinal = "Error del servidor: " + response.code() + " - " +
                                        errorBody.substring(0, Math.min(100, errorBody.length()));
                            } catch (IOException e) {
                                Log.e("API_ERROR", "Error leyendo errorBody", e);
                                errorMsgFinal = "Error del servidor: " + response.code();
                            }
                        } else {
                            errorMsgFinal = "Error del servidor: " + response.code();
                        }

                        final String finalErrorMsg = errorMsgFinal; // Crear variable final
                        runOnUiThread(() -> {
                            handleError(finalErrorMsg);
                        });
                        return;
                    }

                    // Manejar respuesta vacía
                    if (response.body() == null) {
                        runOnUiThread(() -> {
                            handleError("Respuesta vacía del servidor");
                        });
                        return;
                    }

                    // Procesar respuesta exitosa
                    PpeResponse body = response.body();

                    // DEBUG: Mostrar respuesta parseada
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(body);
                    Log.d(TAG, "📊 Respuesta parseada (JSON): " + jsonResponse);

                    runOnUiThread(() -> {
                        procesarRespuesta(body);
                    });
                }

                @Override
                public void onFailure(Call<PpeResponse> call, Throwable t) {
                    runOnUiThread(() -> {
                        btnDetect.setEnabled(true);
                        btnDetect.setText("Detectar EPP");
                    });

                    Log.e("API_FAILURE", "Error en la llamada API: ", t);

                    String errorMsgFinal; // Usar nombre diferente
                    if (t.getMessage() != null) {
                        if (t.getMessage().contains("timeout")) {
                            errorMsgFinal = "Timeout: El servidor tardó demasiado";
                        } else if (t.getMessage().contains("SSL")) {
                            errorMsgFinal = "Error de seguridad SSL";
                        } else if (t.getMessage().contains("Unable to resolve host")) {
                            errorMsgFinal = "No se puede conectar al servidor";
                        } else {
                            errorMsgFinal = "Error: " + t.getMessage();
                        }
                    } else {
                        errorMsgFinal = "Error de conexión desconocido";
                    }

                    final String finalErrorMsg = errorMsgFinal; // Crear variable final
                    runOnUiThread(() -> {
                        handleError(finalErrorMsg);
                    });
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
            // Tamaño máximo para enviar
            int maxWidth = 1024;
            int maxHeight = 1024;

            int width = original.getWidth();
            int height = original.getHeight();

            // Si la imagen es muy grande, redimensionar
            if (width > maxWidth || height > maxHeight) {
                float ratio = Math.min(
                        (float) maxWidth / width,
                        (float) maxHeight / height
                );

                int newWidth = (int) (width * ratio);
                int newHeight = (int) (height * ratio);

                return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
            }

            return original;
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
        Log.d(TAG, "📊 ======== PROCESANDO RESPUESTA DEL SERVIDOR ========");
        Log.d(TAG, "✅ OK?: " + response.isOk());
        Log.d(TAG, "🔧 Contexto seleccionado: " + selectedContext);
        Log.d(TAG, "🎯 Detected items: " + response.getDetected());
        Log.d(TAG, "❌ Missing items (raw): " + response.getMissing());
        Log.d(TAG, "📝 Message: " + response.getMessage());
        Log.d(TAG, "🔍 Model: " + response.getModel());

        // Obtener missing items usando el nuevo método
        List<String> missingItems = response.getMissing();
        Log.d(TAG, "🔍 Missing items procesados: " + missingItems.size() + " -> " + missingItems);

        if (response.isOk()) {
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

            // Enviar notificación de "todo correcto"
            enviarNotificacionWebSocketCompleto(contextName);

        } else {
            // ❌ ELEMENTOS FALTANTES
            if (!missingItems.isEmpty()) {
                // Usar missingItems
                String contextName = getContextDisplayName(selectedContext);
                String missingCount = String.valueOf(missingItems.size());
                resultText.setText(Html.fromHtml("❌ <b>Se detectaron " + missingCount + " elementos faltantes</b><br/><small>Contexto: " + contextName + "</small>"));
                resultText.setTextColor(getResources().getColor(R.color.error_red));

                // Anotar la imagen si es posible
                List<AnnotatedItem> missingAnnotations =
                        annotationManager.getAnnotationsForMissingItems(missingItems, selectedContext);

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

                // Mostrar la lista de elementos faltantes en español
                mostrarListaFaltantes(missingItems);

                // ENVIAR NOTIFICACIÓN AL WEBSOCKET
                enviarNotificacionWebSocket(contextName, missingItems);

                // Toast informativo
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
        if (elemento.contains("uniform")) return "Uniforme";
        if (elemento.contains("bulletproof")) return "Chaleco antibalas";
        if (elemento.contains("radio")) return "Radio de comunicación";
        if (elemento.contains("belt")) return "Cinturón de servicio";
        if (elemento.contains("cap")) return "Gorra/Elemento identificatorio";
        if (elemento.contains("welding")) return "Equipo de soldadura";
        if (elemento.contains("gear")) return "Equipo de protección";

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

    /**
     * MÉTODO PARA PROBAR LA CONEXIÓN WEBSOCKET
     * Mantén presionado el botón "Detectar EPP" para probar
     */
    private void testWebSocketConnection() {
        if (webSocketClient != null) {
            String status = webSocketClient.getConnectionStatus();

            // Mostrar diálogo con información
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("🔧 Prueba WebSocket")
                    .setMessage("Estado: " + status +
                            "\n\nURL: https://unreproaching-rancorously-evelina.ngrok-free.dev" +
                            "\n\n¿Enviar mensaje de prueba?")
                    .setPositiveButton("Enviar Prueba", (dialog, which) -> {
                        webSocketClient.enviarNotificacionSimple("Prueba manual - " + new Date());
                        Toast.makeText(this, "Mensaje de prueba enviado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Ver Estado", (dialog, which) -> {
                        Toast.makeText(this, "Estado: " + status, Toast.LENGTH_LONG).show();
                    })
                    .setNeutralButton("Cancelar", null)
                    .show();
        } else {
            Toast.makeText(this, "WebSocketClient es null", Toast.LENGTH_SHORT).show();
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
                        // Cargar imagen optimizada
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2; // Reducir tamaño
                        selectedBitmap = BitmapFactory.decodeStream(inputStream, null, options);

                        if (inputStream != null) {
                            inputStream.close();
                        }

                        if (selectedBitmap != null) {
                            mostrarImagenSeleccionada();
                            resetResultText();
                            Toast.makeText(this, "Imagen cargada correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        }
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

    @Override
    protected void onResume() {
        super.onResume();
        // Verificar conexión WebSocket cuando la app vuelve al frente
        if (webSocketClient != null && !webSocketClient.isConnected()) {
            Log.d(TAG, "🔄 Reconectando WebSocket en onResume...");
            webSocketClient.reconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // No desconectamos el WebSocket completamente para mantener la conexión
    }

    // Métodos WebSocket
    private void enviarNotificacionWebSocketVerificadoManual(String contexto) {
        Log.d(TAG, "✅ Enviando notificación de EPP verificado manualmente...");
        try {
            JSONObject notificacionData = new JSONObject();
            notificacionData.put("tipo", "epp_verificado_manual");
            notificacionData.put("mensaje", "✅ EPP VERIFICADO MANUALMENTE en contexto: " + contexto);
            notificacionData.put("fecha", new Date().toString());
            notificacionData.put("empresaId", prefsManager.getIdEmpresa());
            notificacionData.put("timestamp", System.currentTimeMillis());
            notificacionData.put("origen", "android_app");
            notificacionData.put("verificacion", "manual");

            JSONObject usuarioData = new JSONObject();
            usuarioData.put("id", prefsManager.getIdUsuario());
            usuarioData.put("nombre", prefsManager.getNombre());
            usuarioData.put("cargo", prefsManager.getCargo());
            notificacionData.put("usuario", usuarioData);

            if (webSocketClient != null && webSocketClient.isConnected()) {
                webSocketClient.enviarNotificacion(notificacionData);
                Log.d(TAG, "✅ Notificación de verificación manual enviada");
                Toast.makeText(Detector_obj.this, "✅ Notificación enviada (verificación manual)", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error enviando notificación de verificación manual: " + e.getMessage());
        }
    }

    private void enviarNotificacionWebSocket(String contexto, List<String> elementosFaltantes) {
        Log.d(TAG, "🚀 INICIANDO envío de notificación WebSocket...");
        String cargo = prefsManager.getCargo();
        if (cargo == null || cargo.isEmpty()) {
            Log.w(TAG, "❌ No se puede enviar notificación: cargo no disponible");
            Toast.makeText(this, "Error: Cargo no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject notificacionData = new JSONObject();
            notificacionData.put("tipo", "epp_faltante");
            notificacionData.put("mensaje", "⚠️ EPP incompleto detectado en contexto: " + contexto);
            notificacionData.put("fecha", new Date().toString());
            notificacionData.put("empresaId", prefsManager.getIdEmpresa());
            notificacionData.put("timestamp", System.currentTimeMillis());
            notificacionData.put("origen", "android_app");

            JSONObject usuarioData = new JSONObject();
            usuarioData.put("id", prefsManager.getIdUsuario());
            usuarioData.put("nombre", prefsManager.getNombre());
            usuarioData.put("nombre_completo", prefsManager.getNombreCompleto());
            usuarioData.put("cargo", cargo);
            usuarioData.put("email", prefsManager.getCorreoElectronico());
            notificacionData.put("usuario", usuarioData);

            JSONObject deteccionData = new JSONObject();
            deteccionData.put("contexto", selectedContext);
            deteccionData.put("contexto_display", contexto);
            deteccionData.put("elementos_faltantes", new org.json.JSONArray(elementosFaltantes));
            deteccionData.put("total_faltantes", elementosFaltantes.size());
            deteccionData.put("fecha_deteccion", System.currentTimeMillis());
            notificacionData.put("deteccion", deteccionData);

            org.json.JSONArray elementosTraducidos = new org.json.JSONArray();
            for (String elemento : elementosFaltantes) {
                elementosTraducidos.put(traducirElemento(elemento));
            }
            notificacionData.put("elementos_faltantes_es", elementosTraducidos);

            Log.d(TAG, "📤 JSON a enviar: " + notificacionData.toString(2));

            if (webSocketClient == null) {
                Toast.makeText(this, "Error: WebSocket no inicializado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (webSocketClient.isConnected()) {
                webSocketClient.enviarNotificacion(notificacionData);
                Toast.makeText(Detector_obj.this, "✅ Notificación enviada al sistema SG-SST", Toast.LENGTH_SHORT).show();
                prefsManager.saveLastNotificationSent(contexto, elementosFaltantes.size());
            } else {
                Log.w(TAG, "⚠️ WebSocket no conectado, intentando reconectar...");
                webSocketClient.reconnect();
                new Handler().postDelayed(() -> {
                    if (webSocketClient.isConnected()) {
                        webSocketClient.enviarNotificacion(notificacionData);
                        Toast.makeText(Detector_obj.this, "✅ Notificación enviada (reconexión)", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Detector_obj.this, "❌ Error de conexión con el servidor", Toast.LENGTH_LONG).show();
                    }
                }, 2000);
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error en enviarNotificacionWebSocket: " + e.getMessage());
        }
    }

    private void enviarNotificacionWebSocketCompleto(String contexto) {
        Log.d(TAG, "✅ Enviando notificación de EPP completo...");
        try {
            JSONObject notificacionData = new JSONObject();
            notificacionData.put("tipo", "epp_completo");
            notificacionData.put("mensaje", "✅ EPP completo detectado en contexto: " + contexto);
            notificacionData.put("fecha", new Date().toString());
            notificacionData.put("empresaId", prefsManager.getIdEmpresa());
            notificacionData.put("timestamp", System.currentTimeMillis());
            notificacionData.put("origen", "android_app");

            JSONObject usuarioData = new JSONObject();
            usuarioData.put("id", prefsManager.getIdUsuario());
            usuarioData.put("nombre", prefsManager.getNombre());
            usuarioData.put("cargo", prefsManager.getCargo());
            notificacionData.put("usuario", usuarioData);

            if (webSocketClient != null && webSocketClient.isConnected()) {
                webSocketClient.enviarNotificacion(notificacionData);
                Log.d(TAG, "✅ Notificación de EPP completo enviada");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error enviando notificación de EPP completo: " + e.getMessage());
        }
    }
}
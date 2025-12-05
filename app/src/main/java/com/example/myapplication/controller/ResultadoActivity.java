package com.example.myapplication.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.OnPhotoTapListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultadoActivity extends AppCompatActivity {

    private PhotoView photoView;
    private DetectorOverlay overlay;
    private ImageButton btnBack, btnToggleOverlay, btnZoom;
    private boolean showOverlay = true;
    private Bitmap currentBitmap;
    private String imagePath;
    private List<RectF> rectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        // Inicializar vistas
        photoView = findViewById(R.id.photoView);
        overlay = findViewById(R.id.overlay);
        btnBack = findViewById(R.id.btnBack);
        btnToggleOverlay = findViewById(R.id.btnToggleOverlay);
        btnZoom = findViewById(R.id.btnZoom);

        // Obtener datos del Intent
        imagePath = getIntent().getStringExtra("imagePath");
        String missingJson = getIntent().getStringExtra("missing");

        // Cargar imagen
        if (imagePath != null) {
            try {
                currentBitmap = BitmapFactory.decodeFile(imagePath);
                if (currentBitmap != null) {
                    photoView.setImageBitmap(currentBitmap);
                } else {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No se proporcionó imagen", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Procesar y dibujar rectángulos si hay JSON
        if (missingJson != null && !missingJson.isEmpty()) {
            processMissingJson(missingJson);
        } else {
            // Si no hay datos, ocultar overlay
            overlay.setVisibility(View.GONE);
            btnToggleOverlay.setVisibility(View.GONE);
        }

        setupClickListeners();
        setupPhotoView();
    }

    private void processMissingJson(String json) {
        try {
            JSONArray arr = new JSONArray(json);
            rectList.clear();
            List<String> labels = new ArrayList<>();

            if (currentBitmap != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.getJSONObject(i);

                    // Obtener coordenadas
                    JSONObject coords = item.getJSONObject("coordinates");
                    float x = (float) coords.getDouble("x");
                    float y = (float) coords.getDouble("y");
                    float w = (float) coords.getDouble("width");
                    float h = (float) coords.getDouble("height");

                    // Obtener etiqueta si existe
                    String label = item.optString("label", "Elemento " + (i + 1));

                    float left = (x - w / 2f) * currentBitmap.getWidth();
                    float top = (y - h / 2f) * currentBitmap.getHeight();
                    float right = (x + w / 2f) * currentBitmap.getWidth();
                    float bottom = (y + h / 2f) * currentBitmap.getHeight();

                    rectList.add(new RectF(left, top, right, bottom));
                    labels.add(label);
                }

                overlay.setRectanglesWithLabels(rectList, labels);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar datos de detección", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPhotoView() {
        // Configurar comportamiento del PhotoView
        photoView.setMaximumScale(8.0f); // Zoom máximo
        photoView.setMediumScale(4.0f);  // Zoom medio
        photoView.setMinimumScale(1.0f); // Zoom mínimo

        // Configurar listener para toques en la foto
        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                // Comportamiento al tocar la foto
                // Podrías hacer zoom automático si lo deseas
                // Por ejemplo, alternar entre zoom 1x y 3x
                float currentScale = photoView.getScale();
                if (currentScale < 2.0f) {
                    photoView.setScale(3.0f, x, y, true);
                } else {
                    photoView.setScale(1.0f, true);
                }
            }
        });

        // Configurar listener para toques largos
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Guardar imagen o compartir
                Toast.makeText(ResultadoActivity.this, "Mantén presionado para opciones", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setupClickListeners() {
        // Botón para retroceder
        btnBack.setOnClickListener(v -> finish());

        // Botón para alternar visibilidad de rectángulos
        btnToggleOverlay.setOnClickListener(v -> {
            showOverlay = !showOverlay;
            overlay.setVisibility(showOverlay ? View.VISIBLE : View.GONE);
            // Cambiar icono según estado
            if (showOverlay) {
                btnToggleOverlay.setImageResource(R.drawable.ic_visibility_on);
            } else {
                btnToggleOverlay.setImageResource(R.drawable.ic_visibility_off);
            }
        });

        // Botón para zoom 100% (reset)
        btnZoom.setOnClickListener(v -> {
            photoView.setScale(1.0f, true);
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Esconde la barra de navegación y la barra de estado
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar memoria de bitmap si es necesario
        if (currentBitmap != null && !currentBitmap.isRecycled()) {
            currentBitmap.recycle();
            currentBitmap = null;
        }
    }
}
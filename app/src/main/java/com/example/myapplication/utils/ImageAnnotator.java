// app/utils/ImageAnnotator.java
package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import java.util.List;

public class ImageAnnotator {

    private Paint boundingBoxPaint;
    private Paint textPaint;
    private Paint textBackgroundPaint;

    public ImageAnnotator() {
        setupPaints();
    }

    private void setupPaints() {
        // Pintura para bounding boxes
        boundingBoxPaint = new Paint();
        boundingBoxPaint.setColor(Color.RED);
        boundingBoxPaint.setStyle(Paint.Style.STROKE);
        boundingBoxPaint.setStrokeWidth(5f);
        boundingBoxPaint.setAlpha(200);

        // Pintura para texto
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setFakeBoldText(true);

        // Pintura para fondo de texto
        textBackgroundPaint = new Paint();
        textBackgroundPaint.setColor(Color.argb(180, 255, 0, 0)); // Rojo semitransparente
        textBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    public Bitmap drawAnnotations(Bitmap originalBitmap, List<AnnotatedItem> items) {
        if (originalBitmap == null || items == null || items.isEmpty()) {
            return originalBitmap;
        }

        try {
            // Crear una copia mutable para dibujar
            Bitmap mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            int imageWidth = mutableBitmap.getWidth();
            int imageHeight = mutableBitmap.getHeight();

            for (AnnotatedItem item : items) {
                drawItem(canvas, item, imageWidth, imageHeight);
            }

            return mutableBitmap;
        } catch (Exception e) {
            Log.e("ImageAnnotator", "Error dibujando anotaciones: " + e.getMessage());
            return originalBitmap;
        }
    }

    private void drawItem(Canvas canvas, AnnotatedItem item, int imageWidth, int imageHeight) {
        // Convertir coordenadas normalizadas a píxeles
        // Usar getCenterX() y getCenterY() en lugar de getX() y getY()
        float centerX = item.getCenterX() * imageWidth;
        float centerY = item.getCenterY() * imageHeight;
        float width = item.getWidth() * imageWidth;
        float height = item.getHeight() * imageHeight;

        // Calcular esquinas del rectángulo basado en el centro
        float left = centerX - (width / 2);
        float top = centerY - (height / 2);
        float right = left + width;
        float bottom = top + height;

        // Asegurarse de que el rectángulo no se salga de los bordes
        left = Math.max(10, left);
        top = Math.max(10, top);
        right = Math.min(imageWidth - 10, right);
        bottom = Math.min(imageHeight - 10, bottom);

        // Dibujar rectángulo
        canvas.drawRect(left, top, right, bottom, boundingBoxPaint);

        // Dibujar texto con el nombre del elemento
        String itemName = item.getDisplayName() != null ? item.getDisplayName() : item.getName();
        drawTextWithBackground(canvas, itemName, left, top, imageWidth);
    }

    private void drawTextWithBackground(Canvas canvas, String text, float left, float top, int imageWidth) {
        // Ajustar posición del texto para que no se salga de la imagen
        float textX = left;
        float textY = top - 15;

        // Si está muy cerca del borde superior, poner el texto abajo
        if (textY < 40) {
            textY = top + 60;
        }

        // Si está muy cerca del borde derecho, ajustar
        if (textX > imageWidth - 200) {
            textX = imageWidth - 200;
        }

        // Medir el texto para dibujar fondo
        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();

        // Dibujar fondo redondeado para el texto
        RectF textBg = new RectF(
                textX - 8,
                textY - textHeight + 10,
                textX + textWidth + 12,
                textY + 8
        );

        canvas.drawRoundRect(textBg, 10, 10, textBackgroundPaint);

        // Dibujar el texto
        canvas.drawText(text, textX, textY, textPaint);

        // Dibujar icono de advertencia
        Paint warningPaint = new Paint();
        warningPaint.setColor(Color.YELLOW);
        warningPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(textX - 25, textY - 15, 15, warningPaint);

        Paint exclamationPaint = new Paint();
        exclamationPaint.setColor(Color.BLACK);
        exclamationPaint.setTextSize(20);
        exclamationPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("!", textX - 25, textY - 8, exclamationPaint);
    }
}
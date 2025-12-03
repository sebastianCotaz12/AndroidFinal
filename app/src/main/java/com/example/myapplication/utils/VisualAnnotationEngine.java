// app/utils/VisualAnnotationEngine.java
package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import java.util.List;

public class VisualAnnotationEngine {

    private Paint boxPaint;
    private Paint textPaint;
    private Paint bgPaint;
    private Paint arrowPaint;
    private Paint highlightPaint;
    private Paint numberPaint;

    public VisualAnnotationEngine() {
        initializePaints();
    }

    private void initializePaints() {
        // Pintura para rectángulos/cajas
        boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(8f);
        boxPaint.setAlpha(220);

        // Pintura para texto
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(42);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        // Pintura para fondo de texto
        bgPaint = new Paint();
        bgPaint.setColor(Color.argb(200, 255, 50, 50)); // Rojo semitransparente
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);

        // Pintura para flechas
        arrowPaint = new Paint();
        arrowPaint.setColor(Color.YELLOW);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setStrokeWidth(5f);
        arrowPaint.setAlpha(180);

        // Pintura para resaltado
        highlightPaint = new Paint();
        highlightPaint.setColor(Color.argb(100, 255, 255, 0)); // Amarillo semitransparente
        highlightPaint.setStyle(Paint.Style.FILL);

        // Pintura para números
        numberPaint = new Paint();
        numberPaint.setColor(Color.YELLOW);
        numberPaint.setTextSize(38);
        numberPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        numberPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Anota la imagen con elementos faltantes
     */
    public Bitmap annotateImage(Bitmap originalImage, List<AnnotatedItem> missingItems, String context) {
        if (originalImage == null || missingItems == null || missingItems.isEmpty()) {
            return originalImage;
        }

        try {
            // Crear copia editable
            Bitmap annotatedImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(annotatedImage);

            int width = annotatedImage.getWidth();
            int height = annotatedImage.getHeight();

            // Ordenar por prioridad (críticos primero)
            missingItems.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));

            // Dibujar cada elemento faltante
            for (int i = 0; i < missingItems.size(); i++) {
                AnnotatedItem item = missingItems.get(i);
                drawItemAnnotation(canvas, item, width, height, i + 1);
            }

            // Agregar leyenda en la parte inferior
            drawLegend(canvas, width, height, missingItems.size(), context);

            return annotatedImage;

        } catch (Exception e) {
            Log.e("AnnotationEngine", "Error anotando imagen: " + e.getMessage());
            return originalImage;
        }
    }

    private void drawItemAnnotation(Canvas canvas, AnnotatedItem item, int width, int height, int itemNumber) {
        // Convertir coordenadas normalizadas a píxeles
        float centerX = item.getCenterX() * width;
        float centerY = item.getCenterY() * height;
        float boxWidth = item.getWidth() * width;
        float boxHeight = item.getHeight() * height;

        // Calcular esquinas del rectángulo
        float left = centerX - (boxWidth / 2);
        float top = centerY - (boxHeight / 2);
        float right = left + boxWidth;
        float bottom = top + boxHeight;

        // Asegurar que esté dentro de la imagen
        left = Math.max(10, left);
        top = Math.max(10, top);
        right = Math.min(width - 10, right);
        bottom = Math.min(height - 10, bottom);

        // Dibujar rectángulo de fondo (resaltado)
        RectF highlightRect = new RectF(left - 5, top - 5, right + 5, bottom + 5);
        canvas.drawRoundRect(highlightRect, 15, 15, highlightPaint);

        // Dibujar rectángulo principal
        RectF boxRect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(boxRect, 10, 10, boxPaint);

        // Dibujar número en la esquina superior izquierda
        String numberText = "" + itemNumber;
        float numberX = left + 30;
        float numberY = top + 40;

        // Fondo circular para el número
        Paint numberBg = new Paint();
        numberBg.setColor(item.getPriority() == 1 ? Color.RED : Color.argb(200, 255, 140, 0));
        canvas.drawCircle(left + 30, top + 30, 25, numberBg);

        // Texto del número
        canvas.drawText(numberText, numberX, numberY, numberPaint);

        // Dibujar flecha apuntando al elemento
        drawArrow(canvas, left - 50, top - 50, left, top);

        // Dibujar etiqueta con nombre
        drawItemLabel(canvas, item.getDisplayName(), left, top, itemNumber);
    }

    private void drawArrow(Canvas canvas, float fromX, float fromY, float toX, float toY) {
        // Ajustar posición si se sale de la imagen
        if (fromX < 0) fromX = 20;
        if (fromY < 0) fromY = 20;

        // Dibujar línea
        arrowPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(fromX, fromY, toX, toY, arrowPaint);

        // Dibujar punta de flecha (triángulo)
        arrowPaint.setStyle(Paint.Style.FILL);

        float angle = (float) Math.atan2(toY - fromY, toX - fromX);
        float arrowLength = 30f;
        float arrowAngle = (float) Math.toRadians(30);

        float x1 = toX - arrowLength * (float) Math.cos(angle - arrowAngle);
        float y1 = toY - arrowLength * (float) Math.sin(angle - arrowAngle);
        float x2 = toX - arrowLength * (float) Math.cos(angle + arrowAngle);
        float y2 = toY - arrowLength * (float) Math.sin(angle + arrowAngle);

        canvas.drawLine(toX, toY, x1, y1, arrowPaint);
        canvas.drawLine(toX, toY, x2, y2, arrowPaint);
        canvas.drawLine(x1, y1, x2, y2, arrowPaint);
    }

    private void drawItemLabel(Canvas canvas, String label, float left, float top, int itemNumber) {
        // Posición de la etiqueta (arriba a la izquierda del rectángulo)
        float labelX = left - 10;
        float labelY = top - 20 - (itemNumber * 5); // Evitar superposición

        // Si se sale por arriba, poner abajo
        if (labelY < 50) {
            labelY = top + 120;
        }

        String labelText = "❌ " + label;

        // Medir texto
        float textWidth = textPaint.measureText(labelText);
        float textHeight = textPaint.getTextSize();

        // Fondo redondeado para el texto
        RectF textBg = new RectF(
                labelX - 15,
                labelY - textHeight + 15,
                labelX + textWidth + 20,
                labelY + 20
        );

        canvas.drawRoundRect(textBg, 25, 25, bgPaint);

        // Dibujar texto
        canvas.drawText(labelText, labelX, labelY, textPaint);
    }

    private void drawLegend(Canvas canvas, int width, int height, int missingCount, String context) {
        // Fondo de la leyenda
        Paint legendBg = new Paint();
        legendBg.setColor(Color.argb(220, 0, 0, 0)); // Negro semitransparente
        legendBg.setStyle(Paint.Style.FILL);

        RectF legendRect = new RectF(20, height - 180, width - 20, height - 20);
        canvas.drawRoundRect(legendRect, 20, 20, legendBg);

        // Texto de la leyenda
        Paint legendText = new Paint();
        legendText.setColor(Color.WHITE);
        legendText.setTextSize(34);
        legendText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        legendText.setTextAlign(Paint.Align.LEFT);

        String contextName = LocalAnnotationManager.getInstance().getContextDisplayName(context);
        String title = "📋 ELEMENTOS FALTANTES (" + contextName + ")";
        canvas.drawText(title, 40, height - 140, legendText);

        // Contador
        Paint countPaint = new Paint();
        countPaint.setColor(Color.RED);
        countPaint.setTextSize(48);
        countPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        String countText = missingCount + " faltantes";
        canvas.drawText(countText, 40, height - 80, countPaint);

        // Indicador de prioridad
        Paint priorityPaint = new Paint();
        priorityPaint.setColor(Color.YELLOW);
        priorityPaint.setTextSize(30);

        String priorityText = "⚠️ Números indican prioridad (1 = CRÍTICO)";
        canvas.drawText(priorityText, 40, height - 40, priorityPaint);
    }
}
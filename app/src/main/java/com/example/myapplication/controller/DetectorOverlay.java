package com.example.myapplication.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DetectorOverlay extends View {

    private final Paint rectPaint;
    private final Paint textPaint;
    private final Paint fillPaint;
    private final List<RectF> rectList = new ArrayList<>();
    private final List<String> labels = new ArrayList<>();

    public DetectorOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Pintura para borde de rectángulo
        rectPaint = new Paint();
        rectPaint.setColor(ContextCompat.getColor(context, R.color.detection_red));
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4f);
        rectPaint.setAntiAlias(true);

        // Pintura para fondo semitransparente
        fillPaint = new Paint();
        fillPaint.setColor(ContextCompat.getColor(context, R.color.detection_red_alpha));
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);

        // Pintura para texto
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36f);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer(4f, 2f, 2f, Color.BLACK);
    }

    public void setRectangles(List<RectF> rects) {
        rectList.clear();
        rectList.addAll(rects);
        invalidate();
    }

    public void setRectanglesWithLabels(List<RectF> rects, List<String> labels) {
        rectList.clear();
        this.labels.clear();
        rectList.addAll(rects);
        this.labels.addAll(labels);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < rectList.size(); i++) {
            RectF rect = rectList.get(i);

            // Dibujar fondo semitransparente
            canvas.drawRect(rect, fillPaint);

            // Dibujar borde
            canvas.drawRect(rect, rectPaint);

            // Dibujar etiqueta si existe
            if (i < labels.size() && labels.get(i) != null) {
                String label = labels.get(i);
                float textWidth = textPaint.measureText(label);

                // Fondo para el texto
                Paint textBgPaint = new Paint();
                textBgPaint.setColor(ContextCompat.getColor(getContext(), R.color.detection_red));
                textBgPaint.setStyle(Paint.Style.FILL);

                float textLeft = rect.left;
                float textTop = rect.top - 10; // Un poco arriba del rectángulo
                float textRight = textLeft + textWidth + 20;
                float textBottom = rect.top + 40;

                canvas.drawRoundRect(textLeft, textTop, textRight, textBottom, 8f, 8f, textBgPaint);

                // Dibujar texto
                canvas.drawText(label, rect.left + 10, rect.top + 30, textPaint);
            }

            // Dibujar esquinas decorativas
            drawCorners(canvas, rect);
        }
    }

    private void drawCorners(Canvas canvas, RectF rect) {
        float cornerSize = 20f;
        Paint cornerPaint = new Paint();
        cornerPaint.setColor(Color.WHITE);
        cornerPaint.setStrokeWidth(4f);

        // Esquina superior izquierda
        canvas.drawLine(rect.left, rect.top, rect.left + cornerSize, rect.top, cornerPaint);
        canvas.drawLine(rect.left, rect.top, rect.left, rect.top + cornerSize, cornerPaint);

        // Esquina superior derecha
        canvas.drawLine(rect.right - cornerSize, rect.top, rect.right, rect.top, cornerPaint);
        canvas.drawLine(rect.right, rect.top, rect.right, rect.top + cornerSize, cornerPaint);

        // Esquina inferior izquierda
        canvas.drawLine(rect.left, rect.bottom - cornerSize, rect.left, rect.bottom, cornerPaint);
        canvas.drawLine(rect.left, rect.bottom, rect.left + cornerSize, rect.bottom, cornerPaint);

        // Esquina inferior derecha
        canvas.drawLine(rect.right - cornerSize, rect.bottom, rect.right, rect.bottom, cornerPaint);
        canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - cornerSize, cornerPaint);
    }
}
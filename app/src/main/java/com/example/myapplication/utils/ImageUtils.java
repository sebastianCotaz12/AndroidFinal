package com.example.myapplication.utils;

import android.graphics.Bitmap;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ImageUtils {

    public static MultipartBody.Part bitmapToMultipart(Bitmap bitmap, String fieldName) {
        // Convertir bitmap a archivo temporal
        File file = bitmapToFile(bitmap);

        // Crear RequestBody para el archivo
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/jpeg"),
                file
        );

        // Crear MultipartBody.Part
        return MultipartBody.Part.createFormData(fieldName, file.getName(), requestFile);
    }

    private static File bitmapToFile(Bitmap bitmap) {
        try {
            // Crear archivo temporal
            File file = File.createTempFile("image", ".jpg");

            // Comprimir bitmap a archivo
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            byte[] bitmapData = bos.toByteArray();

            // Escribir bytes al archivo
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
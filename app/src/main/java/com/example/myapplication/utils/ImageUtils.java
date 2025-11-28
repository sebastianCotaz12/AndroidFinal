package com.example.myapplication.utils;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtils {
    public static MultipartBody.Part bitmapToMultipart(Bitmap bitmap, String partName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] bytes = stream.toByteArray();

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("image/jpeg"),
                bytes
        );

        return MultipartBody.Part.createFormData(partName, "photo.jpg", requestBody);
    }
}
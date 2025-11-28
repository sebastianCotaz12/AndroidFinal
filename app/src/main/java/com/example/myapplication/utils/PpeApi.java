package com.example.myapplication.utils;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PpeApi {

    @Multipart
    @POST("ppeCheck")
    Call<PpeResponse> checkPpe(
            @Part MultipartBody.Part image,
            @Part("context") RequestBody context  // ✅ Agregar este parámetro
    );
}
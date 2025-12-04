package com.example.myapplication.utils;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PpeApi {
    @Multipart
    @POST("/ppeCheck")
    Call<PpeResponse> checkPpe(
            @Header("Authorization") String token,
            @Part("model") String model,
            @Part("context") String context,
            @Part MultipartBody.Part image
    );

}
package com.example.myapplication.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://backsst.onrender.com/";
    private static Retrofit retrofit = null;
    private static String currentToken = "";

    public static Retrofit getClient(String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token) // token JWT
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        //  validación del token token
        if (retrofit == null || !currentToken.equals(token)) {
            currentToken = token;
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://backsst.onrender.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}

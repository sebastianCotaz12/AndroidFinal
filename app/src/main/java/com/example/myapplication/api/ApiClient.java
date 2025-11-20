package com.example.myapplication.api;

import android.util.Log;

import com.example.myapplication.utils.PrefsManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 🌐 URL base del backend
    private static final String BASE_URL = "https://unreproaching-rancorously-evelina.ngrok-free.dev ";

    // Instancia única (Singleton)
    private static Retrofit retrofit = null;
    private static PrefsManager lastPrefsManager = null;

    /**
     *  Método clásico de compatibilidad (sin token)
     */
    public static Retrofit getClient() {
        return getClient(null);
    }

    /**
     * ✅Obtiene el cliente Retrofit con el token JWT (si existe)
     */
    public static Retrofit getClient(PrefsManager prefsManager) {
        // 🔸 Si el PrefsManager cambió o Retrofit es nulo, se reconstruye
        if (retrofit == null || prefsManager != lastPrefsManager) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);

            // 🔹 Agregamos el interceptor solo si hay token
            if (prefsManager != null && prefsManager.getToken() != null) {
                String token = prefsManager.getToken();
                httpClient.addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .method(original.method(), original.body());
                    Log.d("ApiClient", "✅ Token agregado al header Authorization");
                    return chain.proceed(requestBuilder.build());
                });
            } else {
                // Si no hay token, logueamos advertencia
                httpClient.addInterceptor(chain -> {
                    Log.w("ApiClient", "⚠️ Petición sin token (usuario no autenticado)");
                    return chain.proceed(chain.request());
                });
            }

            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            lastPrefsManager = prefsManager;
        }

        return retrofit;
    }

    /**
     * 🔄 Resetea Retrofit (por ejemplo al cerrar sesión)
     */
    public static void resetClient() {
        retrofit = null;
        lastPrefsManager = null;
        Log.i("ApiClient", "🔄 Retrofit reiniciado correctamente");
    }

    /**
     * ✅ Obtiene ApiService sin token
     */
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    /**
     * ✅ Obtiene ApiService con token (automático desde PrefsManager)
     */
    public static ApiService getApiService(PrefsManager prefsManager) {
        return getClient(prefsManager).create(ApiService.class);
    }
}

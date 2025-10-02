package com.example.myapplication.controller;

import android.content.Context;
import android.widget.Toast;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AreaController {

    private final ApiService apiService;

    public AreaController() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void listarAreas(Context context, AreaCallback callback) {
        apiService.listarAreas().enqueue(new Callback<List<Area>>() {
            @Override
            public void onResponse(Call<List<Area>> call, Response<List<Area>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Toast.makeText(context, "Error al traer áreas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Area>> call, Throwable t) {
                Toast.makeText(context, "Fallo conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface AreaCallback {
        void onSuccess(List<Area> areas);
    }
}

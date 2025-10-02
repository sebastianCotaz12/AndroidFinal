package com.example.myapplication.controller;

import android.content.Context;
import android.widget.Toast;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiResponse;
import com.example.myapplication.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpresaController {

    private final ApiService apiService;

    public EmpresaController() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void listarEmpresas(Context context, EmpresaCallback callback) {
        apiService.listarEmpresas().enqueue(new Callback<ApiResponse<List<Empresa>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Empresa>>> call, Response<ApiResponse<List<Empresa>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getDatos());
                } else {
                    Toast.makeText(context, "Error al traer empresas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Empresa>>> call, Throwable t) {
                Toast.makeText(context, "Fallo conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface EmpresaCallback {
        void onSuccess(List<Empresa> empresas);
    }
}

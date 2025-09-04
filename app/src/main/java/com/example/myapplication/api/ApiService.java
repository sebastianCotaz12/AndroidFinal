package com.example.myapplication.api;

import com.example.myapplication.controller.crear_gestionEpp;
import com.example.myapplication.controller.crear_listaChequeo;
import com.example.myapplication.controller.crear_reportes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("crearListaChequeo")
    Call<ApiResponse<crear_listaChequeo>> crearListaChequeo(@Body crear_listaChequeo lista);
    @GET("listarListasChequeo")
    Call<List<crear_listaChequeo>> getListasChequeo();

    @POST("crearGestion")
    Call<ApiResponse<crear_gestionEpp>> crearGestion(@Body crear_gestionEpp gestion);

    @GET("listarGestiones")
    Call<List<crear_gestionEpp>> getGestiones();

    @POST("crearReporte ")
    Call<ApiResponse<crear_reportes>> crearReporte(@Body crear_reportes reporte);

    @GET("listarReportes")
    Call<List<crear_reportes>> getReportes();

}


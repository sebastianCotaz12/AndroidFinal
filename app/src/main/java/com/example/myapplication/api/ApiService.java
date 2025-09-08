package com.example.myapplication.api;

import com.example.myapplication.controller.Crear_actLudica;
import com.example.myapplication.controller.Crear_gestionEpp;
import com.example.myapplication.controller.Crear_listaChequeo;
import com.example.myapplication.controller.Crear_reportes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("crearListaChequeo")
    Call<ApiResponse<Crear_listaChequeo>> crearListaChequeo(@Body Crear_listaChequeo lista);
    @GET("listarListasChequeo")
    Call<List<Crear_listaChequeo>> getListasChequeo();

    @POST("crearGestion")
    Call<ApiResponse<Crear_gestionEpp>> crearGestion(@Body Crear_gestionEpp gestion);

    @GET("listarGestiones")
    Call<List<Crear_gestionEpp>> getGestiones();

    @POST("crearReporte ")
    Call<ApiResponse<Crear_reportes>> crearReporte(@Body Crear_reportes reporte);

    @GET("listarReportes")
    Call<List<Crear_reportes>> getReportes();

    @POST ("crearActividadLudica")
    Call<ApiResponse<Crear_actLudica>> crearActividad(@Body Crear_actLudica actividad);

    @GET("listarActividadesLudicas")
    Call<List<Crear_actLudica>> getActividad ();

}


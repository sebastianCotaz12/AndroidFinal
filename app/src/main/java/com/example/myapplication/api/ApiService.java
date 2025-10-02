package com.example.myapplication.api;

import com.example.myapplication.controller.Crear_actLudica;
import com.example.myapplication.controller.Crear_gestionEpp;
import com.example.myapplication.controller.Crear_listaChequeo;
import com.example.myapplication.controller.Crear_reportes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    // Login
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Lista Chequeo
    @POST("crearListaChequeo")
    Call<ApiResponse<Crear_listaChequeo>> crearListaChequeo(@Body Crear_listaChequeo lista);

    @GET("listarListasChequeo")
    Call<List<Crear_listaChequeo>> getListasChequeo();

    // Gestión EPP
    @POST("crearGestion")
    Call<ApiResponse<Crear_gestionEpp>> crearGestion(@Body Crear_gestionEpp gestion);

    @GET("listarGestiones")
    Call<List<Crear_gestionEpp>> getGestiones();

    // Reportes
    @POST("crearReporte")
    Call<ApiResponse<Crear_reportes>> crearReporte(@Body Crear_reportes reporte);

    @GET("listarReportes")
    Call<List<Crear_reportes>> getReportes();

    // Reportes con subida de archivos
    @Multipart
    @POST("crearReporte")
    Call<ApiResponse<Crear_reportes>> crearReporteMultipart(
            @Part("id_usuario") RequestBody idUsuario,
            @Part("nombre_usuario") RequestBody nombreUsuario,
            @Part("cargo") RequestBody cargo,
            @Part("cedula") RequestBody cedula,
            @Part("fecha") RequestBody fecha,
            @Part("lugar") RequestBody lugar,
            @Part("descripcion") RequestBody descripcion,
            @Part("estado") RequestBody estado,
            @Part MultipartBody.Part imagen,
            @Part MultipartBody.Part archivo
    );

    // Actividades Lúdicas
    @POST("crearActividadLudica")
    Call<ApiResponse<Crear_actLudica>> crearActividad(@Body Crear_actLudica actividad);

    @GET("listarActividadesLudicas")
    Call<List<Crear_actLudica>> getActividad();

    // Recuperar contraseña
    @POST("forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest request);
}

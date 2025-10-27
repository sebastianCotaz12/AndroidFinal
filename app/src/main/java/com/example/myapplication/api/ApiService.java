package com.example.myapplication.api;

import com.example.myapplication.controller.Area;
import com.example.myapplication.controller.Crear_actLudica;
import com.example.myapplication.controller.Crear_eventos;
import com.example.myapplication.controller.Crear_gestionEpp;
import com.example.myapplication.controller.Crear_listaChequeo;
import com.example.myapplication.controller.Crear_registro;
import com.example.myapplication.controller.Crear_reportes;
import com.example.myapplication.controller.Empresa;
import com.example.myapplication.controller.Item_eventos;
import com.example.myapplication.controller.Producto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // Login
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Lista Chequeo
    @POST("crearListaChequeo")
    Call<ApiResponse<Crear_listaChequeo>> crearListaChequeo(@Body Crear_listaChequeo lista);

    @GET("listarListasChequeo")
    Call<List<Crear_listaChequeo>> getListasChequeo();

    // GESTIÓN EPP (sin imágenes)

    @POST("crearGestion")
    Call<ApiResponse<Crear_gestionEpp>> crearGestionEpp(@Body Crear_gestionEpp gestionEpp);

    @GET("listarGestiones")
    Call<List<Crear_gestionEpp>> getGestiones();




    // Reportes (con archivos: imagen/documento)
    @Multipart
    @POST("crearReporte")
    Call<ApiResponse<Crear_reportes>> crearReporteMultipart(
            @Part("id_usuario") RequestBody idUsuario,
            @Part("id_empresa") RequestBody idEmpresa,
            @Part("nombre_usuario") RequestBody nombreUsuario,
            @Part("cargo") RequestBody cargo,
            @Part("cedula") RequestBody cedula,
            @Part("fecha") RequestBody fecha,
            @Part("lugar") RequestBody lugar,
            @Part("descripcion") RequestBody descripcion,
            @Part("estado") RequestBody estado,
            @Part MultipartBody.Part imagen,
            @Part MultipartBody.Part archivos
    );

    @GET("listarReportes")
    Call<List<Crear_reportes>> getReportes();

    @GET("listarAreas")
    Call<List<Area>> listarAreas();

    @GET("listarEmpresas")
    Call<ApiResponse<List<Empresa>>> listarEmpresas();

    @POST("register")
    Call<RegistroResponse> registrarUsuario(@Body Crear_registro nuevoUsuario);

    // 🔹 Crear Actividad Lúdica con Cloudinary (imagen/video + archivo adjunto opcional)
    @Multipart
    @POST("crearActividadLudica")
    Call<ApiResponse<Object>> crearActividadMultipart(
            @Part("nombre_actividad") RequestBody nombreActividad,
            @Part("fecha_actividad") RequestBody fechaActividad,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part imagen_video,          // Puede ser jpg, png, mp4, mov
            @Part MultipartBody.Part archivo_adjunto        // Puede ser pdf, doc, xlsx, etc. (opcional)
    );



    @GET("listarActividadesLudicas")
    Call<List<Crear_actLudica>> getActividad();

    // Recuperar contraseña
    @POST("forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest request);


    @Multipart
    @POST("blogs")
    Call<ApiResponse<Object>> crearEventoMultipart(
            @Part("titulo") RequestBody titulo,
            @Part("fecha_actividad") RequestBody fechaActividad,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part imagen
    );

    @GET("eventos")
    Call<List<Item_eventos>> getEventos();

    // Listar eventos por empresa
    @GET("eventos/empresa/{id_empresa}")
    Call<List<Crear_eventos>> getEventosPorEmpresa();

    @GET("productos/listar")
    Call<Producto[]> listarTodosLosProductos();
}

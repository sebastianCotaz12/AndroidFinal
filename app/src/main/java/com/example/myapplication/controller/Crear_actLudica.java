package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_actLudica {


    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName("nombre_actividad")
    private String nombreActividad;

    @SerializedName("fecha_actividad")
    private String fechaActividad;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("archivo_adjunto")
    private String evidencia;

    @SerializedName("imagen_video")
    private String imagenVideo;


    public Crear_actLudica() {
    }


    public Crear_actLudica(int idUsuario, String nombreUsuario, String nombreActividad, String fechaActividad, String descripcion, String evidencia, String imagenVideo) {

        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.nombreActividad = nombreActividad;
        this.fechaActividad = fechaActividad;
        this.descripcion = descripcion;
        this.evidencia = evidencia;
        this.imagenVideo = imagenVideo;
    }


    public Number getIdUsuario(){return  idUsuario;}

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public String getFechaActividad() {
        return fechaActividad;
    }

    public void setFechaActividad(String fechaActividad) {
        this.fechaActividad = fechaActividad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "CrearActividadesL{" +
                " idUsuario='" + idUsuario + '\'' +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", nombreActividad='" + nombreActividad + '\'' +
                ", fechaActividad='" + fechaActividad + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", evidencia='" + evidencia + '\'' +
                '}';
    }

    public String getImagenVideo() {
        return imagenVideo;
    }

    public void setImagenVideo(String imagenVideo) {
        this.imagenVideo = imagenVideo;
    }
}
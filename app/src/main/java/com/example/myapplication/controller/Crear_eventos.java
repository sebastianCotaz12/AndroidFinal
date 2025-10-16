package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_eventos {

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("fecha_actividad")
    private String fechaActividad;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("archivo")
    private String archivo;

    @SerializedName("imagen")
    private String imagen;

    public Crear_eventos() {
    }

    public Crear_eventos(String titulo, String fechaActividad, String descripcion, String archivo, String imagen) {
        this.titulo = titulo;
        this.fechaActividad = fechaActividad;
        this.descripcion = descripcion;
        this.archivo = archivo;
        this.imagen = imagen;
    }

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Crear_eventos{" +
                "titulo='" + titulo + '\'' +
                ", fechaActividad='" + fechaActividad + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", archivo='" + archivo + '\'' +
                ", imagen='" + imagen + '\'' +
                '}';
    }
}
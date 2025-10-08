package com.example.myapplication.controller;

public class Item_actLudicas {

    private int id;
    private String nombreUsuario;
    private String nombreActividad;
    private String fechaActividad;
    private String descripcion;
    private String archivoAdjunto;

    // Constructor
    public Item_actLudicas(int id, String nombreUsuario, String nombreActividad,
                           String fechaActividad, String descripcion, String archivoAdjunto) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.nombreActividad = nombreActividad;
        this.fechaActividad = fechaActividad;
        this.descripcion = descripcion;
        this.archivoAdjunto = archivoAdjunto;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getArchivoAdjunto() {
        return archivoAdjunto;
    }

    public void setArchivoAdjunto(String archivoAdjunto) {
        this.archivoAdjunto = archivoAdjunto;
    }
}

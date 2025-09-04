package com.example.myapplication.controller;

public class item_actLudicas {
    private int id;
    private String usuario;
    private String nombreActividad;
    private String fecha;
    private String descripcion;
    private String evidencia;
    private String imagenVideo;

    public item_actLudicas(int id, String usuario, String nombreActividad, String fecha, String descripcion, String evidencia,  String imagenVideo) {
        this.id = id;
        this.usuario = usuario;
        this.nombreActividad = nombreActividad;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.evidencia = evidencia;
        this.imagenVideo = imagenVideo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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


    public String getImagenVideo() {
        return imagenVideo;
    }

    public void setImagenVideo(String imagenVideo) {
        this.imagenVideo = imagenVideo;
    }
}
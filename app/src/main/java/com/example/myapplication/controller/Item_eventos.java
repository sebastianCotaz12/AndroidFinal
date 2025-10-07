package com.example.myapplication.controller;

public class Item_eventos {
    private String titulo;
    private String fecha;
    private String descripcion;
    private String adjuntar;
    private String imagen;

    // Constructor
    public Item_eventos(String titulo, String fecha, String descripcion, String adjuntar, String imagen) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.adjuntar = adjuntar;
        this.imagen = imagen;
    }

    // Getters
    public String getTitulo() { return titulo; }

    public String getFecha() { return fecha; }

    public String getDescripcion() { return descripcion; }

    public String getAdjuntar() { return adjuntar; }

    public String getImagen() { return imagen; }

    // Setters
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public void setFecha(String fecha) { this.fecha = fecha; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setAdjuntar(String adjuntar) { this.adjuntar = adjuntar; }

    public void setImagen(String imagen) { this.imagen = imagen; }
}

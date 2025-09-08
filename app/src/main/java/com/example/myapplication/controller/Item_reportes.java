package com.example.myapplication.controller;

public class Item_reportes {

    private int id;
    private String nombreUsuario;
    private String cargo;
    private String cedula;
    private String fecha;
    private String lugar;
    private String descripcion;
    private String imagen;
    private String archivos;
    private String estado;

    public Item_reportes(int id, String nombreUsuario, String cargo, String cedula, String fecha,
                         String lugar, String descripcion, String imagen, String archivos, String estado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.cargo = cargo;
        this.cedula = cedula;
        this.fecha = fecha;
        this.lugar = lugar;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.archivos = archivos;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCargo() {
        return cargo;
    }

    public String getCedula() {
        return cedula;
    }

    public String getFecha() {
        return fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public String getArchivos() {
        return archivos;
    }

    public String getEstado() {
        return estado;
    }
}

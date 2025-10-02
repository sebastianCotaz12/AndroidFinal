package com.example.myapplication.controller;

public class ItemReporte {

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

    // Constructor completo
    public ItemReporte(int id, String nombreUsuario, String cargo, String cedula,
                       String fecha, String lugar, String descripcion,
                       String imagen, String archivos, String estado) {
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

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getArchivos() { return archivos; }
    public void setArchivos(String archivos) { this.archivos = archivos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

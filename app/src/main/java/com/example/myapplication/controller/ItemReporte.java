package com.example.myapplication.controller;

public class ItemReporte {

    private int idReporte;
    private String nombreUsuario;
    private String cargo;
    private String cedula;
    private String fecha;
    private String lugar;
    private String descripcion;
    private String imagen;
    private String archivos;
    private String estado;

    public ItemReporte(int idReporte, String nombreUsuario, String cargo, String cedula,
                       String fecha, String lugar, String descripcion,
                       String imagen, String archivos, String estado) {
        this.idReporte = idReporte;
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

    public int getIdReporte() { return idReporte; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCargo() { return cargo; }
    public String getCedula() { return cedula; }
    public String getFecha() { return fecha; }
    public String getLugar() { return lugar; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
    public String getArchivos() { return archivos; }
    public String getEstado() { return estado; }

    public void setIdReporte(int idReporte) { this.idReporte = idReporte; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setArchivos(String archivos) { this.archivos = archivos; }
    public void setEstado(String estado) { this.estado = estado; }
}
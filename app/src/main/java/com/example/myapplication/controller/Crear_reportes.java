package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_reportes {

    @SerializedName("id_usuario")
    private Integer idUsuario;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName("cargo")
    private String cargo;

    @SerializedName("cedula")
    private String cedula;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("lugar")
    private String lugar;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("imagen")
    private String imagen;

    @SerializedName("archivos")
    private String archivos;

    @SerializedName("estado")
    private String estado;

    @SerializedName("id_empresa")
    private Integer idEmpresa;

    public Crear_reportes() {}

    public Crear_reportes(Integer idUsuario, String nombreUsuario, String cargo, String cedula,
                          String fecha, String lugar, String descripcion,
                          String imagen, String archivos, String estado, Integer idEmpresa) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.cargo = cargo;
        this.cedula = cedula;
        this.fecha = fecha;
        this.lugar = lugar;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.archivos = archivos;
        this.estado = estado;
        this.idEmpresa = idEmpresa;
    }

    // Getters y Setters
    public Integer getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCargo() { return cargo; }
    public String getCedula() { return cedula; }
    public String getFecha() { return fecha; }
    public String getLugar() { return lugar; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
    public String getArchivos() { return archivos; }
    public String getEstado() { return estado; }
    public Integer getIdEmpresa() { return idEmpresa; }

    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setLugar(String lugar) { this.lugar = lugar; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setArchivos(String archivos) { this.archivos = archivos; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }
}

package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_reportes {

    @SerializedName("id_usuario")   // 👈 agregado
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

    public Crear_reportes() {}


    public Integer getIdUsuario() { return idUsuario; }   // 👈 getter
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; } // 👈 setter

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

    public void setTitulo(String titulo) {
    }
}

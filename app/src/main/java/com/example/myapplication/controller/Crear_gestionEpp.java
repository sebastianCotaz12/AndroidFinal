package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_gestionEpp {

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("cedula")
    private String cedula;

    @SerializedName("cargo")
    private String cargo;

    @SerializedName("productos")
    private String productos;

    @SerializedName("cantidad")
    private String cantidad;

    @SerializedName("importancia")
    private String importancia;

    @SerializedName("estado")
    private String estado;

    @SerializedName("fecha_creacion")
    private String fechaCreacion;

    // 🔹 Constructor con todos los campos
    public Crear_gestionEpp(int idUsuario, String nombre, String apellido, String cedula,
                            String cargo, String productos, String cantidad,
                            String importancia, String estado, String fechaCreacion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.cargo = cargo;
        this.productos = productos;
        this.cantidad = cantidad;
        this.importancia = importancia;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    // 🔹 Constructor vacío (para Retrofit/Gson)
    public Crear_gestionEpp() {
    }

    // 🔹 Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getProductos() {
        return productos;
    }

    public void setProductos(String productos) {
        this.productos = productos;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getImportancia() {
        return importancia;
    }

    public void setImportancia(String importancia) {
        this.importancia = importancia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "GestionEpp{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", cedula='" + cedula + '\'' +
                ", cargo='" + cargo + '\'' +
                ", productos='" + productos + '\'' +
                ", cantidad='" + cantidad + '\'' +
                ", importancia='" + importancia + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaCreacion='" + fechaCreacion + '\'' +
                '}';
    }
}

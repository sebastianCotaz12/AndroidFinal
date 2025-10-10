package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_gestionEpp {

    @SerializedName("cedula")
    private String cedula;

    @SerializedName("id_cargo")
    private int idCargo;

    @SerializedName("importancia")
    private String importancia;

    @SerializedName("estado")
    private String estado;

    @SerializedName("cantidad")
    private int cantidad;

    @SerializedName("id_area")
    private int idArea;

    @SerializedName("productos")
    private int[] productos;

    // Constructor vacío
    public Crear_gestionEpp() {}

    // Constructor completo
    public Crear_gestionEpp(String cedula, int idCargo, String importancia, String estado, int cantidad, int idArea, int[] productos) {
        this.cedula = cedula;
        this.idCargo = idCargo;
        this.importancia = importancia;
        this.estado = estado;
        this.cantidad = cantidad;
        this.idArea = idArea;
        this.productos = productos;
    }

    // Getters y setters
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public int[] getProductos() {
        return productos;
    }

    public void setProductos(int[] productos) {
        this.productos = productos;
    }
}

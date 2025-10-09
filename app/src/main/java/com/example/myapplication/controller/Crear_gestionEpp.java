package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_gestionEpp {

    @SerializedName("cedula")
    private String cedula;

    @SerializedName("importancia")
    private String importancia;

    @SerializedName("estado")
    private String estado;

    @SerializedName("productos")
    private int[] productos;

    @SerializedName("id_cargo")
    private int idCargo;

    @SerializedName("id_area")
    private int idArea;

    @SerializedName("cantidad")
    private int cantidad;

    public Crear_gestionEpp(String cedula, String importancia, String estado,
                            int[] productos, int idCargo, int idArea, int cantidad) {
        this.cedula = cedula;
        this.importancia = importancia;
        this.estado = estado;
        this.productos = productos;
        this.idCargo = idCargo;
        this.idArea = idArea;
        this.cantidad = cantidad;
    }

    public Crear_gestionEpp() {}

    // Getters y setters
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getImportancia() { return importancia; }
    public void setImportancia(String importancia) { this.importancia = importancia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int[] getProductos() { return productos; }
    public void setProductos(int[] productos) { this.productos = productos; }

    public int getIdCargo() { return idCargo; }
    public void setIdCargo(int idCargo) { this.idCargo = idCargo; }

    public int getIdArea() { return idArea; }
    public void setIdArea(int idArea) { this.idArea = idArea; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}

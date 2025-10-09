package com.example.myapplication.controller;

public class Item_gestionEpp {
    private int id;
    private String cedula;
    private String importancia;
    private String estado;
    private String fecha_creacion;
    private String productos;
    private String cargo;
    private String area;
    private int cantidad;

    public Item_gestionEpp(int id, String cedula, String importancia, String estado,
                           String fecha_creacion, String productos, String cargo,
                           String area, int cantidad) {
        this.id = id;
        this.cedula = cedula;
        this.importancia = importancia;
        this.estado = estado;
        this.fecha_creacion = fecha_creacion;
        this.productos = productos;
        this.cargo = cargo;
        this.area = area;
        this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public String getCedula() { return cedula; }
    public String getImportancia() { return importancia; }
    public String getEstado() { return estado; }
    public String getFecha_creacion() { return fecha_creacion; }
    public String getProductos() { return productos; }
    public String getCargo() { return cargo; }
    public String getArea() { return area; }
    public int getCantidad() { return cantidad; }
}

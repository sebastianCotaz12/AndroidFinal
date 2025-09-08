package com.example.myapplication.controller;

public class Item_gestionEpp {
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String cedula;
    private String cargo;
    private String productos;
    private int cantidad;
    private String importancia;
    private String estado;
    private String fechaCreacion;

    // Constructor
    public Item_gestionEpp(int idUsuario, String nombre, String apellido, String cedula,
                           String cargo, String productos, int cantidad, String importancia,
                           String estado, String fechaCreacion) {
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

    // Getters
    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCedula() { return cedula; }
    public String getCargo() { return cargo; }
    public String getProductos() { return productos; }
    public int getCantidad() { return cantidad; }
    public String getImportancia() { return importancia; }
    public String getEstado() { return estado; }
    public String getFechaCreacion() { return fechaCreacion; }
}

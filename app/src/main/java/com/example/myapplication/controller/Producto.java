package com.example.myapplication.controller;

// Clase Producto para el spinner
public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private boolean estado;

    public int getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isEstado() { return estado; }

    @Override
    public String toString() {
        return nombre; // Muestra el nombre en el spinner
    }
}
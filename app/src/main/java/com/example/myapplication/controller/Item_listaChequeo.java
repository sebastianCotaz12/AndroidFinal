package com.example.myapplication.controller;

public class Item_listaChequeo {

    private String nombre;
    private String fecha;
    private String hora;
    private String modelo;
    private String marca;
    private String soat;
    private String tecnico;
    private String kilometraje;
    private String placa; // 🔹 Nuevo campo
    private String observaciones; // 🔹 Nuevo campo

    public Item_listaChequeo(String nombre, String fecha, String hora, String modelo, String marca,
                             String soat, String tecnico, String kilometraje,
                             String placa, String observaciones) { // 🔹 Constructor actualizado
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.modelo = modelo;
        this.marca = marca;
        this.soat = soat;
        this.tecnico = tecnico;
        this.kilometraje = kilometraje;
        this.placa = placa;
        this.observaciones = observaciones;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getModelo() {
        return modelo;
    }

    public String getMarca() {
        return marca;
    }

    public String getSoat() {
        return soat;
    }

    public String getTecnico() {
        return tecnico;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public String getPlaca() { // 🔹 Getter nuevo
        return placa;
    }

    public String getObservaciones() { // 🔹 Getter nuevo
        return observaciones;
    }
}

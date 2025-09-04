package com.example.myapplication.controller;

public class item_listaChequeo {

    private String nombre;
    private String fecha;
    private String hora;
    private String modelo;
    private String marca;
    private String soat;
    private String tecnico;
    private String kilometraje;

    public item_listaChequeo(String nombre, String fecha, String hora, String modelo, String marca, String soat, String tecnico, String kilometraje) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.modelo = modelo;
        this.marca = marca;
        this.soat = soat;
        this.tecnico = tecnico;
        this.kilometraje = kilometraje;
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
}

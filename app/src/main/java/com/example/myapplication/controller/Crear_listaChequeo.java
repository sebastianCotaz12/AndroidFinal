package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_listaChequeo {

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("usuario_nombre")
    private String usuarioNombre;

    @SerializedName("cargo")
    private String cargo;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("hora")
    private String hora;

    @SerializedName("modelo")
    private String modelo;

    @SerializedName("marca")
    private String marca;

    @SerializedName("soat")
    private String soat;

    @SerializedName("tecnico")
    private String tecnico;

    @SerializedName("kilometraje")
    private String kilometraje;

    // 🔹 Nuevos campos agregados
    @SerializedName("placa")
    private String placa;

    @SerializedName("observaciones")
    private String observaciones;


    public Crear_listaChequeo(int idUsuario, String usuarioNombre,String cargo, String fecha, String hora,
                              String modelo, String marca, String soat, String tecnico,
                              String kilometraje, String placa, String observaciones) {
        this.idUsuario = idUsuario;
        this.usuarioNombre = usuarioNombre;
        this.cargo = cargo;
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

    public Crear_listaChequeo() {

    }

    // 🔹 Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getSoat() {
        return soat;
    }

    public void setSoat(String soat) {
        this.soat = soat;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "ListaChequeo{" +
                "idUsuario=" + idUsuario +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", modelo='" + modelo + '\'' +
                ", marca='" + marca + '\'' +
                ", soat='" + soat + '\'' +
                ", tecnico='" + tecnico + '\'' +
                ", kilometraje='" + kilometraje + '\'' +
                ", placa='" + placa + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}

package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class crear_listaChequeo {

    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("usuario_nombre")
    private String usuarioNombre;

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


    public crear_listaChequeo(int idUsuario, String usuarioNombre, String fecha, String hora,
                             String modelo, String marca, String soat, String tecnico,
                             String kilometraje) {
        this.idUsuario = idUsuario;
        this.usuarioNombre = usuarioNombre;
        this.fecha = fecha;
        this.hora = hora;
        this.modelo = modelo;
        this.marca = marca;
        this.soat = soat;
        this.tecnico = tecnico;
        this.kilometraje = kilometraje;
    }

    public crear_listaChequeo() {

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
                '}';
    }
}

package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_eventos {

    @SerializedName("tituloEvento")
    private String tituloEvento;

    @SerializedName("fechaEvento")
    private String fechaEvento;

    @SerializedName("descripcionEvento")
    private String descripcionEvento;

    @SerializedName("adjuntarEvento")
    private String adjuntarEvento;

    @SerializedName("imagen")
    private String imagen;

    public  Crear_eventos(){
    }




    public Crear_eventos(String tituloEvento, String fechaEvento, String descripcionEvento, String adjuntarEvento, String imagen) {

        this.tituloEvento = tituloEvento;
        this.fechaEvento = fechaEvento;
        this.descripcionEvento = descripcionEvento;
        this.adjuntarEvento = adjuntarEvento;
        this.imagen = imagen;
    }



    public String getTituloEvento() {
        return tituloEvento;
    }

    public void setTituloEvento(String tituloEvento) {
        this.tituloEvento = tituloEvento;
    }

    public String getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(String fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getDescripcionEvento() {
        return descripcionEvento;
    }

    public void setDescripcionEvento(String descripcionEvento) {
        this.descripcionEvento = descripcionEvento;
    }

    public String getAdjuntarEvento() {
        return adjuntarEvento;
    }

    public void setAdjuntarEvento(String adjuntarEvento) {
        this.adjuntarEvento = adjuntarEvento;
    }

    @Override
    public String toString() {
        return "CrearEventos{" +
                ", tituloEvento='" + tituloEvento + '\'' +
                ", fechaEvento='" + fechaEvento + '\'' +
                ", descripcionEvento='" + descripcionEvento + '\'' +
                ", adjuntarEvento='" +adjuntarEvento + '\'' +
                '}';
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_eventos {

    @SerializedName("tituloEvento")
    private String tituloEvento;

    @SerializedName("fechaEvento")
    private String fechaEvento;

    @SerializedName("descripcionEvento")
    private String descripcionEvento;

    @SerializedName("adjuntar")
    private String adjuntar;

    @SerializedName("imagen")
    private String imagen;

    public  Crear_eventos(){
    }



    public Crear_eventos( String titulo, String fechaEvento, String descripcion, String adjuntar, String imagenVideo) {



        this.tituloEvento = titulo;
        this.fechaEvento = fechaEvento;
        this.descripcionEvento = descripcion;
        this.adjuntar = adjuntar;
        this.imagen = imagenVideo;
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

    public String getAdjuntar() {
        return adjuntar;
    }

    public void setAdjuntar(String adjuntar) {
        this.adjuntar = adjuntar;
    }


    @Override
    public String toString() {
        return "CrearEventos{" +

                ", tituloEvento='" + tituloEvento + '\'' +
                ", fechaEvento='" + fechaEvento + '\'' +
                ", descripcionEvento='" + descripcionEvento + '\'' +
                ", adjuntar='" + adjuntar + '\'' +
                '}';
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}




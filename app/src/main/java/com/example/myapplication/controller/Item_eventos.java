
package com.example.myapplication.controller;

public class Item_eventos {
    private String tituloEvento;
    private String fechaEvento;
    private String descripcionEvento;
    private String adjuntarEvento;
    private String imagen;

    // Constructor
    public Item_eventos(String tituloEvento, String fechaEvento, String descripcionEvento, String adjuntarEvento, String imagen) {
        this.tituloEvento = tituloEvento;
        this.fechaEvento = fechaEvento;
        this.descripcionEvento = descripcionEvento;
        this.adjuntarEvento = adjuntarEvento;
        this.imagen = imagen;
    }

    public Item_eventos( String titulo, String fechaActividad, String descripcion, String archivoAdjunto) {
    }

    // Getters
    public String getTituloEvento() { return tituloEvento; }
    public void setTituloEvento(String tituloEvento) { this.tituloEvento = tituloEvento; }


    public String getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(String fechaEvento) { this.fechaEvento = fechaEvento; }



    public String getDescripcionEvento() { return descripcionEvento; }
    public void setDescripcionEvento(String descripcionEvento) { this.descripcionEvento = descripcionEvento; }



    public String getAdjuntarEvento() { return adjuntarEvento; }
    public void setAdjuntarEvento(String adjuntarEvento) { this.adjuntarEvento = adjuntarEvento; }



    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}

package com.example.myapplication.controller;

public class Item_eventos {

    private int id;
    private int idUsuario;
    private String nombreUsuario;
    private String titulo;
    private String fechaActividad;
    private String descripcion;
    private String imagen;
    private String archivo;
    private int idEmpresa;

    // Constructor principal
    public Item_eventos(int id, int idUsuario, String nombreUsuario, String titulo,
                        String fechaActividad, String descripcion, String imagen,
                        String archivo, int idEmpresa) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.titulo = titulo;
        this.fechaActividad = fechaActividad;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.archivo = archivo;
        this.idEmpresa = idEmpresa;
    }

    // Constructor simplificado (por si no se necesita todo)
    public Item_eventos(String titulo, String fechaActividad, String descripcion, String imagen, String archivo) {
        this.titulo = titulo;
        this.fechaActividad = fechaActividad;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.archivo = archivo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getFechaActividad() { return fechaActividad; }
    public void setFechaActividad(String fechaActividad) { this.fechaActividad = fechaActividad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getArchivo() { return archivo; }
    public void setArchivo(String archivo) { this.archivo = archivo; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }
}

package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("correoElectronico")
    private String correoElectronico;

    @SerializedName("cargo")
    private String cargo;

    @SerializedName("idEmpresa")
    private int idEmpresa;

    @SerializedName("idArea")
    private int idArea;

    @SerializedName("empresa")
    private Empresa empresa;

    @SerializedName("area")
    private Area area;

    // Getters básicos
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getCargo() { return cargo; }
    public int getIdEmpresa() { return idEmpresa; }
    public int getIdArea() { return idArea; }
    public Empresa getEmpresa() { return empresa; }
    public Area getArea() { return area; }

    // 🔹 Getters para nombres de empresa y área
    public String getNombreEmpresa() {
        return empresa != null ? empresa.getNombre() : null;
    }
    public String getNombreArea() {
        return area != null ? area.getNombre() : null;
    }

}

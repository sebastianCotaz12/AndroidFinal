package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Area {
    @SerializedName("idArea")
    private int idArea;

    @SerializedName("nombre")  // 🔹 Nuevo campo agregado
    private String nombre;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("idEmpresa")
    private int idEmpresa;

    @SerializedName("estado")
    private boolean estado;

    @SerializedName("esquema")
    private String esquema;

    @SerializedName("alias")
    private String alias;

    // Getters
    public int getIdArea() { return idArea; }

    public String getNombre() { return nombre; }  // 🔹 Nuevo getter

    public String getDescripcion() { return descripcion; }

    public int getIdEmpresa() { return idEmpresa; }

    public boolean isEstado() { return estado; }

    public String getEsquema() { return esquema; }

    public String getAlias() { return alias; }
}

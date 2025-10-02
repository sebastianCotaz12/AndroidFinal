package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Area {
    @SerializedName("idArea")
    private int idArea;

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

    public int getIdArea() { return idArea; }
    public String getDescripcion() { return descripcion; }
    public int getIdEmpresa() { return idEmpresa; }
    public boolean isEstado() { return estado; }
    public String getEsquema() { return esquema; }
    public String getAlias() { return alias; }
}

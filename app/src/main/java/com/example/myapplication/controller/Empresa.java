package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Empresa {
    @SerializedName("idEmpresa")
    private int idEmpresa;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("nit")
    private String nit;

    @SerializedName("estado")
    private boolean estado;

    @SerializedName("esquema")
    private String esquema;

    @SerializedName("alias")
    private String alias;

    public int getIdEmpresa() { return idEmpresa; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getNit() { return nit; }
    public boolean isEstado() { return estado; }
    public String getEsquema() { return esquema; }
    public String getAlias() { return alias; }
}

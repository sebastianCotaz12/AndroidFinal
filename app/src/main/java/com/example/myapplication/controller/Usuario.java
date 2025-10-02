package com.example.myapplication.controller;


import com.example.myapplication.controller.Area;
import com.example.myapplication.controller.Empresa;
import com.google.gson.annotations.SerializedName;

public class Usuario {
    private int id;

    @SerializedName("idEmpresa")
    private int idEmpresa;

    @SerializedName("idArea")
    private int idArea;

    private String nombre;
    private String apellido;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("correoElectronico")
    private String correoElectronico;

    private String cargo;
    private String contrasena;
    private String createdAt;
    private String updatedAt;

    @SerializedName("empresa")
    private Empresa empresa;

    @SerializedName("area")
    private Area area;

    // getters...
    public int getId(){return id;}
    public int getIdEmpresa(){return idEmpresa;}
    public int getIdArea(){return idArea;}
    public String getNombre(){return nombre;}
    public String getApellido(){return apellido;}
    public String getNombreUsuario(){return nombreUsuario;}
    public String getCorreoElectronico(){return correoElectronico;}
    public String getCargo(){return cargo;}
    public String getContrasena(){return contrasena;}
    public String getCreatedAt(){return createdAt;}
    public String getUpdatedAt(){return updatedAt;}
    public Empresa getEmpresa(){return empresa;}
    public Area getArea(){return area;}
}


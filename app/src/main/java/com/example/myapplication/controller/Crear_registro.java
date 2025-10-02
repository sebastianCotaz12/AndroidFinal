package com.example.myapplication.controller;

import com.google.gson.annotations.SerializedName;

public class Crear_registro {

    @SerializedName("id_empresa")
    private Integer idEmpresa;

    @SerializedName("id_area")
    private Integer idArea;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("nombre_usuario")
    private String nombreUsuario;

    @SerializedName("correo_electronico")
    private String correoElectronico;

    @SerializedName("cargo")
    private String cargo;

    @SerializedName("contrasena")
    private String contrasena;

    @SerializedName("confirmacion")
    private String confirmacion;

    // Getters y setters (nombres en camelCase para usar en Java)
    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }

    public Integer getIdArea() { return idArea; }
    public void setIdArea(Integer idArea) { this.idArea = idArea; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getConfirmacion() { return confirmacion; }
    public void setConfirmacion(String confirmacion) { this.confirmacion = confirmacion; }
}

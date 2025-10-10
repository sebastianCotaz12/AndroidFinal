package com.example.myapplication.api;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("user")
    private Usuario user;

    public String getToken() {
        return token;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Usuario getUser() {
        return user;
    }


    public static class Usuario {
        @SerializedName("id")
        private int id;

        @SerializedName("nombre")
        private String nombre;

        @SerializedName("apellido")
        private String apellido;

        @SerializedName("correo_electronico")
        private String correoElectronico;

        @SerializedName("id_empresa")
        private int idEmpresa;

        @SerializedName("id_area")
        private int idArea;

        // 🔹 NUEVOS CAMPOS
        @SerializedName("nombre_empresa")
        private String nombreEmpresa;

        @SerializedName("nombre_area")
        private String nombreArea;

        @SerializedName("cargo")
        private String cargo;

        // Getters existentes
        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getCorreoElectronico() { return correoElectronico; }
        public int getIdEmpresa() { return idEmpresa; }
        public int getIdArea() { return idArea; }

        public String getCargo() { return cargo; }


        // 🔹 Getters nuevos
        public String getNombreEmpresa() { return nombreEmpresa; }
        public String getNombreArea() { return nombreArea; }
    }
}

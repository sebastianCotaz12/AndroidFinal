package com.example.myapplication.api;



import com.google.gson.annotations.SerializedName;



public class LoginRequest {



    @SerializedName("correo_electronico")

    private String correo_electronico;



    @SerializedName("contrasena")

    private String contrasena;



    public LoginRequest(String correo, String password) {

        this.correo_electronico = correo;

        this.contrasena = password;

    }

}
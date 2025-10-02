package com.example.myapplication.api;

import com.example.myapplication.controller.Crear_registro;
import com.google.gson.annotations.SerializedName;

public class RegistroResponse {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("user")
    private Crear_registro user;

    public String getMensaje() {
        return mensaje;
    }

    public Crear_registro getUser() {
        return user;
    }
}

package com.example.myapplication.api;

import com.example.myapplication.controller.Usuario;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("user")
    private Usuario user;

    public String getToken() { return token; }
    public String getMensaje() { return mensaje; }
    public Usuario getUser() { return user; }
}

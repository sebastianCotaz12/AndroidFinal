package com.example.myapplication.api;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {

    @SerializedName(value = "mensaje", alternate = {"msj", "message"})
    private String mensaje;

    @SerializedName(value = "datos", alternate = {"data"})
    private T datos;

    public String getMsj() {
        return mensaje;
    }

    public T getDatos() {
        return datos;
    }
}

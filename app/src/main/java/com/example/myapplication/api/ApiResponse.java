package com.example.myapplication.api;

public class ApiResponse<T> {
    private String mensaje;
    private T datos;

    public String getMsj() { return mensaje; }
    public T getDatos() { return datos; }
}


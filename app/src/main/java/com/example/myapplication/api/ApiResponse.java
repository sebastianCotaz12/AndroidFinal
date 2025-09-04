package com.example.myapplication.api;

public class ApiResponse<T> {
    private String msj;
    private T datos;

    public String getMsj() { return msj; }
    public T getDatos() { return datos; }
}


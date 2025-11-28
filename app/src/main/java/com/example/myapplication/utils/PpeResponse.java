package com.example.myapplication.utils;

import java.util.List;

public class PpeResponse {
    public boolean ok;
    public String message;
    public List<String> missing;

    // Getters para mayor seguridad
    public boolean isOk() { return ok; }
    public String getMessage() { return message; }
    public List<String> getMissing() { return missing; }
}
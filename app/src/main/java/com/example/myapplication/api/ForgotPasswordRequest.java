package com.example.myapplication.api;

public class ForgotPasswordRequest {
    private String correo_electronico;

    public ForgotPasswordRequest(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }

    public String getCorreo_electronico() {
        return correo_electronico;
    }

    public void setCorreo_electronico(String correo_electronico) {
        this.correo_electronico = correo_electronico;
    }
}

package com.example.myapplication.api;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("msj")
    private String msj;

    public String getToken() {
        return token;
    }

    public String getMsj() {
        return msj;
    }
}
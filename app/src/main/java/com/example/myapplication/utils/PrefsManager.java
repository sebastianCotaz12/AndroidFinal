package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static final String PREF_NAME = "app_prefs";
    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ======== TOKEN ========
    public void setToken(String token) {
        prefs.edit().putString("token", token).apply();
    }

    public String getToken() {
        return prefs.getString("token", null);
    }

    // ======== ID USUARIO ========
    public void setIdUsuario(int id) {
        prefs.edit().putInt("id_usuario", id).apply();
    }

    public int getIdUsuario() {
        return prefs.getInt("id_usuario", -1);
    }

    // ======== ID EMPRESA ========
    public void setIdEmpresa(int idEmpresa) {
        prefs.edit().putInt("id_empresa", idEmpresa).apply();
    }

    public int getIdEmpresa() {
        return prefs.getInt("id_empresa", -1);
    }

    // ======== ID ÁREA ========
    public void setIdArea(int idArea) {
        prefs.edit().putInt("id_area", idArea).apply();
    }

    public int getIdArea() {
        return prefs.getInt("id_area", -1);
    }

    // ======== DATOS DE USUARIO ========
    public void setNombreUsuario(String nombre) {
        prefs.edit().putString("nombre_usuario", nombre).apply();
    }

    public String getNombreUsuario() {
        return prefs.getString("nombre_usuario", null);
    }

    public void setNombreEmpresa(String empresa) {
        prefs.edit().putString("nombre_empresa", empresa).apply();
    }

    public String getNombreEmpresa() {
        return prefs.getString("nombre_empresa", null);
    }

    public void setNombreArea(String area) {
        prefs.edit().putString("nombre_area", area).apply();
    }

    public String getNombreArea() {
        return prefs.getString("nombre_area", null);
    }

    // ======== CARGO ========
    public void setCargo(String cargo) {
        prefs.edit().putString("cargo", cargo).apply();
    }

    public String getCargo() {
        return prefs.getString("cargo", null);
    }


    // ======== LIMPIAR SESIÓN ========
    public void clearPrefs() {
        prefs.edit().clear().apply();
    }
}

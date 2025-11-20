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

    public String getTenantTopic() {
        return "prod_tenant_" + getIdEmpresa();
    }

    // ======== ID ÁREA ========
    public void setIdArea(int idArea) {
        prefs.edit().putInt("id_area", idArea).apply();
    }

    public int getIdArea() {
        return prefs.getInt("id_area", -1);
    }

    // ======== DATOS DE USUARIO ========

    // Nombre real del usuario
    public void setNombre(String nombre) {
        prefs.edit().putString("nombre", nombre != null ? nombre : "").apply();
    }

    public String getNombre() {
        return prefs.getString("nombre", "");
    }

    // === APELLIDO ===
    public void setApellidoUsuario(String apellido) {
        prefs.edit().putString("apellido_usuario", apellido != null ? apellido : "").apply();
    }

    public String getApellidoUsuario() {
        return prefs.getString("apellido_usuario", "");
    }

    // ======== CORREO ELECTRÓNICO ========
    public void setCorreoElectronico(String correoElectronico) {
        prefs.edit().putString("correoElectronico", correoElectronico != null ? correoElectronico : "").apply();
    }

    public String getCorreoElectronico() {
        return prefs.getString("correoElectronico", "");
    }

    // Nombre de usuario (login o username)
    public void setNombreUsuario(String nombreUsuario) {
        prefs.edit().putString("nombre_usuario", nombreUsuario != null ? nombreUsuario : "").apply();
    }

    public String getNombreUsuario() {
        return prefs.getString("nombre_usuario", "");
    }

    public void setNombreEmpresa(String empresa) {
        prefs.edit().putString("nombre_empresa", empresa != null ? empresa : "").apply();
    }

    public String getNombreEmpresa() {
        return prefs.getString("nombre_empresa", "");
    }

    public void setNombreArea(String area) {
        prefs.edit().putString("nombre_area", area != null ? area : "").apply();
    }

    public String getNombreArea() {
        return prefs.getString("nombre_area", "");
    }

    // ======== CARGO ========
    public void setCargo(String cargo) {
        prefs.edit().putString("cargo", cargo != null ? cargo : "").apply();
    }

    public String getCargo() {
        return prefs.getString("cargo", "");
    }

    // ======== MÉTODOS PARA LIMPIAR SESIÓN ========

    // Este es el método que estás llamando desde Menu.java
    public void clearAll() {
        prefs.edit().clear().apply();
    }

    // Método alternativo por si usas clearPrefs en otro lugar
    public void clearPrefs() {
        prefs.edit().clear().apply();
    }

    // ======== MÉTODO PARA VERIFICAR SI HAY SESIÓN ACTIVA ========
    public boolean isLoggedIn() {
        String token = getToken();
        String nombreUsuario = getNombreUsuario();
        return token != null && !token.isEmpty() &&
                nombreUsuario != null && !nombreUsuario.isEmpty();
    }

    // ======== MÉTODO PARA OBTENER NOMBRE COMPLETO ========
    public String getNombreCompleto() {
        String nombre = getNombre();
        String apellido = getApellidoUsuario();

        if (!nombre.isEmpty() && !apellido.isEmpty()) {
            return nombre + " " + apellido;
        } else if (!nombre.isEmpty()) {
            return nombre;
        } else if (!apellido.isEmpty()) {
            return apellido;
        } else {
            return getNombreUsuario();
        }
    }
}
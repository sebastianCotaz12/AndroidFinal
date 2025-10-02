package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static final String PREFS_NAME = "APP_PREFS";

    // Claves
    private static final String KEY_TOKEN = "jwtToken";
    private static final String KEY_ID_USUARIO = "idUsuario";
    private static final String KEY_NOMBRE_USUARIO = "nombreUsuario";
    private static final String KEY_ID_EMPRESA = "idEmpresa";
    private static final String KEY_NOMBRE_EMPRESA = "nombreEmpresa";
    private static final String KEY_ID_AREA = "idArea";
    private static final String KEY_NOMBRE_AREA = "nombreArea";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ------------------ TOKEN ------------------
    public void setToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    // ------------------ USUARIO ------------------
    public void setIdUsuario(int idUsuario) {
        editor.putInt(KEY_ID_USUARIO, idUsuario);
        editor.apply();
    }

    public int getIdUsuario() {
        return prefs.getInt(KEY_ID_USUARIO, -1);
    }

    public void setNombreUsuario(String nombreUsuario) {
        editor.putString(KEY_NOMBRE_USUARIO, nombreUsuario);
        editor.apply();
    }

    public String getNombreUsuario() {
        return prefs.getString(KEY_NOMBRE_USUARIO, "Usuario");
    }

    // ------------------ EMPRESA ------------------
    public void setIdEmpresa(int idEmpresa) {
        editor.putInt(KEY_ID_EMPRESA, idEmpresa);
        editor.apply();
    }

    public int getIdEmpresa() {
        return prefs.getInt(KEY_ID_EMPRESA, -1);
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        editor.putString(KEY_NOMBRE_EMPRESA, nombreEmpresa);
        editor.apply();
    }

    public String getNombreEmpresa() {
        return prefs.getString(KEY_NOMBRE_EMPRESA, "Sin empresa");
    }

    // ------------------ ÁREA ------------------
    public void setIdArea(int idArea) {
        editor.putInt(KEY_ID_AREA, idArea);
        editor.apply();
    }

    public int getIdArea() {
        return prefs.getInt(KEY_ID_AREA, -1);
    }

    public void setNombreArea(String nombreArea) {
        editor.putString(KEY_NOMBRE_AREA, nombreArea);
        editor.apply();
    }

    public String getNombreArea() {
        return prefs.getString(KEY_NOMBRE_AREA, "Sin área");
    }

    // ------------------ LIMPIAR SESIÓN ------------------
    public void clear() {
        editor.clear();
        editor.apply();
    }
}

package com.example.myapplication.utils;

import android.content.Context;
import android.content.Intent;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.controller.InicioSesion;

public class SesionManager {

    private final Context context;
    private final PrefsManager prefsManager;

    public SesionManager(Context context) {
        this.context = context;
        this.prefsManager = new PrefsManager(context);
    }

    // Cerrar sesión de manera segura
    public void cerrarSesion() {
        // Limpiar datos locales
        prefsManager.clear();

        // Resetear cliente de API
        ApiClient.resetClient();

        // Redirigir al inicio de sesión
        Intent intent = new Intent(context, InicioSesion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    // Verificar si hay sesión activa
    public boolean haySesionActiva() {
        String token = prefsManager.getToken();
        int idUsuario = prefsManager.getIdUsuario();
        return token != null && !token.trim().isEmpty() && idUsuario != -1;
    }
}

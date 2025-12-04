package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PrefsManager {

    private static final String PREF_NAME = "app_prefs";
    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ======== MÉTODO NUEVO PARA GUARDAR RESPUESTA API ========
    public void saveLastApiResponse(String response) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_api_response", response);
        editor.putLong("last_api_response_time", System.currentTimeMillis());
        editor.apply();
        Log.d("PrefsManager", "✅ Respuesta API guardada: " + (response != null ? response.length() : 0) + " caracteres");
    }

    public String getLastApiResponse() {
        return prefs.getString("last_api_response", "");
    }

    public long getLastApiResponseTime() {
        return prefs.getLong("last_api_response_time", 0);
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

    // ======== ROL DEL USUARIO (para WebSocket) ========
    public void setRol(String rol) {
        prefs.edit().putString("rol", rol != null ? rol : "").apply();
    }

    public String getRol() {
        return prefs.getString("rol", "");
    }

    // ======== WEB SOCKET CONFIGURATION ========
    public void setWebSocketUrl(String url) {
        prefs.edit().putString("websocket_url", url != null ? url : "").apply();
    }

    public String getWebSocketUrl() {
        return prefs.getString("websocket_url", "http://192.168.1.100:3333");
    }

    public void setWebSocketEnabled(boolean enabled) {
        prefs.edit().putBoolean("websocket_enabled", enabled).apply();
    }

    public boolean isWebSocketEnabled() {
        return prefs.getBoolean("websocket_enabled", true);
    }

    public void setLastWebSocketConnection(long timestamp) {
        prefs.edit().putLong("last_websocket_connection", timestamp).apply();
    }

    public long getLastWebSocketConnection() {
        return prefs.getLong("last_websocket_connection", 0);
    }

    // ======== NOTIFICATION SETTINGS ========
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean("notifications_enabled", true);
    }

    public void setNotificationSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean("notification_sound", enabled).apply();
    }

    public boolean isNotificationSoundEnabled() {
        return prefs.getBoolean("notification_sound", true);
    }

    public void setNotificationVibrationEnabled(boolean enabled) {
        prefs.edit().putBoolean("notification_vibration", enabled).apply();
    }

    public boolean isNotificationVibrationEnabled() {
        return prefs.getBoolean("notification_vibration", true);
    }

    // ======== EPP DETECTION HISTORY ========
    public void setLastDetectionTime(long timestamp) {
        prefs.edit().putLong("last_detection_time", timestamp).apply();
    }

    public long getLastDetectionTime() {
        return prefs.getLong("last_detection_time", 0);
    }

    public void setTotalDetections(int count) {
        prefs.edit().putInt("total_detections", count).apply();
    }

    public int getTotalDetections() {
        return prefs.getInt("total_detections", 0);
    }

    public void setTotalEPPCompletos(int count) {
        prefs.edit().putInt("total_epp_completos", count).apply();
    }

    public int getTotalEPPCompletos() {
        return prefs.getInt("total_epp_completos", 0);
    }

    public void setTotalEPPFaltantes(int count) {
        prefs.edit().putInt("total_epp_faltantes", count).apply();
    }

    public int getTotalEPPFaltantes() {
        return prefs.getInt("total_epp_faltantes", 0);
    }

    public void incrementTotalDetections() {
        int current = getTotalDetections();
        setTotalDetections(current + 1);
    }

    public void incrementEPPCompletos() {
        int current = getTotalEPPCompletos();
        setTotalEPPCompletos(current + 1);
    }

    public void incrementEPPFaltantes() {
        int current = getTotalEPPFaltantes();
        setTotalEPPFaltantes(current + 1);
    }

    // ======== USER PREFERENCES FOR EPP ========
    public void setDefaultContext(String context) {
        prefs.edit().putString("default_context", context != null ? context : "welder").apply();
    }

    public String getDefaultContext() {
        return prefs.getString("default_context", "welder");
    }

    public void setAutoSelectContext(boolean auto) {
        prefs.edit().putBoolean("auto_select_context", auto).apply();
    }

    public boolean isAutoSelectContext() {
        return prefs.getBoolean("auto_select_context", true);
    }

    public void setAutoSendNotifications(boolean auto) {
        prefs.edit().putBoolean("auto_send_notifications", auto).apply();
    }

    public boolean isAutoSendNotifications() {
        return prefs.getBoolean("auto_send_notifications", true);
    }

    // ======== SESSION MANAGEMENT ========
    public void setLastActivityTime(long timestamp) {
        prefs.edit().putLong("last_activity_time", timestamp).apply();
    }

    public long getLastActivityTime() {
        return prefs.getLong("last_activity_time", System.currentTimeMillis());
    }

    public void setSessionTimeout(int minutes) {
        prefs.edit().putInt("session_timeout", minutes).apply();
    }

    public int getSessionTimeout() {
        return prefs.getInt("session_timeout", 30); // 30 minutos por defecto
    }

    // ======== DEVICE INFORMATION ========
    public void setDeviceId(String deviceId) {
        prefs.edit().putString("device_id", deviceId != null ? deviceId : "").apply();
    }

    public String getDeviceId() {
        return prefs.getString("device_id", "");
    }

    public void setFCMToken(String token) {
        prefs.edit().putString("fcm_token", token != null ? token : "").apply();
    }

    public String getFCMToken() {
        return prefs.getString("fcm_token", "");
    }

    // ======== APP SETTINGS ========
    public void setAppVersion(String version) {
        prefs.edit().putString("app_version", version != null ? version : "").apply();
    }

    public String getAppVersion() {
        return prefs.getString("app_version", "");
    }

    public void setFirstLaunch(boolean first) {
        prefs.edit().putBoolean("first_launch", first).apply();
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean("first_launch", true);
    }

    // ======== MÉTODOS PARA DETERMINAR ROL AUTOMÁTICAMENTE ========
    public String getRolDeterminado() {
        String cargo = getCargo();
        if (cargo == null || cargo.isEmpty()) {
            return "empleado";
        }

        cargo = cargo.toLowerCase().trim();

        if (cargo.contains("sg-sst") || cargo.contains("sst")) {
            return "SG-SST";
        } else if (cargo.contains("admin") || cargo.contains("administrador")) {
            return "admin";
        } else if (cargo.contains("supervisor") || cargo.contains("responsable") ||
                cargo.contains("jefe") || cargo.contains("coordinador")) {
            return "supervisor";
        } else {
            return "empleado";
        }
    }

    public boolean isUserSG_SST() {
        String rol = getRolDeterminado();
        return rol.equals("SG-SST") || rol.equals("admin") || rol.equals("supervisor");
    }

    public boolean isUserAdmin() {
        String rol = getRolDeterminado();
        return rol.equals("admin");
    }

    // ======== MÉTODOS PARA NOTIFICACIONES WEB SOCKET ========
    public boolean shouldSendWebSocketNotification() {
        // Verifica si debe enviar notificación basado en:
        // 1. Si las notificaciones están habilitadas
        // 2. Si el usuario tiene un rol que puede enviar notificaciones
        // 3. Si la configuración automática está habilitada
        return isNotificationsEnabled() &&
                isAutoSendNotifications() &&
                isUserSG_SST();
    }

    public void saveLastNotificationSent(String context, int missingItemsCount) {
        long timestamp = System.currentTimeMillis();
        prefs.edit()
                .putLong("last_notification_time", timestamp)
                .putString("last_notification_context", context)
                .putInt("last_notification_missing", missingItemsCount)
                .apply();
    }

    public long getLastNotificationTime() {
        return prefs.getLong("last_notification_time", 0);
    }

    public String getLastNotificationContext() {
        return prefs.getString("last_notification_context", "");
    }

    public int getLastNotificationMissingCount() {
        return prefs.getInt("last_notification_missing", 0);
    }

    // ======== MÉTODOS PARA LIMPIAR SESIÓN ========
    public void clearAll() {
        prefs.edit().clear().apply();
    }

    public void clearPrefs() {
        prefs.edit().clear().apply();
    }

    // Mantener solo datos esenciales (para logout parcial)
    public void clearSessionData() {
        SharedPreferences.Editor editor = prefs.edit();

        // Mantener configuraciones de usuario
        String appVersion = getAppVersion();
        String deviceId = getDeviceId();
        String fcmToken = getFCMToken();
        boolean firstLaunch = isFirstLaunch();

        // Limpiar todo
        editor.clear();

        // Restaurar configuraciones esenciales
        if (appVersion != null && !appVersion.isEmpty()) {
            editor.putString("app_version", appVersion);
        }
        if (deviceId != null && !deviceId.isEmpty()) {
            editor.putString("device_id", deviceId);
        }
        if (fcmToken != null && !fcmToken.isEmpty()) {
            editor.putString("fcm_token", fcmToken);
        }
        editor.putBoolean("first_launch", firstLaunch);

        editor.apply();
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

    // ======== MÉTODO PARA OBTENER INFORMACIÓN DEL USUARIO PARA NOTIFICACIONES ========
    public String getUserInfoForNotifications() {
        StringBuilder info = new StringBuilder();

        String nombreCompleto = getNombreCompleto();
        if (!nombreCompleto.isEmpty()) {
            info.append(nombreCompleto);
        }

        String cargo = getCargo();
        if (!cargo.isEmpty()) {
            if (info.length() > 0) info.append(" - ");
            info.append(cargo);
        }

        String empresa = getNombreEmpresa();
        if (!empresa.isEmpty()) {
            if (info.length() > 0) info.append(" | ");
            info.append(empresa);
        }

        return info.toString();
    }

    // ======== MÉTODO PARA VERIFICAR SI HA PASADO MUCHO TIEMPO DESDE LA ÚLTIMA NOTIFICACIÓN ========
    public boolean shouldSendNotification(long currentTime) {
        long lastNotificationTime = getLastNotificationTime();
        long notificationCooldown = 5 * 60 * 1000; // 5 minutos de cooldown

        // Si nunca se ha enviado una notificación o ha pasado el cooldown
        return lastNotificationTime == 0 ||
                (currentTime - lastNotificationTime) > notificationCooldown;
    }

    // ======== MÉTODO PARA OBTENER CONFIGURACIÓN COMPLETA ========
    public String getConfigSummary() {
        return "Usuario: " + getNombreCompleto() + "\n" +
                "Cargo: " + getCargo() + "\n" +
                "Rol: " + getRolDeterminado() + "\n" +
                "Empresa: " + getNombreEmpresa() + "\n" +
                "ID Empresa: " + getIdEmpresa() + "\n" +
                "WebSocket: " + (isWebSocketEnabled() ? "Habilitado" : "Deshabilitado") + "\n" +
                "Notificaciones: " + (isNotificationsEnabled() ? "Habilitadas" : "Deshabilitadas") + "\n" +
                "Auto-envío: " + (isAutoSendNotifications() ? "Sí" : "No");
    }
}
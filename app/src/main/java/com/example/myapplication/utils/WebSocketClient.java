package com.example.myapplication.utils;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.net.URISyntaxException;

public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private static WebSocketClient instance;
    private Socket mSocket;
    private boolean isConnected = false;
    private Context context;
    private PrefsManager prefsManager;

    // IMPORTANTE: Cambia esta URL por la de tu servidor WebSocket
    private static final String WS_URL = "https://unreproaching-rancorously-evelina.ngrok-free.dev"; // SIN barra al final

    private WebSocketClient(Context context) {
        this.context = context.getApplicationContext();
        this.prefsManager = new PrefsManager(context);
        connect();
    }

    public static synchronized WebSocketClient getInstance(Context context) {
        if (instance == null) {
            instance = new WebSocketClient(context);
        }
        return instance;
    }

    private void connect() {
        try {
            IO.Options options = new IO.Options();
            options.reconnection = true;
            options.reconnectionAttempts = 5;
            options.reconnectionDelay = 2000;
            options.timeout = 10000;
            options.forceNew = true;
            options.transports = new String[]{"websocket"};

            mSocket = IO.socket(WS_URL, options);

            // Configurar listeners básicos (SOLO los que existen)
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            // EVENT_CONNECT_TIMEOUT no existe en esta versión, lo eliminamos

            // Registrar el rol del usuario cuando se conecte
            mSocket.on(Socket.EVENT_CONNECT, args -> {
                registerUserRole();
            });

            // Evento para confirmación de rol
            mSocket.on("rol_registrado", args -> {
                Log.d(TAG, "✅ Rol registrado en el servidor");
            });

            // Evento para recibir notificaciones del servidor (si las necesitas)
            mSocket.on("notificacion_recibida", args -> {
                Log.d(TAG, "📩 Notificación recibida del servidor");
            });

            // Conectar
            mSocket.connect();
            Log.d(TAG, "🔗 Intentando conectar a WebSocket: " + WS_URL);

        } catch (URISyntaxException e) {
            Log.e(TAG, "❌ Error de URL WebSocket: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "❌ Error general en WebSocket: " + e.getMessage());
        }
    }

    private Emitter.Listener onConnect = args -> {
        Log.d(TAG, "✅ Conectado al WebSocket");
        isConnected = true;
    };

    private Emitter.Listener onDisconnect = args -> {
        Log.d(TAG, "🔴 Desconectado del WebSocket");
        isConnected = false;
    };

    private Emitter.Listener onConnectError = args -> {
        if (args.length > 0 && args[0] instanceof Exception) {
            Exception e = (Exception) args[0];
            Log.e(TAG, "❌ Error de conexión WebSocket: " + e.getMessage());
        } else {
            Log.e(TAG, "❌ Error de conexión WebSocket");
        }
        isConnected = false;
    };

    private void registerUserRole() {
        String cargo = prefsManager.getCargo();
        if (cargo == null || cargo.isEmpty()) {
            Log.w(TAG, "⚠️ No se pudo registrar rol: cargo no disponible");
            // Usar cargo por defecto si no hay
            cargo = "empleado";
        }

        cargo = cargo.toLowerCase().trim();
        String rol = "empleado"; // Por defecto

        // Determinar rol basado en el cargo
        if (cargo.contains("sg-sst") || cargo.contains("sst")) {
            rol = "SG-SST";
        } else if (cargo.contains("admin") || cargo.contains("administrador")) {
            rol = "admin";
        } else if (cargo.contains("supervisor") || cargo.contains("responsable")) {
            rol = "supervisor";
        }

        // Enviar evento de registro de rol
        try {
            mSocket.emit("registrar_rol", rol);
            Log.d(TAG, "👤 Registrado en WebSocket como: " + rol);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error registrando rol: " + e.getMessage());
        }
    }

    /**
     * Enviar notificación al WebSocket
     */
    public void enviarNotificacion(JSONObject notificacionData) {
        if (!isConnected || mSocket == null) {
            Log.w(TAG, "⚠️ WebSocket no conectado, intentando reconectar...");
            reconnect();
            return;
        }

        try {
            // IMPORTANTE: Usa el mismo nombre de evento que espera tu backend
            mSocket.emit("notificacion_sg_sst", notificacionData);
            Log.d(TAG, "📤 Notificación enviada vía WebSocket");
            Log.d(TAG, "📊 Datos enviados: " + notificacionData.toString());

        } catch (Exception e) {
            Log.e(TAG, "❌ Error enviando notificación al WebSocket: " + e.getMessage());
        }
    }

    /**
     * Enviar notificación simple (para pruebas)
     */
    public void enviarNotificacionSimple(String mensaje) {
        if (!isConnected || mSocket == null) {
            Log.w(TAG, "⚠️ WebSocket no conectado");
            return;
        }

        try {
            JSONObject simpleData = new JSONObject();
            simpleData.put("mensaje", mensaje);
            simpleData.put("fecha", System.currentTimeMillis());
            simpleData.put("tipo", "alerta_prueba");

            mSocket.emit("notificacion_sg_sst", simpleData);
            Log.d(TAG, "📤 Notificación simple enviada: " + mensaje);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error enviando notificación simple: " + e.getMessage());
        }
    }

    public void reconnect() {
        if (mSocket != null) {
            try {
                Log.d(TAG, "🔄 Reconectando WebSocket...");
                mSocket.disconnect();
                mSocket.connect();
            } catch (Exception e) {
                Log.e(TAG, "❌ Error reconectando WebSocket: " + e.getMessage());
            }
        }
    }

    public void disconnect() {
        if (mSocket != null) {
            try {
                mSocket.disconnect();
                mSocket.off();
                Log.d(TAG, "🔌 WebSocket desconectado");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error desconectando WebSocket: " + e.getMessage());
            }
        }
        isConnected = false;
    }

    public boolean isConnected() {
        return mSocket != null && mSocket.connected();
    }

    /**
     * Obtener estado de conexión como texto
     */
    public String getConnectionStatus() {
        if (mSocket == null) {
            return "No inicializado";
        } else if (mSocket.connected()) {
            return "Conectado";
        } else {
            return "Desconectado";
        }
    }

    /**
     * Verificar si hay conexión activa
     */
    public boolean checkConnection() {
        boolean connected = isConnected();
        Log.d(TAG, "🔍 Estado conexión WebSocket: " + (connected ? "CONECTADO" : "DESCONECTADO"));
        return connected;
    }
}
package com.example.myapplication.utils;

import android.content.Context;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;

public class FcmHelper {

    public static void subscribeToTenantTopic(Context context) {
        PrefsManager prefs = new PrefsManager(context);
        int idEmpresa = prefs.getIdEmpresa(); // obtener el id_empresa guardado en login

        if (idEmpresa == 0) {
            Log.e("FCM", "ID de empresa no disponible, no se puede suscribir");
            return;
        }

        String topic = "prod_tenant_" + idEmpresa;
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Suscrito correctamente al topic: " + topic);
                    } else {
                        Log.e("FCM", "Error al suscribirse al topic: " + topic, task.getException());
                    }
                });
    }

    public static void unsubscribeFromTenantTopic(Context context) {
        PrefsManager prefs = new PrefsManager(context);
        int idEmpresa = prefs.getIdEmpresa();

        if (idEmpresa == 0) return;

        String topic = "prod_tenant_" + idEmpresa;
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Cancelada la suscripción al topic: " + topic);
                    } else {
                        Log.e("FCM", "Error al cancelar suscripción al topic: " + topic, task.getException());
                    }
                });
    }
}

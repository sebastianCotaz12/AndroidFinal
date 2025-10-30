package com.example.myapplication.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String CHANNEL_ID = "events_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage msg) {
        super.onMessageReceived(msg);

        // Crear canal si es necesario (Android 8+)
        createNotificationChannel();

        String title = "Nuevo evento 🎉";
        String body = "";
        String eventId = null;
        String tenantId = null;

        if (msg.getNotification() != null) {
            title = msg.getNotification().getTitle();
            body = msg.getNotification().getBody();
        }

        if (msg.getData() != null) {
            if (msg.getData().containsKey("eventId")) {
                eventId = msg.getData().get("eventId");
            }
            if (msg.getData().containsKey("tenantId")) {
                tenantId = msg.getData().get("tenantId");
            }
            // Si el mensaje solo viene con data y sin "notification"
            if (msg.getNotification() == null && msg.getData().containsKey("body")) {
                body = msg.getData().get("body");
            }
        }

        Intent intent = new Intent(this, Detalles_eventos.class);
        intent.putExtra("eventId", eventId);
        intent.putExtra("tenantId", tenantId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent((int) System.currentTimeMillis(),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Si usas topics, no necesitas enviar el token
        // Si usas tokens individuales, aquí puedes enviarlo al backend
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Eventos";
            String description = "Canal de notificaciones de nuevos eventos";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


}

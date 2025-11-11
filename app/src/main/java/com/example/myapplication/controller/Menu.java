package com.example.myapplication.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Perfil;
import com.example.myapplication.R;
import com.example.myapplication.utils.PermissionHelper;
import com.example.myapplication.utils.PrefsManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.messaging.FirebaseMessaging;

public class Menu extends AppCompatActivity {

    // Tarjetas principales del menú
    MaterialCardView cardGestionEpp, cardReportes, cardActividades, cardListaChequeo, cardBlog;
    TextView tvBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        PrefsManager prefsManager = new PrefsManager(this);

        // Solicitar permiso de notificaciones (Android 13+)
        PermissionHelper.requestNotificationPermission(this);

        // Crear canal de notificación "events_channel" si es Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "events_channel";
            CharSequence channelName = "Eventos importantes";
            String channelDescription = "Notificaciones sobre eventos nuevos";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // --- Referencias a las tarjetas del menú ---
        cardGestionEpp = findViewById(R.id.card_gestion_epp);
        cardReportes = findViewById(R.id.card_reportes);
        cardActividades = findViewById(R.id.card_actividades);
        cardListaChequeo = findViewById(R.id.card_lista_chequeo);
        cardBlog = findViewById(R.id.card_blog);

        // --- Referencia al texto de bienvenida ---
        tvBienvenida = findViewById(R.id.tvBienvenida);

        // --- Mostrar nombre del usuario logueado ---
        String nombreUsuario = prefsManager.getNombreUsuario();
        tvBienvenida.setText("👋 Bienvenido, " + (nombreUsuario != null ? nombreUsuario : "Usuario"));

        // --- Suscripción a topic FCM ---
        int idEmpresa = prefsManager.getIdEmpresa();
        if (idEmpresa > 0) {
            String topic = "prod_tenant_" + idEmpresa;
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FCM", "Suscrito correctamente al topic: " + topic);
                        } else {
                            Log.e("FCM", "Error al suscribirse al topic", task.getException());
                        }
                    });
        }

        // --- Acciones al tocar las tarjetas ---
        cardGestionEpp.setOnClickListener(v ->
                startActivity(new Intent(Menu.this, Lista_gestionEpp.class)));

        cardReportes.setOnClickListener(v ->
                startActivity(new Intent(Menu.this, Lista_reportes.class)));

        cardActividades.setOnClickListener(v ->
                startActivity(new Intent(Menu.this, Lista_actLudicas.class)));

        cardListaChequeo.setOnClickListener(v ->
                startActivity(new Intent(Menu.this, Lista_listaChequeo.class)));

        cardBlog.setOnClickListener(v ->
                startActivity(new Intent(Menu.this, Lista_eventos.class)));
    }

    // --- Método para redirigir al perfil al tocar el logo ---
    public void irPerfil(View view) {
        Intent intent = new Intent(Menu.this, Perfil.class);
        startActivity(intent);
    }
}

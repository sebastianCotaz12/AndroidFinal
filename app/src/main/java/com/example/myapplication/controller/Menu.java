package com.example.myapplication.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;
import com.example.myapplication.utils.PermissionHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.messaging.FirebaseMessaging;

public class Menu extends AppCompatActivity {

    MaterialCardView cardGestionEpp, cardReportes, cardActividades, cardListaChequeo,
            cardBlog, cardAsesoramiento, cardCapacitacion, cardDocumentacion;
    Button btnCerrarSesion, btnEditProfile;
    TextView tvBienvenida, tvEmpresa, tvArea;

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

        // Referencias de cards
        cardGestionEpp = findViewById(R.id.card_gestion_epp);
        cardReportes = findViewById(R.id.card_reportes);
        cardActividades = findViewById(R.id.card_actividades);
        cardListaChequeo = findViewById(R.id.card_lista_chequeo);
        cardBlog = findViewById(R.id.card_blog);
        cardAsesoramiento = findViewById(R.id.card_asesoramiento);

        // Referencias de botones y textos
        btnEditProfile = findViewById(R.id.btn_edit_profile); // ✅ movido aquí
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvEmpresa = findViewById(R.id.tvEmpresa);
        tvArea = findViewById(R.id.tvArea);

        // Recuperamos datos del usuario logueado
        String nombreUsuario = prefsManager.getNombreUsuario();
        String empresa = prefsManager.getNombreEmpresa();
        String area = prefsManager.getNombreArea();
        int idEmpresa = prefsManager.getIdEmpresa();

        // Mostramos los datos en el encabezado
        tvBienvenida.setText("Bienvenido, " + (nombreUsuario != null ? nombreUsuario : "Usuario"));
        tvEmpresa.setText("Empresa: " + (empresa != null ? empresa : "N/A"));
        tvArea.setText("Área: " + (area != null ? area : "N/A"));

        // --- SUSCRIPCIÓN AL TOPIC FCM ---
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

        // --- EVENTOS CLICK PARA CARDS ---
        cardGestionEpp.setOnClickListener(v -> startActivity(new Intent(Menu.this, Lista_gestionEpp.class)));
        cardReportes.setOnClickListener(v -> startActivity(new Intent(Menu.this, Lista_reportes.class)));
        cardActividades.setOnClickListener(v -> startActivity(new Intent(Menu.this, Lista_actLudicas.class)));
        cardListaChequeo.setOnClickListener(v -> startActivity(new Intent(Menu.this, Lista_listaChequeo.class)));
        cardBlog.setOnClickListener(v -> startActivity(new Intent(Menu.this, Lista_eventos.class)));

        // --- BOTÓN EDITAR PERFIL ---
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, EditarPerfil.class);
            startActivity(intent);
        });

        // --- BOTÓN CERRAR SESIÓN ---
        btnCerrarSesion.setOnClickListener(v -> {
            prefsManager.clearPrefs();
            Intent intent = new Intent(Menu.this, InicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

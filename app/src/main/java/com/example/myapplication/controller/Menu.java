package com.example.myapplication.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        prefsManager = new PrefsManager(this);

        // Verificar que hay una sesión activa
        if (!prefsManager.isLoggedIn()) {
            Toast.makeText(this, "Sesión expirada, ingresa nuevamente", Toast.LENGTH_LONG).show();
            redirigirALogin();
            return;
        }

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

        initViews();
        cargarDatosUsuario();
        setupClickListeners();
        suscribirATopicFCM();
    }

    private void initViews() {
        // Referencias de cards
        cardGestionEpp = findViewById(R.id.card_gestion_epp);
        cardReportes = findViewById(R.id.card_reportes);
        cardActividades = findViewById(R.id.card_actividades);
        cardListaChequeo = findViewById(R.id.card_lista_chequeo);
        cardBlog = findViewById(R.id.card_blog);
        cardAsesoramiento = findViewById(R.id.card_asesoramiento);

        // Referencias de botones y textos
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvEmpresa = findViewById(R.id.tvEmpresa);
        tvArea = findViewById(R.id.tvArea);
    }

    private void cargarDatosUsuario() {
        // Usar el nuevo método getNombreCompleto() del PrefsManager actualizado
        String nombreCompleto = prefsManager.getNombreCompleto();
        String empresa = prefsManager.getNombreEmpresa();
        String area = prefsManager.getNombreArea();
        String cargo = prefsManager.getCargo();

        // DEBUG: Ver qué datos tenemos
        Log.d("MENU_DEBUG", "Nombre completo: " + nombreCompleto);
        Log.d("MENU_DEBUG", "Empresa: " + empresa);
        Log.d("MENU_DEBUG", "Área: " + area);
        Log.d("MENU_DEBUG", "Cargo: " + cargo);
        Log.d("MENU_DEBUG", "Token: " + (prefsManager.getToken() != null ? "SI" : "NO"));

        // Mostrar los datos en el encabezado
        if (!nombreCompleto.isEmpty()) {
            tvBienvenida.setText("Bienvenido, " + nombreCompleto);
        } else {
            // Intentar con nombre de usuario si no hay nombre completo
            String nombreUsuario = prefsManager.getNombreUsuario();
            if (!nombreUsuario.isEmpty()) {
                tvBienvenida.setText("Bienvenido, " + nombreUsuario);
            } else {
                tvBienvenida.setText("Bienvenido, Usuario");
            }
        }

        tvEmpresa.setText(!empresa.isEmpty() ? "Empresa: " + empresa : "Empresa: N/A");
        tvArea.setText(!area.isEmpty() ? "Área: " + area : "Área: N/A");
    }

    private void suscribirATopicFCM() {
        int idEmpresa = prefsManager.getIdEmpresa();

        if (idEmpresa > 0) {
            String topic = prefsManager.getTenantTopic(); // Usar el método del PrefsManager
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FCM", "Suscrito correctamente al topic: " + topic);
                        } else {
                            Log.e("FCM", "Error al suscribirse al topic", task.getException());
                        }
                    });
        } else {
            Log.w("FCM", "No se pudo suscribir al topic: idEmpresa no válido");
        }
    }

    private void setupClickListeners() {
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
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        // Limpiar todos los datos
        prefsManager.clearAll(); // Usar clearAll() en lugar de clearPrefs()

        // Redirigir al login
        redirigirALogin();
    }

    private void redirigirALogin() {
        Intent intent = new Intent(Menu.this, InicioSesion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar datos cada vez que vuelva a la actividad
        cargarDatosUsuario();
    }
}
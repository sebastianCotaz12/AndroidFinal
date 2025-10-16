package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;
import com.google.android.material.card.MaterialCardView;

public class Menu extends AppCompatActivity {

    // Cambiar botones por MaterialCardView
    MaterialCardView cardGestionEpp, cardReportes, cardActividades, cardListaChequeo,
            cardBlog, cardAsesoramiento, cardCapacitacion, cardDocumentacion;
    Button btnCerrarSesion;
    TextView tvBienvenida, tvEmpresa, tvArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Inicializamos PrefsManager
        PrefsManager prefsManager = new PrefsManager(this);

        // Referencias de cards (reemplazan a los botones)
        cardGestionEpp = findViewById(R.id.card_gestion_epp);
        cardReportes = findViewById(R.id.card_reportes);
        cardActividades = findViewById(R.id.card_actividades);
        cardListaChequeo = findViewById(R.id.card_lista_chequeo);
        cardBlog = findViewById(R.id.card_blog);
        cardAsesoramiento = findViewById(R.id.card_asesoramiento);

        // Referencias de botones y textos
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvEmpresa = findViewById(R.id.tvEmpresa);
        tvArea = findViewById(R.id.tvArea);

        // Recuperamos datos del usuario logueado
        String nombreUsuario = prefsManager.getNombreUsuario();
        String empresa = prefsManager.getNombreEmpresa();
        String area = prefsManager.getNombreArea();

        // Mostramos los datos en el encabezado
        tvBienvenida.setText("Bienvenido, " + (nombreUsuario != null ? nombreUsuario : "Usuario"));
        tvEmpresa.setText("Empresa: " + (empresa != null ? empresa : "N/A"));
        tvArea.setText("Área: " + (area != null ? area : "N/A"));

        // --- EVENTOS CLICK PARA CARDS ---
        cardGestionEpp.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_gestionEpp.class);
            startActivity(intent);
        });

        cardReportes.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_reportes.class);
            startActivity(intent);
        });

        cardActividades.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_actLudicas.class);
            startActivity(intent);
        });

        cardListaChequeo.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_listaChequeo.class);
            startActivity(intent);
        });

        cardBlog.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_eventos.class);
            startActivity(intent);
        });

        cardAsesoramiento.setOnClickListener(v -> {
            // Agregar la actividad correspondiente para asesoramiento
            // Intent intent = new Intent(Menu.this, AsesoramientoActivity.class);
            // startActivity(intent);
        });


        // --- BOTÓN CERRAR SESIÓN (MANTENIDO) ---
        btnCerrarSesion.setOnClickListener(v -> {
            // Limpiar los datos del usuario guardados en las preferencias
            prefsManager.clearPrefs();

            // Volver a la pantalla de inicio o login
            Intent intent = new Intent(Menu.this, InicioSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
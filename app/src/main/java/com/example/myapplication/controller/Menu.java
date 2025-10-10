package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.PrefsManager;

public class Menu extends AppCompatActivity {

    Button btnGestionEpp, btnReportes, btnAct, btnListaChequeo, btnBlog, btnAsesoramiento, btnCerrarSesion;
    TextView tvBienvenida, tvEmpresa, tvArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Inicializamos PrefsManager
        PrefsManager prefsManager = new PrefsManager(this);

        // Referencias de botones
        btnGestionEpp     = findViewById(R.id.btn_GestionEpp);
        btnReportes       = findViewById(R.id.btn_Reportes);
        btnAct            = findViewById(R.id.btn_Act);
        btnListaChequeo   = findViewById(R.id.btn_ListaChequeo);
        btnBlog           = findViewById(R.id.btn_Blog);
        btnAsesoramiento  = findViewById(R.id.btn_Asesoramiento);
        btnCerrarSesion   = findViewById(R.id.btnCerrarSesion);

        // Referencias de textos para encabezado
        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvEmpresa    = findViewById(R.id.tvEmpresa);
        tvArea       = findViewById(R.id.tvArea);

        // Recuperamos datos del usuario logueado
        String nombreUsuario = prefsManager.getNombreUsuario();
        String empresa = prefsManager.getNombreEmpresa();
        String area = prefsManager.getNombreArea();

        // Mostramos los datos en el encabezado
        tvBienvenida.setText("Bienvenido, " + (nombreUsuario != null ? nombreUsuario : "Usuario"));
        tvEmpresa.setText("Empresa: " + (empresa != null ? empresa : "N/A"));
        tvArea.setText("Área: " + (area != null ? area : "N/A"));

        // --- NAVEGACIONES ---
        btnGestionEpp.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_gestionEpp.class);
            startActivity(intent);
        });


        btnReportes.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_reportes.class);
            startActivity(intent);
        });

        btnAct.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_actLudicas.class);
            startActivity(intent);
        });

        btnListaChequeo.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_listaChequeo.class);
            startActivity(intent);
        });

        btnBlog.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Lista_eventos.class);
            startActivity(intent);
        });


        // --- BOTÓN CERRAR SESIÓN ---
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

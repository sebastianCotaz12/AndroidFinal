package com.example.myapplication.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Menu extends AppCompatActivity {

    Button btnGestionEpp, btnReportes, btnAct, btnListaChequeo, btnBlog, btnAsesoramiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Referencias
        btnGestionEpp     = findViewById(R.id.btn_GestionEpp);
        btnReportes       = findViewById(R.id.btn_Reportes);
        btnAct            = findViewById(R.id.btn_Act);
        btnListaChequeo   = findViewById(R.id.btn_ListaChequeo);
        btnBlog           = findViewById(R.id.btn_Blog);
        btnAsesoramiento  = findViewById(R.id.btn_Asesoramiento);


        btnGestionEpp.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, lista_gestionEpp.class);
                startActivity(intent);
        });

         btnReportes.setOnClickListener(v -> {
             Intent intent = new Intent(Menu.this, lista_reportes.class);
             startActivity(intent);
         });
         btnAct.setOnClickListener(v -> {
             Intent intent = new Intent(Menu.this, lista_actLudicas.class);
             startActivity(intent);

         });

        btnListaChequeo.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, lista_listaChequeo.class);
            startActivity(intent);
        });

        // btnBlog.setOnClickListener(v -> {
        //     // TODO: Abrir interfaz de Blog
        // });

        // btnAsesoramiento.setOnClickListener(v -> {
        //     // TODO: Abrir interfaz de Asesoramiento
        // });
    }
}

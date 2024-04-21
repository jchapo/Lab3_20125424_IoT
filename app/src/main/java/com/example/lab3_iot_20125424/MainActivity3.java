package com.example.lab3_iot_20125424;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private TextView txtDirector;
    private TextView txtActores;
    private TextView txtFechaEstreno;
    private TextView txtGenero;
    private TextView txtIMDB;
    private TextView txtRottenTomatoes;
    private TextView txtMetacritic, txtEscritores, txtTrama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Inicializar TextViews
        txtDirector = findViewById(R.id.txtDirector);
        txtActores = findViewById(R.id.txtActores);
        txtFechaEstreno = findViewById(R.id.txtFechaEstreno);
        txtGenero = findViewById(R.id.txtGenero);
        txtIMDB = findViewById(R.id.txtIMDB);
        txtEscritores = findViewById(R.id.txtEscritores);
        txtRottenTomatoes = findViewById(R.id.txtRottenTomatoes);
        txtMetacritic = findViewById(R.id.txtMetacritic);
        txtTrama = findViewById(R.id.txtTramaPelicula);


        // Obtener los extras del Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Mostrar los valores en los TextViews
            txtDirector.setText(extras.getString("director"));
            txtActores.setText(extras.getString("actores"));
            txtFechaEstreno.setText(extras.getString("fecha_estreno"));
            txtGenero.setText(extras.getString("generos"));
            txtIMDB.setText(extras.getString("rating_imdb"));
            txtEscritores.setText(extras.getString("escritores"));
            txtRottenTomatoes.setText(extras.getString("rating_rotten_tomatoes"));
            txtMetacritic.setText(extras.getString("rating_metacritic"));
            txtTrama.setText(extras.getString("trama"));
        }
    }
}

package com.example.lab3_iot_20125424;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private TextView txtDirector;
    private TextView txtActores;
    private TextView txtFechaEstreno;
    private TextView txtGenero;
    private TextView txtIMDB;
    private TextView txtRottenTomatoes;
    private TextView txtMetacritic, txtEscritores, txtTrama,titulo;
    private CheckBox checkBoxRegresar;
    private Button btnRegresar;

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
        titulo = findViewById(R.id.titulo);
        checkBoxRegresar = findViewById(R.id.checkBoxRegresar);
        btnRegresar = findViewById(R.id.btnRegresar);

        // Deshabilitar botón al inicio
        btnRegresar.setEnabled(false);

        // Agregar listener al CheckBox
        checkBoxRegresar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Habilitar o deshabilitar el botón según el estado del CheckBox
                btnRegresar.setEnabled(isChecked);
            }
        });

        // Establecer un OnClickListener para el botón de regreso
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para volver a MainActivity
                Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                startActivity(intent); // Iniciar la actividad MainActivity
                finish(); // Cerrar la actividad actual
            }
        });


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
            titulo.setText(extras.getString("titulo"));
        }
    }
}

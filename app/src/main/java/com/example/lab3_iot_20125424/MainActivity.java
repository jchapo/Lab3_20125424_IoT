package com.example.lab3_iot_20125424;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_iot_20125424.databinding.ActivityMain3Binding;
import com.example.lab3_iot_20125424.databinding.ActivityMainBinding;
import com.example.lab3_iot_20125424.dto.DtoMovie;
import com.example.lab3_iot_20125424.dto.DtoRatings;
import com.example.lab3_iot_20125424.services.OMDBService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    OMDBService omdbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        omdbService = new Retrofit.Builder()
                .baseUrl("https://www.omdbapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OMDBService.class);

        binding.btnBuscar.setOnClickListener(view -> fetchMovieDetailsWs());
    }

    public void fetchMovieDetailsWs(){
        if(tengoInternet()){
            String idpelicula = String.valueOf(binding.textBuscadorPelicula.getText());
            omdbService.getMovieDetails("bf81d461",idpelicula).enqueue(new Callback<DtoMovie>() {
                @Override
                public void onResponse(Call<DtoMovie> call, Response<DtoMovie> response) {
                    //aca estoy en el UI Thread
                    if(response.isSuccessful()){
                        DtoMovie dtoMovie = response.body();
                        String actores = dtoMovie.getActors();
                        String director = dtoMovie.getDirector();
                        String fechaEstreno = dtoMovie.getReleased();
                        String generos = dtoMovie.getGenre();
                        String escritores = dtoMovie.getWriter();
                        String trama = dtoMovie.getPlot();
                        List<DtoRatings> listRatings = dtoMovie.getRatings();

                        String ratingIMDB = "";
                        String ratingRottenTomatoes = "";
                        String ratingMetacritic = "";

                        for(DtoRatings rating : listRatings) {
                            String source = rating.getSource();
                            if (source != null) { // Verificar si la cadena source no es nula
                                if(source.equals("Internet Movie Database")) {
                                    ratingIMDB = rating.getValue();
                                } else if(source.equals("Rotten Tomatoes")) {
                                    ratingRottenTomatoes = rating.getValue();
                                } else if(source.equals("Metacritic")) {
                                    ratingMetacritic = rating.getValue();
                                }
                            }
                        }


                        Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                        intent.putExtra("actores", actores);
                        intent.putExtra("director", director);
                        intent.putExtra("fecha_estreno", fechaEstreno);
                        intent.putExtra("generos", generos);
                        intent.putExtra("escritores", escritores);
                        intent.putExtra("trama", trama);
                        intent.putExtra("rating_imdb", ratingIMDB);
                        intent.putExtra("rating_rotten_tomatoes", ratingRottenTomatoes);
                        intent.putExtra("rating_metacritic", ratingMetacritic);

                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<DtoMovie> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public boolean tengoInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        boolean tieneInternet = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        Log.d("msg-test-internet", "Internet: " + tieneInternet);

        return tieneInternet;
    }
}

package com.example.lab3_iot_20125424;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_iot_20125424.databinding.ActivityMain3Binding;
import com.example.lab3_iot_20125424.databinding.ActivityMainBinding;
import com.example.lab3_iot_20125424.dto.DtoMovie;
import com.example.lab3_iot_20125424.dto.DtoPrime;
import com.example.lab3_iot_20125424.dto.DtoRatings;
import com.example.lab3_iot_20125424.services.OMDBService;
import com.example.lab3_iot_20125424.services.PrimeNumberService;
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
    private PrimeNumberService primeNumberService;
    List<DtoPrime> primes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Boolean hayInternet = tengoInternet();
        if(hayInternet){
            Toast.makeText(MainActivity.this, "Conexión a Internet establecida", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Conexión a Internet no disponible", Toast.LENGTH_LONG).show();
        }
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

        primeNumberService = new Retrofit.Builder()
                .baseUrl("https://prime-number-api.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PrimeNumberService.class);
        binding.btnVisualizar.setOnClickListener(view -> fetchPrimeFromWs());
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
                        String titulo = dtoMovie.getTitle();
                        String director = dtoMovie.getDirector();
                        String fechaEstreno = dtoMovie.getReleased();
                        String generos = dtoMovie.getGenre();
                        String escritores = dtoMovie.getWriter();
                        String trama = dtoMovie.getPlot();
                        List<DtoRatings> ratingsList = dtoMovie.getRatings();

                        String ratingIMDB = null;
                        String ratingRottenTomatoes = null;
                        String ratingMetacritic = null;

                        // Recorrer la lista de calificaciones
                        for (DtoRatings rating : ratingsList) {
                            String source = rating.getSource();
                            String value = rating.getValue();

                            // Determinar la fuente de la calificación y almacenar el valor correspondiente
                            if (source.equals("Internet Movie Database")) {
                                ratingIMDB = value;
                                Log.d("rating",ratingIMDB);
                            } else if (source.equals("Rotten Tomatoes")) {
                                ratingRottenTomatoes = value;
                                Log.d("rating",ratingRottenTomatoes);
                            } else if (source.equals("Metacritic")) {
                                ratingMetacritic = value;
                                Log.d("rating",ratingMetacritic);
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
                        intent.putExtra("titulo", titulo);
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

    public void fetchPrimeFromWs() {
        if (tengoInternet()) {
            primeNumberService.getPrimes(999, 1).enqueue(new Callback<List<DtoPrime>>() {
                @Override
                public void onResponse(Call<List<DtoPrime>> call, Response<List<DtoPrime>> response) {
                    if (response.isSuccessful()) {
                        primes = response.body();
                        // Convertir la lista de DtoPrime a una cadena de texto utilizando Gson
                        Gson gson = new Gson();
                        String primesAsString = gson.toJson(primes);

                        // Crear el Intent y pasar la cadena de primos como extra
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        intent.putExtra("primesAsString", primesAsString);
                        startActivity(intent);
                    } else {
                        Log.d("msg-test", "Respuesta no exitosa al obtener primos: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<DtoPrime>> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("msg-test", "Falla al obtener primos");
                }
            });
        }
    }

    // Método para convertir la lista de DtoPrime a una cadena de texto
    private String convertDtoPrimesToString(List<DtoPrime> primes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (DtoPrime prime : primes) {
            stringBuilder.append(prime.toString()).append("\n");
        }
        return stringBuilder.toString();
    }


    public boolean tengoInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        boolean tieneInternet = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        Log.d("msg-test-internet", "Internet: " + tieneInternet);

        return tieneInternet;
    }

}

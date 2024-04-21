package com.example.lab3_iot_20125424;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.lab3_iot_20125424.databinding.ActivityMain2Binding;
import com.example.lab3_iot_20125424.dto.DtoPrime;
import com.example.lab3_iot_20125424.services.PrimeNumberService;
import com.example.lab3_iot_20125424.viewmodel.ContadorViewModel;
import com.example.lab3_iot_20125424.workers.worker;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    private PrimeNumberService primeNumberService;
    private ExecutorService executorService;
    List<DtoPrime> primes;
    private boolean isPaused = false;
    private boolean isAscending = true; // Variable para controlar la dirección del contador
    private int contadorValue = 0; // Variable para almacenar el valor del contador

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        primeNumberService = new Retrofit.Builder()
                .baseUrl("https://prime-number-api.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PrimeNumberService.class);

        fetchPrimeFromWs();

        binding = ActivityMain2Binding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnIniciarPrimo.setOnClickListener(view -> {
            if (tengoInternet()) {
                Toast.makeText(MainActivity2.this, "Conexión a Internet establecida", Toast.LENGTH_SHORT).show();

                binding.layoutBoton.setVisibility(View.GONE);
                binding.layoutMostrarPrimo.setVisibility(View.VISIBLE);

                ApplicationThreads application = (ApplicationThreads) getApplication();
                executorService = application.executorService;

                ContadorViewModel contadorViewModel =
                        new ViewModelProvider(MainActivity2.this).get(ContadorViewModel.class);

                contadorViewModel.getContador().observe(this, contador -> {
                    contadorValue = contador; // Almacenar el valor del contador
                    String primotexto = String.valueOf(contador + 1);
                    binding.textViewOrdenPrimo.setText("El primo número " + primotexto + " es:");
                    DtoPrime primoDeOrden = primes.get(contador);
                    String primoTextoDeOrden = String.valueOf(primoDeOrden.getNumber());
                    binding.textViewMostrarPrimo.setText(primoTextoDeOrden);
                });

                // Iniciar un nuevo hilo o reanudar el hilo existente
                startOrResumeThread(contadorViewModel);

                binding.btnAscenderDescender.setOnClickListener(view1 -> {
                    isAscending = !isAscending;
                    binding.btnAscenderDescender.setText(isAscending ? "Descender" : "Ascender");
                    executorService.shutdownNow();
                    isPaused = true;
                    executorService.shutdownNow();
                    isPaused = false;
                    startOrResumeThread(contadorViewModel);
                });

                binding.btnPausarReiniciar.setOnClickListener(view3 -> {
                    if (isPaused) {
                        binding.btnPausarReiniciar.setText("Pausar");
                        binding.btnAscenderDescender.setVisibility(View.VISIBLE);
                        isPaused = false;
                        // Reanudar el hilo cuando se presiona el botón de pausar/reiniciar
                        startOrResumeThread(contadorViewModel);
                        Log.d("Juan","entra primero a boton pausar");
                    } else {
                        binding.btnPausarReiniciar.setText("Reiniciar");
                        binding.btnAscenderDescender.setVisibility(View.INVISIBLE);
                        isPaused = true;
                        // Detener el hilo cuando se presiona el botón de pausar/reiniciar
                        executorService.shutdownNow();
                        Log.d("Juan","entra segundo a boton pausar");

                    }
                });

            } else {
                Toast.makeText(MainActivity2.this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startOrResumeThread(ContadorViewModel contadorViewModel) {
        if (isPaused) {
            return; // No hacer nada si el hilo está pausado
        }

        // Detener el hilo anterior si existe
        if (executorService != null) {
            executorService.shutdownNow();
        }

        executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            int step = isAscending ? 1 : -1; // Dirección del contador
            for (int i = contadorValue; (isAscending ? (i <= 999 && !isPaused) : (i >= 0 && !isPaused)); i += step) {
                contadorViewModel.getContador().postValue(i);

                // Ajustar el contador según la dirección y los límites
                if (isAscending && i == 998) {
                    i = -1; // Reiniciar el contador a 0 si está en modo ascendente y alcanza 998
                } else if (!isAscending && i == 0) {
                    i = 999; // Reiniciar el contador a 998 si está en modo descendente y alcanza 0
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean tengoInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        boolean tieneInternet = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        Log.d("msg-test-internet", "Internet: " + tieneInternet);

        return tieneInternet;
    }

    public void fetchPrimeFromWs() {
        if (tengoInternet()) {
            primeNumberService.getPrimes(999, 1).enqueue(new Callback<List<DtoPrime>>() {
                @Override
                public void onResponse(Call<List<DtoPrime>> call, Response<List<DtoPrime>> response) {
                    if (response.isSuccessful()) {
                        primes = response.body();
                        // Verificar si la lista de primos se ha obtenido correctamente
                        if (primes != null && !primes.isEmpty()) {
                            // La lista de primos se ha obtenido correctamente, puedes hacer lo que necesites con ella aquí
                            // Por ejemplo, mostrarla en la vista, etc.
                            Log.d("msg-test", "Lista de primos obtenida correctamente");
                        } else {
                            // La lista de primos es nula o vacía, puede ser un indicador de un problema
                            Log.d("msg-test", "La lista de primos es nula o vacía");
                        }
                    } else {
                        // La solicitud no fue exitosa, maneja el error aquí si es necesario
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
}

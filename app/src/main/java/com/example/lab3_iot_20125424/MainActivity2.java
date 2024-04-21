package com.example.lab3_iot_20125424;

import android.content.Context;
import android.content.Intent;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

        binding = ActivityMain2Binding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String primesAsString = intent.getStringExtra("primesAsString");

        if (primesAsString != null) {
            primes = convertStringToDtoPrimes(primesAsString);
        }


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

        binding.btnAscenderDescender.setOnClickListener(view -> {
            // Cambiar el estado de la dirección del contador
            isAscending = !isAscending;

            // Cambiar el texto del botón según la dirección actual
            binding.btnAscenderDescender.setText(isAscending ? "Descender" : "Ascender");

            // Detener el hilo anterior si existe
            if (executorService != null) {
                executorService.shutdownNow();
            }

            // Crear un nuevo hilo de ejecución con la dirección actual del contador
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
                        // Capturar la excepción y salir del bucle
                        Log.e("MainActivity2", "El hilo fue interrumpido");
                        return;
                    }
                }
            });
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

        binding.btnBuscarOrden.setOnClickListener(view -> {
            // Obtener el texto del TextInputEditText textViewOrderPrimo
            String orderPrimoText = binding.textViewOrderPrimo.getText().toString();

            // Verificar si el texto no está vacío
            if (!orderPrimoText.isEmpty()) {
                try {
                    // Convertir el texto a un número entero
                    int orderPrimo = Integer.parseInt(orderPrimoText);

                    // Establecer el valor del contador
                    contadorValue = orderPrimo - 1; // Restar 1 porque el índice de la lista de primos comienza en 0

                    // Actualizar el ViewModel con el nuevo valor del contador
                    contadorViewModel.getContador().postValue(contadorValue);

                    // Iniciar o reanudar el hilo con el nuevo valor del contador y dirección ascendente
                    isAscending = true; // Asegurarse de que la dirección sea ascendente
                    startOrResumeThread(contadorViewModel);
                } catch (NumberFormatException e) {
                    // Manejar la excepción si el texto no se puede convertir a un número entero
                    Log.e("MainActivity2", "Error al convertir el texto a un número entero");
                    Toast.makeText(MainActivity2.this, "Ingrese un número válido", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Mostrar un mensaje si el campo está vacío
                Toast.makeText(MainActivity2.this, "Ingrese un número de orden de primo", Toast.LENGTH_SHORT).show();
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
                    // Capturar la excepción y salir del bucle
                    Log.e("MainActivity2", "El hilo fue interrumpido");
                    return;
                }
            }
        });
    }

    // Método para convertir la cadena de texto a una lista de objetos DtoPrime
    private List<DtoPrime> convertStringToDtoPrimes(String primesAsString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<DtoPrime>>(){}.getType();
        return gson.fromJson(primesAsString, listType);
    }
}

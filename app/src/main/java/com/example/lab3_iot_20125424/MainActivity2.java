package com.example.lab3_iot_20125424;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.lab3_iot_20125424.databinding.ActivityMain2Binding;
import com.example.lab3_iot_20125424.workers.worker;

import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    TypicodeService typicodeService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textViewMostrarPrimo.setOnClickListener(view -> {
            if (tengoInternet()) { // Verifica si hay conexión a Internet
                Toast.makeText(MainActivity2.this, "Conexión a Internet establecida", Toast.LENGTH_SHORT).show();

                typicodeService = new Retrofit.Builder()
                        .baseUrl("https://my-json-server.typicode.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(TypicodeService.class);

                binding.button3.setOnClickListener(view -> fetchProfileFromWs());


                UUID uuid = UUID.randomUUID();

                Data dataBuilder = new Data.Builder()
                        .putInt("numero", new Random().nextInt(10))
                        .build();

                WorkRequest workRequest = new OneTimeWorkRequest.Builder(worker.class)
                        .setId(uuid)
                        .setInputData(dataBuilder)
                        .build();

                WorkManager
                        .getInstance(MainActivity2.this.getApplicationContext())
                        .enqueue(workRequest);

                WorkManager.getInstance(binding.getRoot().getContext())
                        .getWorkInfoByIdLiveData(uuid)
                        .observe(MainActivity2.this, workInfo -> {
                            if (workInfo != null) {
                                Data progress = workInfo.getProgress();
                                int contador = progress.getInt("contador", 0);
                                Log.d("msg-test", "progress: " + contador);
                                binding.textViewMostrarPrimo.setText(String.valueOf(contador));
                            } else {
                                Log.d("msg-test", "work info == null ");
                            }
                        });
            } else {
                Toast.makeText(MainActivity2.this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            }
        });    }


    public boolean tengoInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        boolean tieneInternet = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        Log.d("msg-test-internet", "Internet: " + tieneInternet);

        return tieneInternet;
    }
}

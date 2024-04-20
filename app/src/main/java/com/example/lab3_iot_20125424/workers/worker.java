package com.example.lab3_iot_20125424.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

public class worker extends Worker {

    public worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        int contador = getInputData().getInt("numero",0);
        int contadorFinal = contador + 10;

        while (contador <= contadorFinal) {

            Log.d("msg-test-contador", "contador: " + contador);
            setProgressAsync(new Data.Builder().putInt("contador",contador).build());
            contador++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return Result.failure();
            }
        }

        return Result.success();
    }
}

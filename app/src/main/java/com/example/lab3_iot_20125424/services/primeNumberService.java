package com.example.lab3_iot_20125424.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class primeNumberService extends Service {
    public primeNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
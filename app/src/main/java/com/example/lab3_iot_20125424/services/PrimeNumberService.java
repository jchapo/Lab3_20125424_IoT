package com.example.lab3_iot_20125424.services;

import com.example.lab3_iot_20125424.dto.DtoPrime;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PrimeNumberService {

    @GET("/primeNumbers")
    Call<List<DtoPrime>> getPrimes(@Query("len") int len,
                                   @Query("order") int order);

}

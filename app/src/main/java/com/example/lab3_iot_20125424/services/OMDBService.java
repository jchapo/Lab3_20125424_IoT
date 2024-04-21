package com.example.lab3_iot_20125424.services;

import com.example.lab3_iot_20125424.dto.DtoMovie;
import com.example.lab3_iot_20125424.dto.DtoPrime;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OMDBService {

    @GET("/")
    Call<DtoMovie> getMovieDetails(
            @Query("apikey") String apiKey,
            @Query("i") String movieId
    );

}

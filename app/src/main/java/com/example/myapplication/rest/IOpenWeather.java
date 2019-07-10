package com.example.myapplication.rest;

import com.example.myapplication.rest.entities.WeatherRequestRestFiveDayModel;
import com.example.myapplication.rest.entities.WeatherRequestRestOneDayModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherRequestRestOneDayModel> loadOneDayWeather(@Query("q") String city,
                                                          @Query("appid") String keyApi,
                                                          @Query("units") String units);
    @GET("data/2.5/forecast")
    Call<WeatherRequestRestFiveDayModel> loadFiveDayWeather(@Query("q") String city,
                                                           @Query("appid") String keyApi,
                                                           @Query("units") String units);
}

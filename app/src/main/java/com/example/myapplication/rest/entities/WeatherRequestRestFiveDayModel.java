package com.example.myapplication.rest.entities;

import com.google.gson.annotations.SerializedName;

public class WeatherRequestRestFiveDayModel {
    @SerializedName("cod")
    public int cod;
    @SerializedName("message")
    public float message;
    @SerializedName("cnt")
    public int cnt;
    @SerializedName("list")
    public ListRestModel[] list;
    @SerializedName("city")
    public CityRestModel city;
}

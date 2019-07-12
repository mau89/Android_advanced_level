package com.example.myapplication.rest.entities;

import com.google.gson.annotations.SerializedName;

public class ListRestModel {
    @SerializedName("dt")
    public long dt;
    @SerializedName("main")
    public MainRestModel main;
    @SerializedName("weather")
    public WeatherRestModel[] weather;
    @SerializedName("clouds")
    public CloudsRestModel clouds;
    @SerializedName("wind")
    public WindRestModel wind;
    @SerializedName("sys")
    public SysFiveDayRestModel sys;
    @SerializedName("dt_txt")
    public String dt_txt;
}

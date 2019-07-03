package com.example.myapplication.rest.entities;

import com.google.gson.annotations.SerializedName;

public class CityRestModel {
    @SerializedName("id")
    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("coord")
    public CoordFiveDayRestModel coordinates;
    @SerializedName("country")
    public String country;
    @SerializedName("population")
    public String population;
}

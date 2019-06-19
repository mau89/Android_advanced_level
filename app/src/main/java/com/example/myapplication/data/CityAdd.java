package com.example.myapplication.data;

import java.util.Objects;

public class CityAdd {
    private String stringCity;
    private String stringTemperature;
    int currentID;

    public CityAdd(String stringCity, String stringTemperature, int currentID) {
        this.stringCity = stringCity;
        this.stringTemperature = stringTemperature;
        this.currentID = currentID;
    }

    public int getCurrentID() {
        return currentID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityAdd cityAdd = (CityAdd) o;
        return currentID == cityAdd.currentID &&
                Objects.equals(stringCity, cityAdd.stringCity) &&
                Objects.equals(stringTemperature, cityAdd.stringTemperature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringCity, stringTemperature, currentID);
    }

    public String getStringCity() {
        return stringCity;
    }

    public String getStringTemperature() {
        return stringTemperature;
    }

    @Override
    public String toString() {
        return "CityAdd{" +
                "stringCity='" + stringCity + '\'' +
                ", stringTemperature='" + stringTemperature + '\'' +
                '}';
    }
}

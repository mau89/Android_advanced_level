package com.example.myapplication.data;

import java.util.Objects;

public class VillageAdd {
    private String stringDay;
    private String stringTemperature;
    private int currentID;

    public VillageAdd(String stringDay, String stringTemperature, int currentID) {
        this.stringDay = stringDay;
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
        VillageAdd that = (VillageAdd) o;
        return currentID == that.currentID &&
                Objects.equals(stringDay, that.stringDay) &&
                Objects.equals(stringTemperature, that.stringTemperature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringDay, stringTemperature, currentID);
    }

    public String getStringDay() {
        return stringDay;
    }

    public String getStringTemperature() {
        return stringTemperature;
    }

    @Override
    public String toString() {
        return "VillageAdd{" +
                "stringDay='" + stringDay + '\'' +
                ", stringTemperature='" + stringTemperature + '\'' +
                '}';
    }
}

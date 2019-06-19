package com.example.myapplication.data;


import java.util.Objects;

public final class HistoryCity {
    private String stringDay;
    private int drawableImageViewDay;
    private String stringTextViewTemperatureDay;

    public HistoryCity(String stringDay, int drawableImageViewDay, String stringTextViewTemperatureDay) {
        this.stringDay = stringDay;
        this.drawableImageViewDay = drawableImageViewDay;
        this.stringTextViewTemperatureDay = stringTextViewTemperatureDay;
    }

    public String getStringDay() {
        return stringDay;
    }

    public int getDrawableImageViewDay() {
        return drawableImageViewDay;
    }

    public String getStringTextViewTemperatureDay() {
        return stringTextViewTemperatureDay;
    }

    @Override
    public String toString() {
        return "HistoryCity{" +
                "stringDay='" + stringDay + '\'' +
                ", drawableImageViewDay='" + drawableImageViewDay + '\'' +
                ", stringTextViewTemperatureDay='" + stringTextViewTemperatureDay + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryCity that = (HistoryCity) o;
        return drawableImageViewDay == that.drawableImageViewDay &&
                Objects.equals(stringDay, that.stringDay) &&
                Objects.equals(stringTextViewTemperatureDay, that.stringTextViewTemperatureDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringDay, drawableImageViewDay, stringTextViewTemperatureDay);
    }
}

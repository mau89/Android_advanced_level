package com.example.myapplication.utils;

import android.content.SharedPreferences;

public class Preferences {
    private final SharedPreferences preferences;

    public enum Key {
        CITY,
        EDIT_CITY
    }

    public Preferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void putString(String string, Key key) {
        preferences.edit().putString(key.name(), string).apply();
    }

    public String getString(Key key) {
        return preferences.getString(key.name(), "");
    }
}

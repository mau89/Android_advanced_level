package com.example.myapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CityDataBaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "HistoryCity";
    private static final int VERSION = 1;
    public static final String TABLE_CONTACTS = "contacts";
    public static final String KEY_ID = "_id";
    public static final String APP_PREFERENCES_cityName = "cityName";
    public static final String APP_PREFERENCES_pressure_switch = "APP_PREFERENCES_pressure_switch";
    public static final String APP_PREFERENCES_pressure_speed_wind = "APP_PREFERENCES_pressure_speed_wind";
    public static final String APP_PREFERENCES_pressure_wetness = "APP_PREFERENCES_pressure_wetness";

    public CityDataBaseHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CONTACTS + "(" +
                KEY_ID + " integer primary key autoincrement," +
                APP_PREFERENCES_cityName + " text, " +
                APP_PREFERENCES_pressure_switch + " integer, " +
                APP_PREFERENCES_pressure_speed_wind + " integer, " +
                APP_PREFERENCES_pressure_wetness + " integer " + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CONTACTS);
        onCreate(db);
    }
}

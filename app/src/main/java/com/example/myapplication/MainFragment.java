package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.CityDataBaseHelper;
import com.example.myapplication.data.HistoryCity;
import com.example.myapplication.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainFragment extends Fragment {
    private CityDataBaseHelper cityDataBaseHelper;
    private SQLiteDatabase database;
    private SensorManager sensorManager;
    private Sensor sensorAmbientTemperature;
    private Sensor sensorRelativeHumidity;
    private TextView textViewAmbientTemperature;
    private TextView textViewRelativeHumidity;

    private static List<HistoryCity> generateCity() {
        List<HistoryCity> historyCityList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {

            historyCityList.add(new HistoryCity(String.valueOf(12 + i), RandomList(),
                    ((int) (Math.random() * 10 + i)) + " \u2103"));
        }
        return historyCityList;
    }

    private static int RandomList() {
        if (Math.random() < 0.2) {
            return R.drawable.light_rain;
        } else if (Math.random() > 0.2 && Math.random() < 0.4) {
            return R.drawable.little_cloudy;
        } else if (Math.random() > 0.4 && Math.random() < 0.6) {
            return R.drawable.overcast;
        } else if (Math.random() > 0.6 && Math.random() < 0.8) {
            return R.drawable.rain;
        } else {
            return R.drawable.sun;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        cityDataBaseHelper = ((App) getActivity().getApplication()).getCityDataBaseHelper();
        database = cityDataBaseHelper.getReadableDatabase();

        textViewAmbientTemperature = view.findViewById(R.id.ambient_temperature);
        textViewRelativeHumidity = view.findViewById(R.id.relative_humidity);
        String city = ((App) getActivity().getApplication()).getPreferences().getString(Preferences.Key.CITY);
        Timber.d(city);
        if (!city.isEmpty()) {
            loadCity(city, view);
        }
        setUpRecyclerView(view.findViewById(R.id.linerHistory));
        showSensors();
        return view;
    }

    private void showSensors() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorAmbientTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorRelativeHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (sensorAmbientTemperature != null) {
            sensorManager.registerListener(listenerAmbientTemperature, sensorAmbientTemperature,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorRelativeHumidity != null) {
            sensorManager.registerListener(listenerRelativeHumidity, sensorRelativeHumidity,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private SensorEventListener listenerAmbientTemperature = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            showSensorAmbientTemperature(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private SensorEventListener listenerRelativeHumidity = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            showSensorRelativeHumidity(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void showSensorAmbientTemperature(SensorEvent event) {
        textViewAmbientTemperature.setText(new StringBuilder().append("").append(event.values[0]).toString());
    }

    private void showSensorRelativeHumidity(SensorEvent event) {
        textViewRelativeHumidity.setText(new StringBuilder().append("").append(event.values[0]).toString());
    }

    private void setUpRecyclerView(LinearLayout historyLayout) {
        View historyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_weather_history, historyLayout, false);
        RecyclerView recyclerView = historyView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(new HistoryDayAdapter(generateCity()));
        historyLayout.addView(historyView);
    }

    private void loadCity(String data, View view) {
        if (database == null) {
            return;
        }
        String[] projection = {
                CityDataBaseHelper.KEY_ID,
                CityDataBaseHelper.APP_PREFERENCES_cityName,
                CityDataBaseHelper.APP_PREFERENCES_pressure_switch,
                CityDataBaseHelper.APP_PREFERENCES_pressure_speed_wind,
                CityDataBaseHelper.APP_PREFERENCES_pressure_wetness};
        String selection = "_id = ?";
        String[] selectionArgs = new String[]{data};
        Cursor cursor = database.query(
                CityDataBaseHelper.TABLE_CONTACTS,
                projection,            // столбцы
                selection,                  // столбцы для условия WHERE
                selectionArgs,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);
        while (cursor.moveToNext()) {
            String APP_PREFERENCES_cityName = cursor.getString(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_cityName));
            int APP_PREFERENCES_pressure_speed_wind = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_speed_wind));
            int APP_PREFERENCES_pressure_switch = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_switch));
            int APP_PREFERENCES_pressure_wetness = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_wetness));
            TextView textViewCity = view.findViewById(R.id.City);
            textViewCity.setText(APP_PREFERENCES_cityName);
            TextView textView4 = view.findViewById(R.id.textView4);
            TextView textView5 = view.findViewById(R.id.textView5);
            TextView textView6 = view.findViewById(R.id.textView6);
            textView4.setVisibility(APP_PREFERENCES_pressure_switch == 0 ? View.GONE : View.VISIBLE);
            textView5.setVisibility(APP_PREFERENCES_pressure_wetness == 0 ? View.GONE : View.VISIBLE);
            textView6.setVisibility(APP_PREFERENCES_pressure_speed_wind == 0 ? View.GONE : View.VISIBLE);
        }
        cursor.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        database.close();
        cityDataBaseHelper.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerRelativeHumidity, sensorRelativeHumidity);
        sensorManager.unregisterListener(listenerAmbientTemperature, sensorAmbientTemperature);
    }
}


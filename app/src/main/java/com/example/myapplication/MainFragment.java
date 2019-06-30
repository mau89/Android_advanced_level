package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.CityDataBaseHelper;
import com.example.myapplication.data.HistoryCity;
import com.example.myapplication.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class MainFragment extends Fragment {
    private CityDataBaseHelper cityDataBaseHelper;
    private SQLiteDatabase database;
    private SensorManager sensorManager;
    private Sensor sensorAmbientTemperature;
    private Sensor sensorRelativeHumidity;
    private TextView textViewAmbientTemperature;
    private TextView textViewRelativeHumidity;
    private String cityName;
    private final Handler handler = new Handler();
    private View view;

    private List<HistoryCity> generateCity() {
        List<HistoryCity> historyCityList = new ArrayList<>();

        Integer[] integers = new Integer[15];
//        MyAsyncTask asyncTask = new MyAsyncTask();
//        for (int i = 0; i < 15; i++) {
//            integers[i] = i;
//        }
//        asyncTask.execute(integers);

        for (int i = 0; i < 15; i++) {
            historyCityList.add(new HistoryCity(getDate(i), RandomList(),
                    ((int) (Math.random() * 10 + i)) + " \u2103"));
        }

        Intent intent = new Intent((getActivity().getApplicationContext()), MyService.class);
        intent.putExtra(MyService.CITY, cityName);
        intent.putExtra(MyService.CURRENTDATE, getDate(0));
        getActivity().startService(intent);
        return historyCityList;
    }

    private String getDate(int i) {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, i);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM");
        return dateFormat.format(date.getTime());
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            @Override
            public void run() {
                final JSONObject jsonObject = WeatherCurrentDay.getJSONData(city);
                if (jsonObject == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Place not found", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(jsonObject);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject jsonObject) {
        Timber.d("json: " + jsonObject.toString());
        try {
            JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject wind = jsonObject.getJSONObject("wind");

            setDetails(details, main, wind);
            setCurrentTemp(main);
            setUpdatedText(jsonObject);
            setWeatherIcon(details.getInt("id"),
                    jsonObject.getJSONObject("sys").getLong("sunrise") * 1000,
                    jsonObject.getJSONObject("sys").getLong("sunset") * 1000,
                    details.getString("description"));
        } catch (Exception exc) {
            exc.printStackTrace();
            Timber.e("One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset, String weather_description) {
        int id = actualId / 100;
        String icon = "";

        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = "\u2600";
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2: {
                    icon = getString(R.string.weather_thunder);
                    break;
                }
                case 3: {
                    icon = getString(R.string.weather_drizzle);
                    break;
                }
                case 5: {
                    icon = getString(R.string.weather_rainy);
                    break;
                }
                case 6: {
                    icon = getString(R.string.weather_snowy);
                    break;
                }
                case 7: {
                    icon = getString(R.string.weather_foggy);
                    break;
                }
                case 8: {
                    icon = "\u2601";
                    // icon = getString(R.string.weather_cloudy);
                    break;
                }
            }
        }
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String sunriseText = dateFormat.format(new Date(sunrise));
        TextView sunriseTextView = view.findViewById(R.id.sunrise);
        sunriseTextView.setText(sunriseText);
        String sunsetText = dateFormat.format(new Date(sunset));
        TextView sunsetTextView = view.findViewById(R.id.sunset);
        sunsetTextView.setText(sunsetText);
        TextView weather_icon = view.findViewById(R.id.weather_icon);

        TextView weatherDescription = view.findViewById(R.id.weather_description);
        weatherDescription.setText(weather_description);
        weather_icon.setText(icon);
    }

    private void setUpdatedText(JSONObject jsonObject) throws JSONException {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = dateFormat.format(new Date(jsonObject.getLong("dt") * 1000));
        String updatedText = "Last update: " + updateOn;
        TextView updatedTextView = view.findViewById(R.id.updatedTextView);
        updatedTextView.setText(updatedText);
    }

    private void setCurrentTemp(JSONObject main) throws JSONException {
        String currentTextText = String.format(Locale.getDefault(), "%.2f", main.getDouble("temp"))
                + " \u2103";
        TextView currentTemperatureTextView = view.findViewById(R.id.currentTemperatureTextView);
        currentTemperatureTextView.setText(currentTextText);
    }

    private void setDetails(JSONObject details, JSONObject main, JSONObject wind) throws JSONException {
        String PressureText = main.getString("pressure") + " hPa";
        TextView PressureTextView = view.findViewById(R.id.Pressure);
        PressureTextView.setText(PressureText);

        String HumidityText = main.getString("humidity") + " %";
        TextView HumidityTextView = view.findViewById(R.id.Humidity);
        HumidityTextView.setText(HumidityText);
        String WindText = wind.getString("speed") + " m/s";
        TextView WindTextView = view.findViewById(R.id.Wind);
        WindTextView.setText(WindText);

    }

//    private void setPlaceName(JSONObject jsonObject) throws JSONException {
//    private void setPlaceName(JSONObject jsonObject) throws JSONException {
//        String cityText = jsonObject.getString("name").toUpperCase() + ", "
//                + jsonObject.getJSONObject("sys").getString("country");
//        cityTextView.setText(cityText);
//    }

    private int RandomList() {
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
        view = inflater.inflate(R.layout.fragment_main, container, false);

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
        updateWeatherData(cityName);
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
            cityName = cursor.getString(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_cityName));
            int APP_PREFERENCES_pressure_speed_wind = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_speed_wind));
            int APP_PREFERENCES_pressure_switch = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_switch));
            int APP_PREFERENCES_pressure_wetness = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_wetness));
            TextView textViewCity = view.findViewById(R.id.City);
            textViewCity.setText(cityName);
            TextView textView4 = view.findViewById(R.id.Pressure);
            TextView textView5 = view.findViewById(R.id.Humidity);
            TextView textView6 = view.findViewById(R.id.Wind);
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

//    private class MyAsyncTask extends AsyncTask<Integer, String, String> {
//
//        @Override
//        protected String doInBackground(Integer... integers) {
//
//
//
//
//            return null;
//        }
//
//
//    }
}


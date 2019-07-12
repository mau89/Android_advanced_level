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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.CityDataBaseHelper;
import com.example.myapplication.data.HistoryCity;
import com.example.myapplication.rest.OpenWeatherRepo;
import com.example.myapplication.rest.entities.WeatherRequestRestFiveDayModel;
import com.example.myapplication.rest.entities.WeatherRequestRestOneDayModel;
import com.example.myapplication.utils.Preferences;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private View view;
    private List<HistoryCity> historyCityList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        cityDataBaseHelper = ((App) Objects.requireNonNull(getActivity()).getApplication()).getCityDataBaseHelper();
        database = cityDataBaseHelper.getReadableDatabase();

        textViewAmbientTemperature = view.findViewById(R.id.ambient_temperature);
        textViewRelativeHumidity = view.findViewById(R.id.relative_humidity);

        String city = ((App) getActivity().getApplication()).getPreferences().getString(Preferences.Key.CITY);
        Timber.d(city);
        if (!city.isEmpty()) {
            loadCity(city, view);
        }
        Timber.i(city);
        Timber.i(cityName);
        updateWeatherFiveDayData(cityName);
        showSensors();
        updateWeatherOneDayData(cityName);
        return view;
    }

    private List<HistoryCity> generateCity() {
        List<HistoryCity> historyCityList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            historyCityList.add(new HistoryCity(getDate(i), "Icon_weather",
                    ((int) (Math.random() * 10 + i)) + " \u2103"));
        }
        Intent intent = new Intent((Objects.requireNonNull(getActivity()).getApplicationContext()), MyService.class);
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

    private void updateWeatherOneDayData(final String city) {
        OpenWeatherRepo.getSingleton().getAPI().loadOneDayWeather(city + ",ru",
                "762ee61f52313fbd10a4eb54ae4d4de2", "metric")
                .enqueue(new Callback<WeatherRequestRestOneDayModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestOneDayModel> call,
                                           @NonNull Response<WeatherRequestRestOneDayModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            renderWeatherOneDay(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<WeatherRequestRestOneDayModel> call, @NotNull Throwable t) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWeatherFiveDayData(final String city1) {
        //  Toast.makeText(getActivity(),city1,Toast.LENGTH_SHORT).show();
        Timber.d("Зашел999999999");
        OpenWeatherRepo.getSingleton().getAPI().loadFiveDayWeather("Moscow",
                "762ee61f52313fbd10a4eb54ae4d4de2", "metric")
                .enqueue(new Callback<WeatherRequestRestFiveDayModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestFiveDayModel> call,
                                           @NonNull Response<WeatherRequestRestFiveDayModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            renderWeatherFiveDay(response.body());
                            Timber.d("Зашел123");
                            Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), "зашел !!!!!!!!!!!!!!!!!!!!!!!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call<WeatherRequestRestFiveDayModel> call1, @NotNull Throwable t) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                        Timber.d("Зашел321");
                    }
                });
    }

    private void renderWeatherOneDay(WeatherRequestRestOneDayModel model) {
        setDetails(model.main.humidity, model.main.pressure, model.wind.speed);
        setCurrentTemp(model.main.temp);
        setUpdatedText(model.dt);
        setWeatherIcon(model.weather[0].id,
                model.sys.sunrise * 1000,
                model.sys.sunset * 1000,
                model.weather[0].description);
    }

    private void renderWeatherFiveDay(WeatherRequestRestFiveDayModel model) {
        historyCityList = new ArrayList<>();
        for (int i = 0; i < model.list.length; i++) {
            historyCityList.add(new HistoryCity(model.list[i].dt_txt, setWeatherFiveDayIcon(model.list[i].weather[0].id),
                    ((int) model.list[i].main.temp) + " \u2103"));
        }
        setUpRecyclerView(view.findViewById(R.id.linerHistory));
    }

    private String setWeatherFiveDayIcon(int actualId) {
        int id = actualId / 100;
        String imageURL = "https://image.flaticon.com/icons/png/512/103/103085.png";
        if (actualId == 800) {
            imageURL = "https://image.flaticon.com/icons/png/512/54/54455.png";
        } else {
            switch (id) {
                case 2: {
                    imageURL = "https://image.flaticon.com/icons/png/512/91/91981.png";
                    break;
                }
                case 3: {
                    imageURL = "https://image.flaticon.com/icons/png/512/106/106044.png";
                    break;
                }
                case 5: {
                    imageURL = "https://image.flaticon.com/icons/png/512/116/116251.png";
                    break;
                }
                case 6: {
                    imageURL = "https://image.flaticon.com/icons/png/512/52/52052.png";
                    break;
                }
                case 7: {
                    imageURL = "https://image.flaticon.com/icons/png/512/106/106055.png";
                    break;
                }
                case 8: {
                    imageURL = "https://image.flaticon.com/icons/png/512/53/53562.png";
                    break;
                }
            }
        }
        return imageURL;
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset, String weather_description) {
        int id = actualId / 100;
        ImageView weather_icon = view.findViewById(R.id.weather_icon);
        LoadImage loadImage = new LoadImage();
        String imageURL = "https://image.flaticon.com/icons/png/512/103/103085.png";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                imageURL = "https://image.flaticon.com/icons/png/512/54/54455.png";
            } else {
                imageURL = "https://image.flaticon.com/icons/png/512/53/53381.png";
            }
        } else {
            switch (id) {
                case 2: {
                    imageURL = "https://image.flaticon.com/icons/png/512/91/91981.png";
                    break;
                }
                case 3: {
                    imageURL = "https://image.flaticon.com/icons/png/512/106/106044.png";
                    break;
                }
                case 5: {
                    imageURL = "https://image.flaticon.com/icons/png/512/116/116251.png";
                    break;
                }
                case 6: {
                    imageURL = "https://image.flaticon.com/icons/png/512/52/52052.png";
                    break;
                }
                case 7: {
                    imageURL = "https://image.flaticon.com/icons/png/512/106/106055.png";
                    break;
                }
                case 8: {
                    imageURL = "https://image.flaticon.com/icons/png/512/53/53562.png";
                    break;
                }
            }
        }
        loadImage.Load(weather_icon, imageURL);
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String sunriseText = dateFormat.format(new Date(sunrise));
        TextView sunriseTextView = view.findViewById(R.id.sunrise);
        sunriseTextView.setText(sunriseText);
        String sunsetText = dateFormat.format(new Date(sunset));
        TextView sunsetTextView = view.findViewById(R.id.sunset);
        sunsetTextView.setText(sunsetText);
        TextView weatherDescription = view.findViewById(R.id.weather_description);
        weatherDescription.setText(weather_description);
    }

    private void setUpdatedText(long dt) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = dateFormat.format(dt * 1000);
        String updatedText = "Last update: " + updateOn;
        TextView updatedTextView = view.findViewById(R.id.updatedTextView);
        updatedTextView.setText(updatedText);
    }

    private void setCurrentTemp(float temp) {
        String currentTextText = String.format(Locale.getDefault(), "%.2f", temp) + " \u2103";
        TextView currentTemperatureTextView = view.findViewById(R.id.currentTemperatureTextView);
        currentTemperatureTextView.setText(currentTextText);
    }

    private void setDetails(int humidity, int pressure, float speed) {
        String PressureText = pressure + " hPa";
        TextView PressureTextView = view.findViewById(R.id.Pressure);
        PressureTextView.setText(PressureText);
        String HumidityText = humidity + " %";
        TextView HumidityTextView = view.findViewById(R.id.Humidity);
        HumidityTextView.setText(HumidityText);
        String WindText = speed + " m/s";
        TextView WindTextView = view.findViewById(R.id.Wind);
        WindTextView.setText(WindText);
    }

    private void showSensors() {
        sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);
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
        recyclerView.setAdapter(new HistoryDayAdapter(historyCityList));
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
            TextView Pressure = view.findViewById(R.id.Pressure);
            TextView Humidity = view.findViewById(R.id.Humidity);
            TextView Wind = view.findViewById(R.id.Wind);
            Pressure.setVisibility(APP_PREFERENCES_pressure_switch == 0 ? View.GONE : View.VISIBLE);
            Humidity.setVisibility(APP_PREFERENCES_pressure_wetness == 0 ? View.GONE : View.VISIBLE);
            Wind.setVisibility(APP_PREFERENCES_pressure_speed_wind == 0 ? View.GONE : View.VISIBLE);
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


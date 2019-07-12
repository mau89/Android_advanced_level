package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.data.CityDataBaseHelper;
import com.example.myapplication.utils.Preferences;

import java.util.Collections;
import java.util.Objects;

import timber.log.Timber;

public class AddingCitiesFragment extends Fragment {
    private CityDataBaseHelper cityDataBaseHelper;
    private SQLiteDatabase database;
    private String[] cityArray = {"Выберите город", "Москва", "Санкт-Петербург", "Самара", "Тюмень", "Уфа", "Владивосток", "Новосибирск"};
    private String citySelected;
    private Spinner spinner;
    private View view;
    private String city;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_adding_cities, container, false);

        cityDataBaseHelper = ((App) getActivity().getApplication()).getCityDataBaseHelper();
        database = cityDataBaseHelper.getReadableDatabase();

        getCity();
        city = ((App) getActivity().getApplication()).getPreferences().getString(Preferences.Key.EDIT_CITY);
        if (!city.equals("")) {
            loadCity(city, view);
        }
        addCity();
        return view;
    }

    private void addCity() {
        view.findViewById(R.id.add_city).setOnClickListener(v -> {
            SQLiteDatabase database = cityDataBaseHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            Switch pressure_switch1 = view.findViewById(R.id.pressure_switch);
            Switch pressure_speed1_wind = view.findViewById(R.id.pressure_speed_wind);
            Switch pressure_wetness = view.findViewById(R.id.pressure_wetness);
            if (!city.equals("")) {
                contentValues.put(CityDataBaseHelper.APP_PREFERENCES_cityName, citySelected);
                contentValues.put(CityDataBaseHelper.APP_PREFERENCES_pressure_speed_wind, (pressure_speed1_wind.isChecked() ? 1 : 0));
                contentValues.put(CityDataBaseHelper.APP_PREFERENCES_pressure_switch, (pressure_switch1.isChecked() ? 1 : 0));
                contentValues.put(CityDataBaseHelper.APP_PREFERENCES_pressure_wetness, (pressure_wetness.isChecked() ? 1 : 0));
                cityDataBaseHelper.getWritableDatabase().update(CityDataBaseHelper.TABLE_CONTACTS, contentValues, CityDataBaseHelper.KEY_ID + "=?", new String[]{city});
                Objects.requireNonNull(getActivity()).onBackPressed();
            } else {
                contentValues.put(CityDataBaseHelper.APP_PREFERENCES_cityName, citySelected);
                contentValues.put(CityDataBaseHelper.APP_PREFERENCES_pressure_speed_wind, (pressure_speed1_wind.isChecked() ? 1 : 0));
                contentValues.put(CityDataBaseHelper.APP_PREFERENCES_pressure_switch, (pressure_switch1.isChecked() ? 1 : 0));
                contentValues.put(CityDataBaseHelper.APP_PREFERENCES_pressure_wetness, (pressure_wetness.isChecked() ? 1 : 0));
                database.insert(CityDataBaseHelper.TABLE_CONTACTS, null, contentValues);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
    }

    private void getCity() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cityArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = view.findViewById(R.id.select_city);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Город");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getItemAtPosition(position).equals("Выберите город")) {
                    Timber.d(spinner.getSelectedItem().toString());
                    citySelected = spinner.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadCity(String edit_city, View view) {
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
        String[] selectionArgs = new String[]{edit_city};
        Cursor cursor = database.query(
                CityDataBaseHelper.TABLE_CONTACTS,
                projection,            // столбцы
                selection,                  // столбцы для условия WHERE
                selectionArgs,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);
        while (cursor.moveToNext()) {
            String cityName = cursor.getString(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_cityName));
            int speedWind = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_speed_wind));
            int pressureSwitch = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_switch));
            int Wetness = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_pressure_wetness));
            spinner.setSelection(Collections.singletonList(city).indexOf(cityName));
            Switch pressure_switch = view.findViewById(R.id.pressure_switch);
            Switch pressure_wetness = view.findViewById(R.id.pressure_wetness);
            Switch pressure_speed_wind = view.findViewById(R.id.pressure_speed_wind);
            pressure_switch.setChecked(pressureSwitch == 0 ? false : true);
            pressure_wetness.setChecked(Wetness == 0 ? false : true);
            pressure_speed_wind.setChecked(speedWind == 0 ? false : true);
        }
        cursor.close();
    }

    public void onDestroyView() {
        super.onDestroyView();
        database.close();
        cityDataBaseHelper.close();
    }
}


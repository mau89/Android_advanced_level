package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.CityAdd;
import com.example.myapplication.data.CityDataBaseHelper;
import com.example.myapplication.data.VillageAdd;
import com.example.myapplication.rest.OpenWeatherRepo;
import com.example.myapplication.rest.entities.WeatherRequestRestOneDayModel;
import com.example.myapplication.utils.Preferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MenuFragment extends Fragment {
    private List<Object> cityAdds = new ArrayList<>();
    private CityDataBaseHelper cityDataBaseHelper;
    private SQLiteDatabase database;
    private static final int IDM_OPEN = 101;
    private View view;
    private int idCity;
    private String currentTextText;
    private CityAddAdapter cityAddAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        cityDataBaseHelper = ((App) Objects.requireNonNull(getActivity()).getApplication()).getCityDataBaseHelper();
        database = cityDataBaseHelper.getReadableDatabase();

        view.findViewById(R.id.add_location).setOnClickListener(v -> {
            ((App) getActivity().getApplication()).getPreferences().putString("", Preferences.Key.EDIT_CITY);
            Objects.requireNonNull(getFragmentManager()).beginTransaction()
                    .replace(R.id.container, new AddingCitiesFragment())
                    .addToBackStack(MenuFragment.class.getName())
                    .commit();
        });
        Timber.d("onCreateView");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (database == null) {
            return;
        }
        String[] projection = {
                CityDataBaseHelper.KEY_ID,
                CityDataBaseHelper.APP_PREFERENCES_cityName,
                CityDataBaseHelper.APP_PREFERENCES_pressure_switch,
                CityDataBaseHelper.APP_PREFERENCES_pressure_speed_wind,
                CityDataBaseHelper.APP_PREFERENCES_pressure_wetness};
        Cursor cursor = database.query(
                CityDataBaseHelper.TABLE_CONTACTS,
                projection,
                null,
                null,
                null,
                null,
                null);
        cityAdds.clear();
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.KEY_ID));
            String cityName = cursor.getString(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_cityName));
            Timber.d(cityName);
            updateWeatherData(cityName);
            if (cityName == null) {
                deleteRow(String.valueOf(currentID));
            } else if (cityName.substring(0, 2).equals("п.")) {
                cityAdds.add(new VillageAdd(cityName, currentTextText, currentID));
            } else {
                cityAdds.add(new CityAdd(cityName, currentTextText, currentID));
            }
        }
        setUpRecyclerView(view.findViewById(R.id.linear_city));
        cursor.close();
    }

    private void updateWeatherData(final String city) {
        OpenWeatherRepo.getSingleton().getAPI().loadOneDayWeather(city + ",ru",
                "762ee61f52313fbd10a4eb54ae4d4de2", "metric")
                .enqueue(new Callback<WeatherRequestRestOneDayModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestOneDayModel> call,
                                           @NonNull Response<WeatherRequestRestOneDayModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            renderWeather(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<WeatherRequestRestOneDayModel> call, @NotNull Throwable t) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderWeather(WeatherRequestRestOneDayModel model) {
        setCurrentTemp(model.main.temp);
    }

    private void setCurrentTemp(float temp) {
        currentTextText = String.format(Locale.getDefault(), "%.1f", temp) + " \u2103";
        cityAddAdapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView(LinearLayout historyLayout) {
        View historyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_city_list, historyLayout, false);
        RecyclerView recyclerView = historyView.findViewById(R.id.recycler_city_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        setCityAdapter();
        recyclerView.setAdapter(cityAddAdapter);
        historyLayout.addView(historyView);
    }

    private void setCityAdapter() {
        cityAddAdapter = new CityAddAdapter(cityAdds);
        cityAddAdapter.notifyDataSetChanged();
        cityAddAdapter.setOnClickCity(objects -> {
            if (objects instanceof CityAdd) {
                idCity = ((CityAdd) objects).getCurrentID();
            } else {
                idCity = ((VillageAdd) objects).getCurrentID();
            }
            ((App) Objects.requireNonNull(getActivity()).getApplication()).getPreferences().putString(String.valueOf(idCity), Preferences.Key.CITY);
            getActivity().onBackPressed();
        });
        cityAddAdapter.setOnLongClickCity(objects -> {
            if (objects instanceof CityAdd) {
                idCity = ((CityAdd) objects).getCurrentID();
            } else {
                idCity = ((VillageAdd) objects).getCurrentID();
            }
            ((App) Objects.requireNonNull(getActivity()).getApplication()).getPreferences()
                    .putString(String.valueOf(idCity), Preferences.Key.EDIT_CITY);
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new AddingCitiesFragment())
                    .addToBackStack(MenuFragment.class.getName())
                    .commit();
        });
        //TODO не реализовано удаление городов
        //cityAddAdapter.setOnClickDelButton(object -> {
        //Toast.makeText(getActivity(),"asfsdf",Toast.LENGTH_SHORT).show();
        //if (object instanceof CityAdd) {
        //idCity = ((CityAdd) object).getCurrentID();
        //} else {
        //idCity = ((VillageAdd) object).getCurrentID();
        //}
        //deleteRow(String.valueOf(idCity));
        //cityAddAdapter.removeItem(object);
        //});
    }

    @Override
    public void onCreateContextMenu(@NotNull ContextMenu menu, @NotNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, IDM_OPEN, Menu.NONE, "Изменить");
    }

    @Override
    public boolean onContextItemSelected(@NotNull MenuItem item) {
        //startActivity(new Intent(this, AddingCitiesFragment.class));
        return super.onContextItemSelected(item);
    }

    private void deleteRow(String value) {
        cityDataBaseHelper.getWritableDatabase()
                .delete(CityDataBaseHelper.TABLE_CONTACTS, CityDataBaseHelper.KEY_ID + "=?", new String[]{value});
    }

    public void onDestroyView() {
        super.onDestroyView();
        database.close();
        cityDataBaseHelper.close();
    }
}

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.CityAdd;
import com.example.myapplication.data.CityDataBaseHelper;
import com.example.myapplication.data.VillageAdd;
import com.example.myapplication.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MenuFragment extends Fragment {
    private List<Object> cityAdds = new ArrayList<>();
    private CityDataBaseHelper cityDataBaseHelper;
    private SQLiteDatabase database;
    private static final int IDM_OPEN = 101;
    private View view;
    private int idCity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        cityDataBaseHelper = ((App) getActivity().getApplication()).getCityDataBaseHelper();
        database = cityDataBaseHelper.getReadableDatabase();
        view.findViewById(R.id.add_location).setOnClickListener(v -> {
            ((App) getActivity().getApplication()).getPreferences().putString("", Preferences.Key.EDIT_CITY);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new AddingCitiesFragment())
                    .addToBackStack(MenuFragment.class.getName())
                    .commit();
        });
        Timber.d("onCreateview");
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
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);
        cityAdds.clear();
        while (cursor.moveToNext()) {
            int currentID = cursor.getInt(cursor.getColumnIndex(CityDataBaseHelper.KEY_ID));
            String APP_PREFERENCES_cityName = cursor.getString(cursor.getColumnIndex(CityDataBaseHelper.APP_PREFERENCES_cityName));
            Timber.d(APP_PREFERENCES_cityName);
            if (APP_PREFERENCES_cityName.substring(0, 2).equals("п.")) {
                cityAdds.add(new VillageAdd(APP_PREFERENCES_cityName, ((int) (Math.random() * 10)) + " \u2103", currentID));
            } else {
                cityAdds.add(new CityAdd(APP_PREFERENCES_cityName, ((int) (Math.random() * 10)) + " \u2103", currentID));
            }
        }
        setUpRecyclerView(view.findViewById(R.id.linear_city));
        cursor.close();
    }

    private void setUpRecyclerView(LinearLayout historyLayout) {

        View historyView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_city_list, historyLayout, false);
        RecyclerView recyclerView = historyView.findViewById(R.id.recycler_city_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        CityAddAdapter cityAddAdapter = new CityAddAdapter(cityAdds);
        cityAddAdapter.notifyDataSetChanged();
        cityAddAdapter.setOnClickCity(objects -> {
            if (objects instanceof CityAdd) {
                idCity = ((CityAdd) objects).getCurrentID();
            } else {
                idCity = ((VillageAdd) objects).getCurrentID();
            }
            ((App) getActivity().getApplication()).getPreferences().putString(String.valueOf(idCity), Preferences.Key.CITY);
            getActivity().onBackPressed();
        });
        cityAddAdapter.setOnLongClickCity(objects -> {
            if (objects instanceof CityAdd) {
                idCity = ((CityAdd) objects).getCurrentID();
            } else {
                idCity = ((VillageAdd) objects).getCurrentID();
            }
            ((App) getActivity().getApplication()).getPreferences().putString(String.valueOf(idCity), Preferences.Key.EDIT_CITY);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new AddingCitiesFragment())
                    .addToBackStack(MenuFragment.class.getName())
                    .commit();
        });
//        cityAddAdapter.setOnClickDelButton(object -> {
//            Toast.makeText(getActivity(),"asfsdf",Toast.LENGTH_SHORT).show();
//            if (object instanceof CityAdd) {
//                idCity = ((CityAdd) object).getCurrentID();
//            } else {
//                idCity = ((VillageAdd) object).getCurrentID();
//            }
//            deleteRow(String.valueOf(idCity));
//            cityAddAdapter.removeItem(object);
//        });
        recyclerView.setAdapter(cityAddAdapter);
        historyLayout.addView(historyView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, IDM_OPEN, Menu.NONE, "Изменить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
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

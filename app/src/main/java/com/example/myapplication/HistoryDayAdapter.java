package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.HistoryCity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HistoryDayAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<HistoryCity> historyCityList;

    public HistoryDayAdapter(List<HistoryCity> historyCityList) {
        this.historyCityList = historyCityList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast_for_day, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        HistoryCity historyCity = historyCityList.get(position);
        holder.textViewDay.setText(historyCity.getStringDay());
        holder.imageViewDay.setText(historyCity.getDrawableImageViewDay());
        holder.textViewTemperatureDay.setText(historyCity.getStringTextViewTemperatureDay());
    }

    @Override
    public int getItemCount() {
        return historyCityList.size();
    }
}

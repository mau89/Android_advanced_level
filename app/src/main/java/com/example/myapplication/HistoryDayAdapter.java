package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.HistoryCity;

import java.util.List;


public class HistoryDayAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<HistoryCity> historyCityList;

    public HistoryDayAdapter(List<HistoryCity> historyCityList) {
        this.historyCityList = historyCityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast_for_day, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryCity historyCity = historyCityList.get(position);
        holder.textViewDay.setText(historyCity.getStringDay());
        holder.imageViewDay.setImageResource(historyCity.getDrawableImageViewDay());
        holder.textViewTemperatureDay.setText(historyCity.getStringTextViewTemperatureDay());
    }

    @Override
    public int getItemCount() {
        return historyCityList.size();
    }
}

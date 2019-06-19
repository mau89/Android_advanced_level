package com.example.myapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView textViewDay;
    ImageView imageViewDay;
    TextView textViewTemperatureDay;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewDay = itemView.findViewById(R.id.textViewDay);
        imageViewDay = itemView.findViewById(R.id.imageViewDay);
        textViewTemperatureDay = itemView.findViewById(R.id.textViewTemperatureDay);
    }
}

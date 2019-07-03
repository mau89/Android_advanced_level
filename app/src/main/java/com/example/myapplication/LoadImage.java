package com.example.myapplication;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class LoadImage {

    public LoadImage() {
    }

    public void Load(ImageView imageView, String string) {
        Picasso.get()
                .load(string)
                .into(imageView);
    }
}

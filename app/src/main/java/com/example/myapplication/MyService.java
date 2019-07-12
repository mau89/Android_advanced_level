package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {

    public static final String CITY = "CITY";
    public static final String CURRENTDATE = "CURRENTDATE";
    private String mCity;
    private String mDate;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            mCity = intent.getStringExtra(CITY);
            mDate = intent.getStringExtra(CURRENTDATE);
            if (mCity != null && mDate != null) {
                Toast.makeText(this, "Служба запущена город " + mCity + " день " + mDate, Toast.LENGTH_SHORT).show();
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Служба остановлена", Toast.LENGTH_SHORT).show();
    }
}

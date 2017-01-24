package com.weather.my.weatherapp.activities;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SplashActivity extends AppCompatActivity implements LocationListener {

    private String SPLASH_LOG = "tLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(SPLASH_LOG, "Splash activity onCreate");
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//        DataProvider.loadCurrentWeather(this);
//        DataProvider.initCoordinates(this);
//        LocationDataProvider locationDataProvider = new LocationDataProvider(this);
//        DataProvider.setLocationDataProvider(locationDataProvider);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(SPLASH_LOG, "Location update inside splash activity");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

package com.weather.my.weatherapp.interfaces.listeners;

import android.location.Location;


public interface OnLocationReceivedListener {
    void onLocationReceived(Location location);
    void onLocationReceivedError();
}
package com.weather.my.weatherapp.interfaces.listeners;


public interface OnWeatherReceivedListener {

    void onWeatherReceived(Object weatherData);

    void onWeatherReceivedError();

}

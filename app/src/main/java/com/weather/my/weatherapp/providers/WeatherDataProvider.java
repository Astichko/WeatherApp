package com.weather.my.weatherapp.providers;

import android.location.Location;
import android.util.Log;

import com.weather.my.weatherapp.interfaces.apis.CurrentWeatherApi;
import com.weather.my.weatherapp.interfaces.apis.DaysForecastApi;
import com.weather.my.weatherapp.interfaces.apis.HoursForecastApi;
import com.weather.my.weatherapp.interfaces.listeners.OnWeatherReceivedListener;
import com.weather.my.weatherapp.models.current_weather.CurrentWeather;
import com.weather.my.weatherapp.models.forecast_days.DaysForecast;
import com.weather.my.weatherapp.models.forecast_hours.HoursForecast;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 1 on 21.01.2017.
 */

public class WeatherDataProvider {
    public static final String WDP_LOG = "WDP LOG";
    public static ArrayList<OnWeatherReceivedListener> arrayWeatherListeners;

    public static void getCurrentWeather(Location location) {
        double[] coordinates = new double[2];
        coordinates[0] = location.getLatitude();
        coordinates[1] = location.getLongitude();
        //If internet is available statement
        DataProvider.getApi(CurrentWeatherApi.class).getWeather(String.valueOf(coordinates[0]), String.valueOf(coordinates[1]))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CurrentWeather>() {

                    @Override
                    public void onNext(CurrentWeather currentWeather) {
                        transferWeather(currentWeather);
                    }

                    @Override
                    public void onCompleted() {
                        Log.v(WDP_LOG, "Work completed in current weather!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v(WDP_LOG, e.toString());
                    }
                });
    }

    public static void getDaysForecast(Location location) {
        DataProvider.getApi(DaysForecastApi.class).getDaysForecast(location.getLatitude(), location.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DaysForecast>() {

                    @Override
                    public void onNext(DaysForecast daysForecast) {
                        transferWeather(daysForecast);
                    }

                    @Override
                    public void onCompleted() {
                        Log.v(WDP_LOG, "Work completed in days forecast!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.v(WDP_LOG, e.toString());
                    }
                });
    }

    public static void getHoursForecast(Location location) {
        DataProvider.getApi(HoursForecastApi.class).getHoursForecast(location.getLatitude(), location.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HoursForecast>() {

                    @Override
                    public void onNext(HoursForecast hoursForecast) {
                        transferWeather(hoursForecast);
                    }

                    @Override
                    public void onCompleted() {
                        Log.v(WDP_LOG, "Work completed in hours forecast!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.v(WDP_LOG, e.toString());
                    }
                });
    }

    public static void setListener(OnWeatherReceivedListener weatherListener) {
        if (arrayWeatherListeners == null) {
            arrayWeatherListeners = new ArrayList<>();
        }
        arrayWeatherListeners.add(weatherListener);
    }

    public static void transferWeather(Object weatherData) {
        for (int i = 0; i < arrayWeatherListeners.size(); i++) {
            arrayWeatherListeners.get(0).onWeatherReceived(weatherData);
        }
    }

}

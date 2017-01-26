package com.weather.my.weatherapp.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weather.my.weatherapp.models.current_weather.CurrentWeather;
import com.weather.my.weatherapp.models.forecast_days.DaysForecast;
import com.weather.my.weatherapp.models.forecast_hours.HoursForecast;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;


public class DataProvider {

    public static Retrofit retrofit;
    public static CurrentWeather currentWeather;
    public static HoursForecast hoursForecast;
    public static DaysForecast daysForecast;
    public static LocationDataProvider locationDataProvider;
    final public static String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    public static DaysForecast getDaysForecast() {
        return daysForecast;
    }

    public static void setDaysForecast(DaysForecast daysForecast) {
        DataProvider.daysForecast = daysForecast;
    }

    public static void setCurrentWeather(CurrentWeather currentWeather) {
        DataProvider.currentWeather = currentWeather;
    }

    public static CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public static HoursForecast getHoursForecast() {
        return hoursForecast;
    }

    public static void setHoursForecast(HoursForecast hoursForecast) {
        DataProvider.hoursForecast = hoursForecast;
    }

    public static <T> T getApi(Class<T> type) {
        if (retrofit == null) {
            RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
            Gson gson = new GsonBuilder().create();

            retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(rxAdapter)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BASE_URL)
                    .build();
        }

        return retrofit.create(type);
    }

    public static LocationDataProvider getLocationDataProvider() {
        return locationDataProvider;
    }

    public static void setLocationDataProvider(LocationDataProvider ld) {
        locationDataProvider = ld;
    }

}

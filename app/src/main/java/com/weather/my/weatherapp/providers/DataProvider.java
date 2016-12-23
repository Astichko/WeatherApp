package com.weather.my.weatherapp.providers;

import com.weather.my.weatherapp.apis.WeatherApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by BOSS on 22.12.2016.
 */

public class DataProvider {
    final public static String BASE_URL = "http://api.openweathermap.org/data/2.5/" ;
    public static WeatherApi weatherApi;

    public static WeatherApi getWeatherApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        return retrofit.create(WeatherApi.class);
    }


}

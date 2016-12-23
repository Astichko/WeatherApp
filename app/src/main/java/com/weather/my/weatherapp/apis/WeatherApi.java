package com.weather.my.weatherapp.apis;

import com.weather.my.weatherapp.models.DataWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by BOSS on 22.12.2016.
 */

public interface WeatherApi {
    @GET("weather?&APPID=aa3466d7a5d97ccaf20b12a8ed360ef1&units=metric")
    Call<DataWeather> getWeather(@Query("q") String q);
}

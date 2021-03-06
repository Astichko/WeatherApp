package com.weather.my.weatherapp.interfaces.apis;

import com.weather.my.weatherapp.models.forecast_hours.HoursForecast;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public interface HoursForecastApi {
    @GET("forecast?&APPID=ccbd85ee13cc280628597847b5e9615f&units=metric")//ccbd85ee13cc280628597847b5e9615f||aa3466d7a5d97ccaf20b12a8ed360ef1
    Observable<HoursForecast> getHoursForecast(@Query("lat") double lat, @Query("lon") double lon);
}

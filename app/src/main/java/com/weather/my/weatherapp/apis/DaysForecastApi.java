package com.weather.my.weatherapp.apis;

import com.weather.my.weatherapp.models.forecast_days.DaysForecast;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by 1 on 18.01.2017.
 */

public interface DaysForecastApi {
    @GET("forecast/daily?&APPID=ccbd85ee13cc280628597847b5e9615f&units=metric")//ccbd85ee13cc280628597847b5e9615f||aa3466d7a5d97ccaf20b12a8ed360ef1
    Observable<DaysForecast> getDaysForecast(@Query("lat") double lat, @Query("lon") double lon);



}

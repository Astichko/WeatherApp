package com.weather.my.weatherapp.interfaces.apis;

import com.weather.my.weatherapp.models.current_weather.CurrentWeather;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public interface CurrentWeatherApi {
    @GET("weather?&APPID=ccbd85ee13cc280628597847b5e9615f&units=metric")//ccbd85ee13cc280628597847b5e9615f||aa3466d7a5d97ccaf20b12a8ed360ef1
    Observable<CurrentWeather> getWeather(@Query("lat") String lat, @Query("lon") String lon);

}

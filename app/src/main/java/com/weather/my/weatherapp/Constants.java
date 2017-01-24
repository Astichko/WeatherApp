package com.weather.my.weatherapp;

/**
 * Created by 1 on 03.01.2017.
 */

public class Constants {
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public static final int HEADER = 0;
    public static final int FORECAST = 1;
    public static final int WIND = 2;
    public static final int DETAILS = 3;
    public static final int MAP = 4;
    public static final int NUM_OF_DAY_FORECASTS = 8;
    public static final String RESOURCE_PREFIX = "wi_owm_";
    public static final String RESOURCE_DAY_PREFIX = "wi_owm_day_";
    public static final String RESOURCE_NIGHT_PREFIX = "wi_owm_night_";
    public static final int REQUEST_PERMISSION_GPS = 1334;
    public static final int ONE_SEC = 1000;
    public static final int WIND_CONSTANT = 40;
    public static final int MAX_TRANSPARENCY = 255;
}

package com.weather.my.weatherapp.providers;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weather.my.weatherapp.models.current_weather.CurrentWeather;
import com.weather.my.weatherapp.models.forecast_days.DaysForecast;
import com.weather.my.weatherapp.models.forecast_hours.HoursForecast;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

//import android.location.LocationListener;

/**
 * Created by BOSS on 22.12.2016.
 */

public class DataProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    final public static String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static Retrofit retrofit;
    public static CurrentWeather currentWeather;
    public static HoursForecast hoursForecast;
    public static DaysForecast daysForecast;

    public static DaysForecast getDaysForecast() {
        return daysForecast;
    }

    public static void setDaysForecast(DaysForecast daysForecast) {
        DataProvider.daysForecast = daysForecast;
    }

    public static void setCurrentWeather(CurrentWeather currentWeather) {
        DataProvider.currentWeather = currentWeather;
    }

    public static HoursForecast getHoursForecast() {
        return hoursForecast;
    }

    public static void setHoursForecast(HoursForecast hoursForecast) {
        DataProvider.hoursForecast = hoursForecast;
    }

    public static boolean isGPSEnabled = false;
    public static boolean isNetworkEnabled = false;
    public static double[] coordinates;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static LocationManager locationManager;
    private static String DATA_PROVIDER_LOG = "tLog";
    private Context context;
    public static LocationDataProvider locationDataProvider;


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

    public void createGoogleAPiClientAndConnect(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    public GoogleApiClient getmGoogleApiClient;

    public static CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

//    public static void loadCurrentWeather(Context context) {
//        coordinates = DataProvider.getCoordinates(context);
//        if (coordinates == null) {
//            return;
//        }
//        getApi(CurrentWeatherApi.class).getWeather(String.valueOf(coordinates[0]), String.valueOf(coordinates[1]))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<CurrentWeather>() {
//
//                    @Override
//                    public void onNext(CurrentWeather wd) {
//                        currentWeather = wd;
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        Log.v("mLogs", "Work completed in current weather!");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.v("mLogs", e.toString());
//                    }
//                });
//    }
////
//    public static double[] getCoordinates(Context context) {
//        if (coordinates != null) {
//            Log.v(DATA_PROVIDER_LOG, "Set coordinate from scope variable");
//            return coordinates;
//        }
//        double[] coordinates = new double[2];
//        locationManager = (LocationManager) context
//                .getSystemService(LOCATION_SERVICE);
//        // getting GPS status
//        isGPSEnabled = locationManager
//                .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//        // getting network status
////        isNetworkEnabled = locationManager
////                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            coordinates[0] = 51.5073509;
//            coordinates[1] = -0.1277583;
//            Log.v(DATA_PROVIDER_LOG, "Set LONDON coordinate");
//            return coordinates;
//        }
//
////        if (isNetworkEnabled) {
////            Toast.makeText(context, "No Internet Connections", Toast.LENGTH_LONG).show();
////            return null;
////        } else {
////            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BW_UPDATES, (LocationListener) context);
////            Location location = locationManager
////                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
////            coordinates[0] = location.getLatitude();
////            coordinates[1] = location.getLongitude();
////        }
//
//        if (!isGPSEnabled) {
//            Toast.makeText(context, "Unable to determine GPS location", Toast.LENGTH_LONG).show();
//            return null;
//        } else {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BW_UPDATES, (LocationListener) context);
//            Location location = locationManager
//                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (location != null) {
//                Log.v(DATA_PROVIDER_LOG, "Set last know position in getCoordinateMethod");
//                coordinates[0] = location.getLatitude();
//                coordinates[1] = location.getLongitude();
//            }
//        }
//
//
//        return coordinates;
//    }
//
//    public static void initCoordinates(Context context) {
//        Log.v(DATA_PROVIDER_LOG, "Start init coordinates");
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//        }
//        if (locationManager == null) {
//            return;
//        }
//        locationManager = (LocationManager) context
//                .getSystemService(LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BW_UPDATES, (LocationListener) context);
//    }


    @Override
    public void onLocationChanged(Location location) {
        Log.v(DATA_PROVIDER_LOG, "Location update received");
        coordinates[0] = location.getLatitude();
        coordinates[1] = location.getLongitude();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(DATA_PROVIDER_LOG, "Location services connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(DATA_PROVIDER_LOG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

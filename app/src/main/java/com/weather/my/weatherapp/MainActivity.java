package com.weather.my.weatherapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.weather.my.weatherapp.models.DataWeather;
import com.weather.my.weatherapp.providers.DataProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    final private String MAIN_ACT_TAG = "mainLog";
    TextView columnRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        columnRight = (TextView) findViewById(R.id.columnRight);
        Call<DataWeather> weatherCall = DataProvider.getWeatherApi().getWeather("Kirovograd");
        weatherCall.enqueue(new Callback<DataWeather>() {
            @Override
            public void onResponse(Call<DataWeather> call, Response<DataWeather> response) {
                String result = String.valueOf(response.body().getClouds().getAll());
                Log.v(MAIN_ACT_TAG, result);
                columnRight.setText(result);
            }

            @Override
            public void onFailure(Call<DataWeather> call, Throwable t) {
                Log.v(MAIN_ACT_TAG, "Failure on get json");
                columnRight.setText("Failure on get json");
            }
        });
    }
}

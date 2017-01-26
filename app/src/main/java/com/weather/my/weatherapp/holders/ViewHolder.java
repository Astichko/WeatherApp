package com.weather.my.weatherapp.holders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.my.weatherapp.R;

import java.util.ArrayList;

import static com.weather.my.weatherapp.utils.Constants.FORECAST;
import static com.weather.my.weatherapp.utils.Constants.HEADER;
import static com.weather.my.weatherapp.utils.Constants.MAP;
import static com.weather.my.weatherapp.utils.Constants.WIND;


public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView temp;
    public TextView maxTemp;
    public TextView minTemp;
    public TextView location;
    public RecyclerView hoursForecastRecycler;
    public ImageView windPropellers;
    public ArrayList<TextView[]> forecastDayData;
    public ImageView windDirections;
    public TextView windSpeed;
    public TextView pressure;
    public ConstraintLayout itemHeaderContainer;
    public WebView webView;

    public ViewHolder(View itemView, int viewType) {
        super(itemView);
        findViews(viewType);
    }

    public void findViews(int viewType) {
        if (viewType == HEADER) {
            itemHeaderContainer = (ConstraintLayout) itemView.findViewById(R.id.main_layout);
            temp = (TextView) itemView.findViewById(R.id.temperature);
            maxTemp = (TextView) itemView.findViewById(R.id.maxTemp);
            minTemp = (TextView) itemView.findViewById(R.id.minTemp);
            location = (TextView) itemView.findViewById(R.id.location);
        }
        if (viewType == WIND) {
            windPropellers = (ImageView) itemView.findViewById(R.id.wind_propellers);
            windDirections = (ImageView) itemView.findViewById(R.id.wind_direction);
            windSpeed = (TextView) itemView.findViewById(R.id.wind_speed);
            pressure = (TextView) itemView.findViewById(R.id.pressure);
        }
        if (viewType == MAP) {
            webView = (WebView) itemView.findViewById(R.id.web_view);
        }
        if (viewType == FORECAST) {
            View frame = itemView.findViewById(R.id.element1);
            if (frame != null) {
                findForecastViews(R.id.element1, R.id.element2, R.id.element3, R.id.element4, R.id.element5);
                hoursForecastRecycler = (RecyclerView) itemView.findViewById(R.id.hours_forecast_recycler);
            }
        }
    }

    public void findForecastViews(int... ids) {
        forecastDayData = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            View frame = itemView.findViewById(ids[i]);
            TextView[] textViews = new TextView[3];
            textViews[0] = (TextView) frame.findViewById(R.id.condition);
            textViews[1] = (TextView) frame.findViewById(R.id.temp);
            textViews[2] = (TextView) frame.findViewById(R.id.day);
            forecastDayData.add(textViews);
        }
    }
}

package com.weather.my.weatherapp.holders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.weather.my.weatherapp.R;

import java.util.ArrayList;


/**
 * Created by 1 on 05.01.2017.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView temp;
    public TextView maxTemp;
    public TextView minTemp;
    public TextView location;
    public RecyclerView hoursForecastRecycler;
    public TextView dayOfWeek;
    public TextView forecastCondition;
    public TextView forecastTemp;
    public ImageView windPropellers;
    public ArrayList<TextView[]> forecastDayData;
    public ImageView windDirections;
    public TextView windSpeed;
    public MapView mapView;
    public TextView pressure;
    public ConstraintLayout itemHeaderContainer;
    public WebView webView;


    public ViewHolder(View itemView) {
        super(itemView);
        temp = (TextView) itemView.findViewById(R.id.temperature);
        maxTemp = (TextView) itemView.findViewById(R.id.maxTemp);
        minTemp = (TextView) itemView.findViewById(R.id.minTemp);
        location = (TextView) itemView.findViewById(R.id.location);
        windPropellers = (ImageView) itemView.findViewById(R.id.wind_propellers);
        windDirections = (ImageView) itemView.findViewById(R.id.wind_direction);
        windSpeed = (TextView) itemView.findViewById(R.id.wind_speed);
//        mapView = (MapView) itemView.findViewById(R.id.map_view);
        pressure = (TextView) itemView.findViewById(R.id.pressure);
        itemHeaderContainer = (ConstraintLayout) itemView.findViewById(R.id.item_header);
        webView = (WebView) itemView.findViewById(R.id.web_view);
        View frame = itemView.findViewById(R.id.element1);
        if (frame != null) {
            findForecastViews(R.id.element1, R.id.element2, R.id.element3, R.id.element4, R.id.element5);
            hoursForecastRecycler = (RecyclerView) itemView.findViewById(R.id.hours_forecast_recycler);
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

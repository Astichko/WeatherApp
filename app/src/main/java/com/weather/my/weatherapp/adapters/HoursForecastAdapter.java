package com.weather.my.weatherapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weather.my.weatherapp.R;
import com.weather.my.weatherapp.holders.HoursForecastHolder;
import com.weather.my.weatherapp.models.forecast_hours.HoursForecast;
import com.weather.my.weatherapp.providers.DataProvider;

import static com.weather.my.weatherapp.Constants.RESOURCE_PREFIX;

/**
 * Created by 1 on 18.01.2017.
 */

public class HoursForecastAdapter extends RecyclerView.Adapter<HoursForecastHolder> {

    public Context context;
    public HoursForecast hoursForecast;
    private String LOG = "hoursRecycler";

    public HoursForecastAdapter(Context context) {
        Log.v(LOG, "HoursForecastAdapter is created");
        this.context = context;
    }

    @Override
    public HoursForecastHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forecast_hours, parent, false);
        return new HoursForecastHolder(view);
    }

    @Override
    public void onBindViewHolder(HoursForecastHolder holder, int position) {//095834190 Max
        hoursForecast = DataProvider.getHoursForecast();
        String dateTime = hoursForecast.getList().get(position).getDtTxt();
        Log.v(LOG, "HoursForecastAdapter day time is :" + dateTime);
        Log.v(LOG, "HoursForecastAdapter formatted day time is :" + dateTime.substring(dateTime.length() - 7, dateTime.length() - 3));
        holder.time.setText(dateTime.substring(dateTime.length() - 8, dateTime.length() - 3));

        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
//
        holder.hoursCondition.setTypeface(weatherFont);
        holder.hoursCondition.setText(getStringResourceByName(RESOURCE_PREFIX
                + hoursForecast.getList().get(position).getWeather().get(0).getId()));
//        holder.hoursCondition.setText(getStringResourceByName("wi_owm_800"));
////        holder.hoursCondition.setText(getStringResourceByName(RESOURCE_PREFIX
////                + hoursForecast.getList().get(position).getWeather().get(0).getId()));
        holder.hoursTemp.setText(String.valueOf(hoursForecast.getList().get(position).getMain().getTemp().intValue())+ "\u00B0");

    }

    @Override
    public int getItemCount() {
        return 9;
    }

    private String getStringResourceByName(String aString) {//wi_owm_day_
        String packageName = context.getPackageName();
        Log.v(LOG, "Package name is: " + packageName);
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }
}

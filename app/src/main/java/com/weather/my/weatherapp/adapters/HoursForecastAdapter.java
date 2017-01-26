package com.weather.my.weatherapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weather.my.weatherapp.R;
import com.weather.my.weatherapp.holders.HoursForecastHolder;
import com.weather.my.weatherapp.models.forecast_hours.HoursForecast;
import com.weather.my.weatherapp.providers.DataProvider;

import static com.weather.my.weatherapp.utils.Constants.DEG;
import static com.weather.my.weatherapp.utils.Constants.FONT_PATH;
import static com.weather.my.weatherapp.utils.Constants.RESOURCE_PREFIX;


public class HoursForecastAdapter extends RecyclerView.Adapter<HoursForecastHolder> {

    public Context context;
    public HoursForecast hoursForecast;
    private String LOG = "hoursRecycler";

    public HoursForecastAdapter(Context context) {
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
        holder.time.setText(dateTime.substring(dateTime.length() - 8, dateTime.length() - 3));
        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), FONT_PATH);
        holder.hoursCondition.setTypeface(weatherFont);
        holder.hoursCondition.setText(getStringResourceByName(RESOURCE_PREFIX
                + hoursForecast.getList().get(position).getWeather().get(0).getId()));
        holder.hoursTemp.setText(String.valueOf(hoursForecast.getList().get(position).getMain().getTemp().intValue()) + DEG);

    }

    @Override
    public int getItemCount() {
        return 9;
    }

    private String getStringResourceByName(String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }
}

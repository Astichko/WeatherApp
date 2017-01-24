package com.weather.my.weatherapp.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.weather.my.weatherapp.R;

/**
 * Created by 1 on 19.01.2017.
 */

public class HoursForecastHolder extends RecyclerView.ViewHolder {
    public TextView hoursCondition;
    public TextView time;
    public TextView hoursTemp;

    public HoursForecastHolder(View itemView) {
        super(itemView);
        hoursCondition = (TextView) itemView.findViewById(R.id.hours_condition);
        time = (TextView) itemView.findViewById(R.id.time);
        hoursTemp = (TextView) itemView.findViewById(R.id.hours_temp);
    }
}

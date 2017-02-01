package com.weather.my.weatherapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.weather.my.weatherapp.R;
import com.weather.my.weatherapp.holders.ViewHolder;
import com.weather.my.weatherapp.models.current_weather.CurrentWeather;
import com.weather.my.weatherapp.models.forecast_days.DaysForecast;
import com.weather.my.weatherapp.providers.DataProvider;
import com.weather.my.weatherapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.weather.my.weatherapp.utils.Constants.DEG;
import static com.weather.my.weatherapp.utils.Constants.FONT_PATH;
import static com.weather.my.weatherapp.utils.Constants.FORECAST;
import static com.weather.my.weatherapp.utils.Constants.HEADER;
import static com.weather.my.weatherapp.utils.Constants.MAP;
import static com.weather.my.weatherapp.utils.Constants.ONE_SEC;
import static com.weather.my.weatherapp.utils.Constants.RESOURCE_PREFIX;
import static com.weather.my.weatherapp.utils.Constants.WIND;
import static com.weather.my.weatherapp.utils.Constants.WIND_CONSTANT;


public class MainRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final Context context;
    private CurrentWeather currentWeather;
    private DaysForecast daysForecast;
    private HoursForecastAdapter hoursForecastAdapter;
    private final String LOG = "mRecycler";
    private boolean isAnimationRun = false;

    public MainRecyclerAdapter(Context context) {
        this.context = context;
        currentWeather = DataProvider.getCurrentWeather();
        daysForecast = DataProvider.getDaysForecast();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else if (position == 1) {
            return FORECAST;
        } else if (position == 2) {
            return MAP;
        } else {
            return WIND;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case HEADER:
                view = LayoutInflater.from(context).inflate(R.layout.item_header, parent, false);
                break;
            case FORECAST:
                view = LayoutInflater.from(context).inflate(R.layout.item_forecast, parent, false);
                break;
            case WIND:
                view = LayoutInflater.from(context).inflate(R.layout.item_wind_pressure, parent, false);
                break;
            case MAP:
                view = LayoutInflater.from(context).inflate(R.layout.item_map, parent, false);
                break;
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        currentWeather = DataProvider.getCurrentWeather();
        daysForecast = DataProvider.getDaysForecast();
        switch (holder.getItemViewType()) {
            case HEADER:
                createHeader(holder);
                break;
            case FORECAST:
                createForecast(holder);
                break;
            case WIND:
                createWind(holder);
                break;
            case MAP:
                createMap(holder);
                break;
        }
    }

    private void createMap(ViewHolder holder) {
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                DataProvider.getCurrentWeather().getSys().getCountry()
                + "&zoom=4&size=400x200&maptype=roadmap" +
                "&key=AIzaSyDiZ5o7No2YVWH7plEejs8YU6joGQXrnac";
        holder.webView.loadUrl(url);
    }

    private void createWind(ViewHolder holder) {
        if (currentWeather == null) {
            return;
        }
        int windSpeed = DataProvider.getCurrentWeather().getWind().getSpeed().intValue();
        if (windSpeed != 0) {//No animation is set - propellers not moving
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.rotate);
            anim.setDuration((WIND_CONSTANT / (windSpeed) * ONE_SEC));
            ViewCompat.setHasTransientState(holder.windPropellers, true);
            holder.windPropellers.setAnimation(anim);
        }
        holder.windDirections.setRotation((float) currentWeather.getWind().getDeg().intValue());
        holder.windSpeed.setText(windSpeed + Utils.getStringResources(context, R.string.km_h));
        holder.pressure.setText(currentWeather.getMain().getPressure().intValue()
                + Utils.getStringResources(context, R.string.h_pa));
    }

    private void createHeader(ViewHolder holder) {
        if (!isAnimationRun) {
            setAnimation(holder);
            isAnimationRun = true;
        }
        if (currentWeather == null) {
            return;
        }
        int temp = currentWeather.getMain().getTemp().intValue();
        int max = currentWeather.getMain().getTempMax().intValue();
        int min = currentWeather.getMain().getTempMin().intValue();
        holder.location.setText(String.valueOf(currentWeather.getName()));
        holder.temp.setText(String.valueOf(temp) + DEG);//Just degree \u00B0
        holder.maxTemp.setText(String.valueOf(max) + DEG);
        holder.minTemp.setText(String.valueOf(min) + DEG);
    }

    private void createForecast(ViewHolder holder) {
        setUpForecast(holder);
        createHoursRecycler(holder);
    }

    private void setAnimation(ViewHolder holder) {
        Log.v(LOG, "Set header Animation in Recycler");
        Utils.setVisibility(View.INVISIBLE, holder.itemHeaderContainer);
        Animation mSlideInBottom = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
        holder.itemHeaderContainer.startAnimation(mSlideInBottom);
        Utils.setVisibility(View.VISIBLE, holder.itemHeaderContainer);
    }

    private void createHoursRecycler(ViewHolder holder) {
        if (hoursForecastAdapter == null) {
            hoursForecastAdapter = new HoursForecastAdapter(context);
        }
        holder.hoursForecastRecycler.setAdapter(hoursForecastAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.hoursForecastRecycler.setLayoutManager(linearLayoutManager);
    }

    private void setUpForecast(ViewHolder holder) {
        TextView forecastCondition;
        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), FONT_PATH);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("EEEE", Locale.US);
        for (int i = 0; i < holder.forecastDayData.size(); i++) {
            forecastCondition = holder.forecastDayData.get(i)[0];
            forecastCondition.setTypeface(weatherFont);
            forecastCondition.setText(getStringResourceByName(RESOURCE_PREFIX + daysForecast.getList().get(i).getWeather().get(0).getId()));//daysForecast.getList().get(0).getWeather().get(0).getId()
            holder.forecastDayData.get(i)[1]
                    .setText(String.valueOf(daysForecast.getList().get(i).getTemp().getEve().intValue()) + DEG);
            holder.forecastDayData.get(i)[2].setText(f.format(c.getTime()));
            c.add(Calendar.DATE, 1);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    private String getStringResourceByName(String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }
}

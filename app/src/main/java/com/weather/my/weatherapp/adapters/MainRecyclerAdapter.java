package com.weather.my.weatherapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.weather.my.weatherapp.R;
import com.weather.my.weatherapp.holders.ViewHolder;
import com.weather.my.weatherapp.models.current_weather.CurrentWeather;
import com.weather.my.weatherapp.models.forecast_days.DaysForecast;
import com.weather.my.weatherapp.providers.DataProvider;
import com.weather.my.weatherapp.utils.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.weather.my.weatherapp.Constants.FORECAST;
import static com.weather.my.weatherapp.Constants.HEADER;
import static com.weather.my.weatherapp.Constants.MAP;
import static com.weather.my.weatherapp.Constants.ONE_SEC;
import static com.weather.my.weatherapp.Constants.RESOURCE_PREFIX;
import static com.weather.my.weatherapp.Constants.WIND;
import static com.weather.my.weatherapp.Constants.WIND_CONSTANT;

/**
 * Created by 1 on 05.01.2017.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final Context context;
    private CurrentWeather weatherData;
    private DaysForecast daysForecast;
    private HoursForecastAdapter hoursForecastAdapter;
    private boolean isAnimationSet = false;
    private FragmentManager fragmentManager;
    private final String LOG = "Recycler";
    private boolean isAnimationRun = false;

    public MainRecyclerAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        weatherData = DataProvider.getCurrentWeather();
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        weatherData = DataProvider.getCurrentWeather();
        daysForecast = DataProvider.getDaysForecast();
        switch (holder.getItemViewType()) {
            case HEADER:
                if (!isAnimationRun) {
                    setAnimation(holder);
                    isAnimationRun = true;
                }
                Utils.setVisibility(View.VISIBLE, holder.itemHeaderContainer);
                if (weatherData == null) {
                    return;
                }
                Log.v("recycler", "On bind header weather");
                int temp = weatherData.getMain().getTemp().intValue();
                int max = weatherData.getMain().getTempMax().intValue();
                int min = weatherData.getMain().getTempMin().intValue();
                holder.location.setText(String.valueOf(weatherData.getName()));
                holder.temp.setText(String.valueOf(temp) + "\u00B0");//Just degree \u00B0
                holder.maxTemp.setText(String.valueOf(max) + "\u00B0");
                holder.minTemp.setText(String.valueOf(min) + "\u00B0");
                break;
            case FORECAST:
                if (daysForecast == null) {
                    Log.v("recycler", "No hours forecast weather");
                    return;
                }
                setUpForecast(holder);
                createHoursRecycler(holder);
                break;
            case WIND:
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.rotate);
                int windSpeed = DataProvider.getCurrentWeather().getWind().getSpeed().intValue();
                anim.setDuration((WIND_CONSTANT / windSpeed) * ONE_SEC);
//                if (Build.VERSION.SDK_INT >= 16) {
//                    holder.windPropellers.setHasTransientState(true);
//                }
                ViewCompat.setHasTransientState(holder.windPropellers, true);
                holder.windPropellers.setAnimation(anim);
                holder.windDirections.setRotation((float) DataProvider.getCurrentWeather().getWind().getDeg().intValue());
                holder.windSpeed.setText(windSpeed + " km/h");
                holder.pressure.setText(DataProvider.getCurrentWeather().getMain().getPressure().intValue() + " hPa");
                break;
            case MAP:
                String url = "https://maps.googleapis.com/maps/api/staticmap?center="+
                        DataProvider.getCurrentWeather().getSys().getCountry()
                        +"&zoom=4&size=400x200&maptype=roadmap" +
                        "&key=AIzaSyDiZ5o7No2YVWH7plEejs8YU6joGQXrnac";
                holder.webView.loadUrl(url);
//                holder.mapView.onCreate(null);
//                holder.mapView.getMapAsync(googleMap -> {
//                    Location location = DataProvider.getLocationDataProvider().location;
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 3));
////                    googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(createTileProvider("pressure")).transparency(0.5f));
//                });
//                holder.mapView.setOnClickListener(v -> {
//                    context.startActivity(new Intent(context, MapsActivity.class));
//                });
//                // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
//                MapsInitializer.initialize(context);
//                holder.mapView.onResume();
////                MapsFragment fragment = new MapsFragment();
////                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                fragmentTransaction
////                        .replace(R.id.frame_map_container, fragment)
////                        .addToBackStack(null)
////                        .commit();
//                //Do something
                break;
        }
    }

    private void setAnimation(ViewHolder holder) {
        Transition recyclerAnimation = new Slide(Gravity.BOTTOM);
        recyclerAnimation.setDuration(2000);
        TransitionManager.beginDelayedTransition(holder.itemHeaderContainer, recyclerAnimation);
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

        Log.v("recycler", "On bind forecast weather");
        TextView forecastCondition;
        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("EEEE", Locale.US);
        String dayOfWeek = f.format(c.getTime());
        Log.v("mLogs", "Current day is: " + dayOfWeek);
        for (int i = 0; i < holder.forecastDayData.size(); i++) {
            forecastCondition = holder.forecastDayData.get(i)[0];
            forecastCondition.setTypeface(weatherFont);
            forecastCondition.setText(getStringResourceByName(RESOURCE_PREFIX + daysForecast.getList().get(i).getWeather().get(0).getId()));//daysForecast.getList().get(0).getWeather().get(0).getId()
            holder.forecastDayData.get(i)[1]
                    .setText(String.valueOf(daysForecast.getList().get(i).getTemp().getEve().intValue()) + "\u00B0");
            holder.forecastDayData.get(i)[2].setText(f.format(c.getTime()));
            c.add(Calendar.DATE, 1);
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }


    public void setWeatherIcon(TextView wIcon, int actualId, long sunrise, long sunset) {
        String id;
        long currentTime = new Date().getTime();
        if (currentTime >= sunrise && currentTime < sunset) {
            id = "wi_owm_day_";
        } else {
            id = "wi_owm_night_";
        }

        wIcon.setText(getStringResourceByName(id + actualId));
    }


    private String getStringResourceByName(String aString) {//wi_owm_day_
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }

    private TileProvider createTileProvider(String tileType) {
        Log.v(LOG, "Tile initialisations begin");
//        final String OWM_TILE_URL = "http://openweathermap.org/help/tiles.html?/temperature/48/32/5.png";
        final String OWM_TILE_URL = "http://maps2.aerisapi.com/YtzDrNudB7nwvfBHOK1Xi_xqFLY0ynBv9RIuxXKXMn3WIYvl6Z2XsfSdKwofnc/radar/5/41/23/current.png";
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String fUrl = OWM_TILE_URL;
                URL url = null;
                try {
                    url = new URL(fUrl);
                    Toast.makeText(context, "Tile created successfully", Toast.LENGTH_SHORT).show();
                } catch (MalformedURLException mfe) {
                    Log.v(LOG, "Tile initialisations begin");
                    Toast.makeText(context, "Some url problem", Toast.LENGTH_LONG).show();
                    mfe.printStackTrace();
                }
                return url;
            }
        };
        return tileProvider;
    }
}

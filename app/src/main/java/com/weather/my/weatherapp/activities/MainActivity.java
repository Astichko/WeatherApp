package com.weather.my.weatherapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.weather.my.weatherapp.R;
import com.weather.my.weatherapp.adapters.MainRecyclerAdapter;
import com.weather.my.weatherapp.interfaces.listeners.OnLocationReceivedListener;
import com.weather.my.weatherapp.interfaces.listeners.OnWeatherReceivedListener;
import com.weather.my.weatherapp.models.current_weather.CurrentWeather;
import com.weather.my.weatherapp.models.forecast_days.DaysForecast;
import com.weather.my.weatherapp.models.forecast_hours.HoursForecast;
import com.weather.my.weatherapp.providers.DataProvider;
import com.weather.my.weatherapp.providers.LocationDataProvider;
import com.weather.my.weatherapp.providers.WeatherDataProvider;
import com.weather.my.weatherapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.weather.my.weatherapp.utils.Constants.ANIMATION_DURATION;
import static com.weather.my.weatherapp.utils.Constants.FONT_PATH;
import static com.weather.my.weatherapp.utils.Constants.MAX_TRANSPARENCY;
import static com.weather.my.weatherapp.utils.Constants.REQUEST_PERMISSION_GPS;
import static com.weather.my.weatherapp.utils.Constants.RESOURCE_DAY_PREFIX;
import static com.weather.my.weatherapp.utils.Constants.RESOURCE_NIGHT_PREFIX;

public class MainActivity extends AppCompatActivity implements
        OnLocationReceivedListener, OnWeatherReceivedListener {

    private TextView wIcon;
    private TextView condition;
    private RecyclerView recyclerView;
    private ImageView backgroundPhoto;
    private ImageView backgroundBlurPhoto;
    private ConstraintLayout mainContainer;
    private MainRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Location lastKnownLocation;
    private View allowAccessBlock;
    private View enterLocationBlock;
    private LocationDataProvider locationDataProvider;
    private boolean[] typeWeatherReceived = new boolean[3];
    private boolean isFirstTime = true;
    private float offsetIcon = 0;
    private float offsetCondition = 0;
    final private String LOG = "mLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        if (checkForPermissions()) {
            onCreate();
        }
    }

    public void onCreate() {
        createLocationProvider();
        findViews();
        intiViews();
    }

    public boolean checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_GPS);
            return false;
        }
        return true;
    }

    public void intiViews() {
        WeatherDataProvider.setListener(this);
        backgroundBlurPhoto.setAlpha(0);
        backgroundBlurPhoto.setVisibility(View.VISIBLE);
        Typeface weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), FONT_PATH);
        wIcon.setTypeface(weatherFont);
        setSwipeRefreshLayout();
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateWeather(lastKnownLocation);
        });
        swipeRefreshLayout.setProgressViewOffset(false, 0, 50);
    }

    public void createLocationProvider() {
        locationDataProvider = new LocationDataProvider(this);
        DataProvider.setLocationDataProvider(locationDataProvider);
        locationDataProvider.setListener(this);
        locationDataProvider.getGoogleApiClient().connect();
    }

    public void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("EEEE dd.MM.yyyy", Locale.US);
        String currentDate = f.format(c.getTime());
        title.setText(currentDate);
        setSupportActionBar(toolbar);
    }

    public void findViews() {
        wIcon = (TextView) findViewById(R.id.weatherIcon);
        condition = (TextView) findViewById(R.id.condition);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        backgroundPhoto = (ImageView) findViewById(R.id.background_photo);
        backgroundBlurPhoto = (ImageView) findViewById(R.id.background_photo_blur);
        mainContainer = (ConstraintLayout) findViewById(R.id.main_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    }


    public void fillRecycler() {
        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler);
        }
        if (adapter == null) {
            adapter = new MainRecyclerAdapter(getApplicationContext(), getSupportFragmentManager());
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            setScrollListener();
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    public void setScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                moveViews();
            }
        });
    }

    private void moveViews() {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            int transparency = Math.abs(holder.itemView.getTop());
            if (transparency > MAX_TRANSPARENCY) {
                transparency = MAX_TRANSPARENCY;
            }
            backgroundPhoto.setTop(recyclerView.computeVerticalScrollOffset() / 10);
            backgroundBlurPhoto.setTop(recyclerView.computeVerticalScrollOffset() / 10);
            backgroundBlurPhoto.setAlpha(transparency);
            if (wIcon.getX() != 0) {
                if (isFirstTime) {
                    offsetIcon = wIcon.getX();
                    offsetCondition = condition.getX();
                    isFirstTime = false;
                }
            }
            if (offsetIcon != 0) {
                wIcon.setX(offsetIcon + recyclerView.computeVerticalScrollOffset());
                condition.setX(offsetCondition + recyclerView.computeVerticalScrollOffset());
            }
        }
    }

    public void setWeatherIcon(int actualId, long sunrise, long sunset) {
        String id;
        long currentTime = new Date().getTime();
        if (currentTime >= sunrise && currentTime < sunset) {
            id = RESOURCE_DAY_PREFIX;
        } else {
            id = RESOURCE_NIGHT_PREFIX;
        }
        wIcon.setText(getStringResourceByName(id + actualId));
    }

    private String getStringResourceByName(String aString) {
        int resId = getApplicationContext().getResources().getIdentifier(aString, "string", getApplicationContext().getPackageName());//"com.weather.my.weatherapp"
        return getString(resId);
    }

    public void updateWeather(Location location) {
        WeatherDataProvider.getCurrentWeather(location);
        WeatherDataProvider.getDaysForecast(location);
        WeatherDataProvider.getHoursForecast(location);
    }

    public void setWeather(Object weatherData) {
        if (weatherData instanceof CurrentWeather) {
            typeWeatherReceived[0] = true;
            if (isAllWeatherReceived()) {
                setUpRecyclers();
                swipeRefreshLayout.setRefreshing(false);
            }
            CurrentWeather currentWeather = (CurrentWeather) weatherData;
            condition.setText(currentWeather.getWeather().get(0).getDescription());
            setWeatherIcon(currentWeather.getWeather().get(0).getId(),
                    currentWeather.getSys().getSunrise(), currentWeather.getSys().getSunset());
            DataProvider.setCurrentWeather((CurrentWeather) weatherData);
            Log.v(LOG,"Current weather is set");
        }
        if (weatherData instanceof DaysForecast) {
            typeWeatherReceived[1] = true;
            if (isAllWeatherReceived()) {
                setUpRecyclers();
                swipeRefreshLayout.setRefreshing(false);
            }
            DataProvider.setDaysForecast((DaysForecast) weatherData);
        }
        if (weatherData instanceof HoursForecast) {
            typeWeatherReceived[2] = true;
            if (isAllWeatherReceived()) {
                setUpRecyclers();
                swipeRefreshLayout.setRefreshing(false);
            }
            DataProvider.setHoursForecast((HoursForecast) weatherData);
        }
    }

    public void setUpRecyclers() {
        fillRecycler();
        Transition wIconAnimation = new Slide(Gravity.RIGHT);
        wIconAnimation.addTarget(wIcon);
        wIconAnimation.addTarget(condition);
        wIconAnimation.setDuration(ANIMATION_DURATION);
        TransitionManager.beginDelayedTransition(mainContainer, wIconAnimation);
        Utils.setVisibility(View.VISIBLE, condition, wIcon);

    }

    public boolean isAllWeatherReceived() {
        return typeWeatherReceived[0] && typeWeatherReceived[1] && typeWeatherReceived[2];
    }

    public void setLocation(Location location) {
        if (lastKnownLocation == null) {
            updateWeather(location);
            lastKnownLocation = location;
        }
        if (lastKnownLocation.getLatitude() != location.getLatitude()
                && lastKnownLocation.getLongitude() != location.getLongitude()) {
            updateWeather(location);
            lastKnownLocation = location;
        }
    }

    public void createInformationBlock() {
        allowAccessBlock = findViewById(R.id.allow_access_block);
        enterLocationBlock = findViewById(R.id.enter_location_block);
        Utils.setVisibility(View.VISIBLE, allowAccessBlock, enterLocationBlock);
        allowAccessBlock.findViewById(R.id.allow_access_text).setOnClickListener(v -> {
            checkForPermissions();
        });
    }

    public void removeInformationBlock() {
        if (allowAccessBlock != null) {
            Utils.setVisibility(View.INVISIBLE, allowAccessBlock, enterLocationBlock);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_GPS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCreate();
                    removeInformationBlock();

                } else {
                    Toast.makeText(this, "Gps access denied", Toast.LENGTH_SHORT).show();
                    createInformationBlock();
                }
                break;
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        setLocation(location);
    }

    @Override
    public void onLocationReceivedError() {
        Toast.makeText(this, "Can not receive GPS location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWeatherReceived(Object weatherData) {
        setWeather(weatherData);
    }

    @Override
    public void onWeatherReceivedError() {
        Toast.makeText(this, "Can not receive weather information", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (locationDataProvider != null) {
            locationDataProvider.removeUpdates();
        }
        super.onDestroy();
    }
}

package com.weather.my.weatherapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
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

import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.weather.my.weatherapp.R;
import com.weather.my.weatherapp.adapters.MainRecyclerAdapter;
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

import static com.weather.my.weatherapp.Constants.MAX_TRANSPARENCY;
import static com.weather.my.weatherapp.Constants.REQUEST_PERMISSION_GPS;
import static com.weather.my.weatherapp.Constants.RESOURCE_DAY_PREFIX;
import static com.weather.my.weatherapp.Constants.RESOURCE_NIGHT_PREFIX;

public class MainActivity extends AppCompatActivity implements LocationDataProvider.OnLocationReceivedListener,
        WeatherDataProvider.OnWeatherReceivedListener {

    Typeface weatherFont;

    TextView wIcon;
    TextView condition;
    RecyclerView recyclerView;
    ImageView backgroundPhoto;
    ImageView backgroundBlurPhoto;
    ConstraintLayout mainContainer;

    public int delta = 0;
    private float offsetIcon = 0;
    private float offsetCondition = 0;
    final private String LOG = "mLog";
    private boolean isFirstTime = true;
    private LocationDataProvider locationDataProvider;
    MainRecyclerAdapter adapter;
    private boolean isFirstTimeReceivedWeather = true;
    public boolean[] typeWeatherReceived = new boolean[3];
    public int maxPosition;
    public boolean isNotified = false;
    public Location lastKnownLocation;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermissions();
        createLocationProvider();
        setUpToolbar();
        findViews();
        intiViews();
    }

    public void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_GPS);
            return;
        }
    }

    public void intiViews() {
        WeatherDataProvider.setListener(this);
        backgroundBlurPhoto.setAlpha(0);
        backgroundBlurPhoto.setVisibility(View.VISIBLE);
        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weather.ttf");
        wIcon.setTypeface(weatherFont);
        setRefreshListener();
    }

    public void setRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateWeather(lastKnownLocation);
        });
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
        mainContainer = (ConstraintLayout) findViewById(R.id.item_header);
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
                Log.v(LOG, "recycler dy: " + dy);
//                updateWindAnimation();
                moveViews();
            }
        });
    }

    private void updateWindAnimation() {
        if (Build.VERSION.SDK_INT <= 15) {
            RecyclerView.LayoutManager layMan = recyclerView.getLayoutManager();
            int lastPosition = ((LinearLayoutManager) layMan).findLastVisibleItemPosition();
            if (lastPosition == 2) {
                isNotified = false;
            }
            if (lastPosition == 3 && !isNotified) {
                adapter.notifyItemChanged(lastPosition);
                isNotified = true;
            }
        }
    }

    private void moveViews() {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
//            Log.v(LOG, "Scroll successful: " + dy + " delta: " + delta * 4 + " compute: " + recyclerView.computeVerticalScrollOffset() / 10);
            int transparency = Math.abs(holder.itemView.getTop());
            if (transparency > MAX_TRANSPARENCY) {
                transparency = MAX_TRANSPARENCY;
            }
//            Log.v(LOG, "Vertical offset " + recyclerView.computeVerticalScrollOffset());
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

//    @OnClick(R.id.button3)
//    public void goToNewActivity() {
//        Log.v(LOG, "New Activity clicked!");
//        Blurry.with(this).capture(backgroundPhoto).into(backgroundPhoto);
//    }


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

    private String getStringResourceByName(String aString) {//wi_owm_day_
//        String packageName = getPackageName();
        int resId = getApplicationContext().getResources().getIdentifier(aString, "string", getApplicationContext().getPackageName());//"com.weather.my.weatherapp"
        return getString(resId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_GPS:
                //Permission received
                break;
        }
    }

    public boolean isAllWeatherReceived() {
        return typeWeatherReceived[0] && typeWeatherReceived[1] && typeWeatherReceived[2];
    }

    @Override
    public void onLocationReceived(Location location) {
        if (lastKnownLocation == null) {
            updateWeather(location);
            lastKnownLocation = location;
        }

        Log.v(LOG, "Location received is:" + location.getLatitude() + " : " + location.getLatitude());
        if (lastKnownLocation.getLatitude() != location.getLatitude()
                && lastKnownLocation.getLongitude() != location.getLongitude()) {
            updateWeather(location);
            lastKnownLocation = location;
        }


    }

    @Override
    public void onWeatherReceived(Object weatherData) {
        setWeather(weatherData);
    }

    public void updateWeather(Location location) {
        Log.v(LOG, "Weather is updated");
        WeatherDataProvider.getCurrentWeather(location);
        WeatherDataProvider.getDaysForecast(location);
        WeatherDataProvider.getHoursForecast(location);
        isFirstTimeReceivedWeather = false;
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
            setWeatherIcon(currentWeather.getWeather().get(0).getId(), currentWeather.getSys().getSunrise(), currentWeather.getSys().getSunset());
            DataProvider.setCurrentWeather((CurrentWeather) weatherData);
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
        wIconAnimation.setDuration(2000);
        TransitionManager.beginDelayedTransition(mainContainer, wIconAnimation);
        Utils.setVisibility(View.VISIBLE, condition, wIcon);

    }
}

//    public void getCurrentWeather(Location location) {
//        double[] coordinates = new double[2];
//        if (locationDataProvider == null) {
//            Log.v("LocationDataProvider", "LocationDataProvider is empty inside getCurrentWeather");
//            return;
//        } else if (locationDataProvider.location == null) {
//            Log.v("LocationDataProvider", "LocationDataProvider is empty inside getCurrentWeather 2");
//            return;
//        }
//        Log.v("LocationDataProvider", "LocationDataProvider not empty make request");
//        coordinates[0] = location.getLatitude();
//        coordinates[1] = location.getLongitude();
//        //If internet is available statement
//        DataProvider.getApi(CurrentWeatherApi.class).getWeather(String.valueOf(coordinates[0]), String.valueOf(coordinates[1]))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<CurrentWeather>() {
//
//                    @Override
//                    public void onNext(CurrentWeather currentWeather) {
//                        DataProvider.setCurrentWeather(currentWeather);
//                        Log.v("LocationDataProvider", "New currentWeather received and set new info on condition textView");
//                        condition.setText(currentWeather.getWeather().get(0).getDescription());
//                        setWeatherIcon(currentWeather.getWeather().get(0).getId(), currentWeather.getSys().getSunrise(), currentWeather.getSys().getSunset());
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        Log.v("mLogs", "Work completed in current weather!");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.v("mLog", e.toString());
//                    }
//                });
//    }
//
//    public void getDaysForecast(Location location) {
//        DataProvider.getApi(DaysForecastApi.class).getDaysForecast(location.getLatitude(), location.getLongitude())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<DaysForecast>() {
//
//                    @Override
//                    public void onNext(DaysForecast daysForecast) {
//                        Log.v(LOG, "Forecast days received: " + String.valueOf(daysForecast.getList().get(0).getWeather().get(0).getId()));
//                        Toast.makeText(MainActivity.this, String.valueOf(daysForecast.getList().get(0).getWeather().get(0).getId()), Toast.LENGTH_LONG).show();
//                        DataProvider.setDaysForecast(daysForecast);
//                        fillRecycler();
//                        setScrollListener();
//
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        Log.v("mLogs", "Work completed in days forecast!");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        Log.v("mLogs", e.toString());
//                    }
//                });
//    }
//
//    public void getHoursForecast(Location location) {
//        DataProvider.getApi(HoursForecastApi.class).getHoursForecast(location.getLatitude(), location.getLongitude())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<HoursForecast>() {
//
//                    @Override
//                    public void onNext(HoursForecast hoursForecast) {
//                        Log.v(LOG, "Forecast hours received: " + String.valueOf(hoursForecast.getList().get(0).getMain().getTemp()));
//                        Log.v(LOG, "Forecast hours received: " + String.valueOf(hoursForecast.getList().get(1).getMain().getTemp()));
//                        DataProvider.setHoursForecast(hoursForecast);
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        Log.v("mLogs", "Work completed in hours forecast!");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        Log.v("mLogs", e.toString());
//                    }
//                });
//    }


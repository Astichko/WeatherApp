package com.weather.my.weatherapp.providers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static com.weather.my.weatherapp.Constants.REQUEST_PERMISSION_GPS;

/**
 * Created by 1 on 12.01.2017.
 */

public class LocationDataProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public String TAG = "LocationDataProvider";
    public ArrayList<OnLocationReceivedListener> arrayLocationReceivedListeners;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context context;
    public Location location;
    public boolean isFirstLocationReceived = true;

    public LocationDataProvider(Context context) {
        this.context = context;
        createGoogleAPiClientAndConnect(context);
    }

    public void createGoogleAPiClientAndConnect(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        //Location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20 * 1000)        // 20 seconds, in milliseconds
                .setFastestInterval(20 * 1000); // 20 second, in milliseconds
        //And Connect
        mGoogleApiClient.connect();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_GPS);
            return;
        }

        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
//        Log.i(TAG, "Location services connected and location is:" + location.getLatitude() + " : " + location.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        transferLocation(location);
        if (isFirstLocationReceived) {
            createHourRequest();
            isFirstLocationReceived = false;
        }
//        createHourRequest();
//        Toast.makeText(context, location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "LocationDataProvider in locationChanged is: " + location.getLatitude() + " : " + location.getLongitude());
    }

    public void createHourRequest() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_GPS);
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //Location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(60 * 1000)        // 60 seconds, in milliseconds
                .setFastestInterval(60 * 1000); // 60 second, in milliseconds

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void handleNewLocation(Location location) {
        transferLocation(location);
        Log.i(TAG, "Location in handleNewLocation is: " + location.getLatitude() + " : " + location.getLongitude());
    }

    public interface OnLocationReceivedListener {
        void onLocationReceived(Location location);
    }


    public void setListener(OnLocationReceivedListener locationReceived) {
        if (arrayLocationReceivedListeners == null) {
            arrayLocationReceivedListeners = new ArrayList<>();
        }
        arrayLocationReceivedListeners.add(locationReceived);
    }

    public void transferLocation(Location location) {
        for (int i = 0; i < arrayLocationReceivedListeners.size(); i++) {
            arrayLocationReceivedListeners.get(i).onLocationReceived(location);
        }
    }
}

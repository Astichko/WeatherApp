package com.weather.my.weatherapp.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;


public class Utils {

    public static String getAddressByCoordinates(Context context, double lat, double lon) {
//        double lat1 = 51.5073509;
//        double lon1 = -0.1277583;

        Log.v("LocationDataProvider", "Location in:" + lat + " : " + lon);
        Geocoder gc = new Geocoder(context);
//        List<Address> list = gc.getFromLocationName("1600 Amphitheatre Parkway, Mountain View, CA", 1);
        List<Address> list = null;
        try {
            list = gc.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "New " + list.get(0).toString();

//        List<Address> addresses = null;
//        Geocoder geocoder = new Geocoder(context);
//        try {
//            addresses = geocoder.getFromLocationName("London", 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Address address = addresses.get(0);
//        if (address != null) {
//            Toast.makeText(context, address.getLatitude() + " : " + address.getLongitude(), Toast.LENGTH_SHORT).show();
//            Log.v("LocationDataProvider", address.getLatitude() + " : " + address.getLongitude());
//        }
//
//        return address.toString();


    }

    public static String getStringResources(Context context, int id){
        return context.getResources().getString(id);
    }

    public static void setVisibility(int visibility, View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(visibility);
        }
    }

}

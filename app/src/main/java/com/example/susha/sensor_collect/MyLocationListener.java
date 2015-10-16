package com.example.susha.sensor_collect;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by susha on 10/12/2015.
 */
public class MyLocationListener implements LocationListener{

    public static double latitude;
    public static double longitude;

    @Override
    public void onLocationChanged(Location loc) {
        loc.getLatitude();
        loc.getLongitude();
        latitude=loc.getLatitude();
        longitude=loc.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

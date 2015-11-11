package com.example.susha.sensor_collect;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.example.susha.sensor_collect.MainActivity.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by susha on 10/12/2015.
 */
public class MyLocationListener implements LocationListener{

    public static double latitude;
    public static double longitude;
    private File gps;
    private BufferedWriter gpsofstream;


    @Override
    public void onLocationChanged(Location loc) {
        Double mlongitude = loc.getLongitude();
        Double mlatitude = loc.getLatitude();
        longitude = mlongitude;
        latitude = mlatitude;

        gps = new File(MainActivity.SessionDir + File.separator + "gps.txt");
        if (!gps.exists()) {
            try {
                gpsofstream = new BufferedWriter(new FileWriter(gps));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            gpsofstream.write(Long.toString(new Date().getTime()) + "\t" + mlatitude + " " + mlongitude + "\n");
            gpsofstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double getLongitude() {
        return longitude;
    }

    public static double getLatitude() {
        return latitude;
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

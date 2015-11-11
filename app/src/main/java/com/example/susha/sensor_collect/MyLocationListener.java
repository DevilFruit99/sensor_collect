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

    public static double mlongitude;
    public static double mlatitude;
    private File gps;
    private BufferedWriter gpsofstream;

    public MyLocationListener() {
        gps = new File(MainActivity.SessionDir + File.separator + "gps.txt");
        try {
            gpsofstream = new BufferedWriter(new FileWriter(gps));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location loc) {
        mlongitude = loc.getLongitude();
        mlatitude = loc.getLatitude();

        try {
            String add = Long.toString(new Date().getTime()) + "\t" + mlatitude + " " + mlongitude + "\n";
            gpsofstream.write(add);
            gpsofstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

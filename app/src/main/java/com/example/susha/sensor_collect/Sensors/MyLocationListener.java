package com.example.susha.sensor_collect.Sensors;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Service;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.susha.sensor_collect.MainActivity.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class MyLocationListener extends Service implements LocationListener{

    public static double mlongitude;
    public static double mlatitude;
    private File gps;
    private BufferedWriter gpsofstream;
    private final Context mContext;
    private boolean writeGPS;

    public MyLocationListener(Context context,boolean writeGPS) {
        this.mContext = context;
        this.writeGPS = writeGPS;
        if(writeGPS) {
            gps = new File(MainActivity.SessionDir + File.separator + "gps.txt");
            try {
                gpsofstream = new BufferedWriter(new FileWriter(gps));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        mlongitude = loc.getLongitude();
        mlatitude = loc.getLatitude();

        if(writeGPS) {
            try {
                String add = Long.toString(new Date().getTime()) + "\t" + mlatitude + " " + mlongitude + "\n";
                gpsofstream.write(add);
                gpsofstream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(mContext,
                    "Debugging Purposes:\nLat: " + mlatitude + "\nLon: " + mlongitude,
                    Toast.LENGTH_SHORT).show();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

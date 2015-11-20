package com.example.susha.sensor_collect.FileHandler;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dardan on 11/17/2015.
 */

public class FIleHandler {
    private final Context mContext;
    private File gyroscope;
    private File magnetic;
    private File accelerometer;
    private File orientation;
    private File gravity;



    private BufferedWriter gyroscopeofstream;
    private BufferedWriter magneticofstream;
    private BufferedWriter accelerometerofstream;
    private BufferedWriter orientationofstream;
    private BufferedWriter gravityofstream;
    private BufferedWriter summaryofstream;
    private BufferedWriter wifiofstream;
    private BufferedWriter visualofstream;
    private File summaryfile;
    private File wififile;
    private File visualfile;

    private ArrayList<String> toBeScanned;

    public FIleHandler(Context context){
        // Create a list of what is to be checked (MediaScannerConnection list)
        toBeScanned = new ArrayList<String>();
        this.mContext = context;
    }

    public void setStreamsNull(){
        // Init all bufferWriter objects to null, letting ifChecked modify them
        wifiofstream = null;
        visualofstream = null;
        gyroscopeofstream = null;
        magneticofstream = null;
        accelerometerofstream = null;
        orientationofstream = null;
        gravityofstream = null;
    }

    public void createWifi(String dir) throws IOException {
        wififile = new File(dir);
        toBeScanned.add(dir);
        wifiofstream = new BufferedWriter(new FileWriter(wififile));
    }
    public void createVisual(String dir) throws IOException {
        visualfile = new File(dir);
        toBeScanned.add(dir);
        visualofstream = new BufferedWriter(new FileWriter(visualfile));
    }
    public void createGyro(String dir) throws IOException {
        gyroscope = new File(dir);
        toBeScanned.add(dir);
        gyroscopeofstream = new BufferedWriter(new FileWriter(gyroscope));
    }
    public void createMagnetic(String dir) throws IOException {
        magnetic = new File(dir);
        toBeScanned.add(dir);
        magneticofstream = new BufferedWriter(new FileWriter(magnetic));
    }
    public void createAccelerometer(String dir) throws IOException {
        accelerometer = new File(dir);
        toBeScanned.add(dir);
        accelerometerofstream = new BufferedWriter(new FileWriter(accelerometer));
    }
    public void createOrientation(String dir) throws IOException {
        orientation = new File(dir);
        toBeScanned.add(dir);
        orientationofstream = new BufferedWriter(new FileWriter(orientation));
    }
    public void createGravity(String dir) throws IOException {
        gravity = new File(dir);
        toBeScanned.add(dir);
        gravityofstream = new BufferedWriter(new FileWriter(gravity));
    }

    public void createGPS(String dir) throws IOException {
        toBeScanned.add(dir);
    }

    public void createSummary(String dir) throws IOException {
        summaryfile = new File(dir);
        toBeScanned.add(dir);
        summaryofstream = new BufferedWriter(new FileWriter(summaryfile));
    }

    public void fillSummaryWithGPS(Double lat, Double lon){
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String summaryFileText = Long.toString(new Date().getTime()) + "\t" + lat + " " + lon + "\n" ;
        String s="Debug:";
        s += "\n Device ID: " + tm.getDeviceId();
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
        summaryFileText+=s;
        try {
            summaryofstream.write(summaryFileText);
            summaryofstream.flush();
        } catch (IOException e) {
            Toast.makeText(mContext, "Failed to write to Summary file.txt", Toast.LENGTH_SHORT).show();
        }
    }

    public void fillSummaryWithoutGPS(){
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String summaryFileText = Long.toString(new Date().getTime()) + "\t" +"0.0 0.0" + "\n";
        String s="Debug:";
        s += "\n Device ID: " + tm.getDeviceId();
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
        summaryFileText+=s;
        try {
            summaryofstream.write(summaryFileText);
            summaryofstream.flush();
        } catch (IOException e) {
            Toast.makeText(mContext, "Failed to write to Summary file.txt", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(mContext, "GPS off or not ready.", Toast.LENGTH_SHORT).show();
    }

    public void invokeMediaScanner(){
        // Iterate through the toBeScanned list for MediaScannerConnection
        String[] toBeScannedStr = new String[toBeScanned.size()];
        toBeScannedStr = toBeScanned.toArray(toBeScannedStr);
        MediaScannerConnection.scanFile(mContext, toBeScannedStr, null, null);

    }

    public void visualStreamWrite(String path) throws IOException {
        visualofstream.write(path);
        visualofstream.flush();
    }

    public void wifiStreamWrite(String path) throws IOException {
        wifiofstream.write(path);
        wifiofstream.flush();
    }






    //
    // TEMP GETTERS FOR BUFFEREDWRITERS, WILL BE REPLACED IN LATER ITERATION.
    //e
    public BufferedWriter getGyroscopeofstream() {
        return gyroscopeofstream;
    }

    public BufferedWriter getMagneticofstream() {
        return magneticofstream;
    }

    public BufferedWriter getAccelerometerofstream() {
        return accelerometerofstream;
    }

    public BufferedWriter getOrientationofstream() {
        return orientationofstream;
    }

    public BufferedWriter getGravityofstream() {
        return gravityofstream;
    }

    public BufferedWriter getSummaryofstream() {
        return summaryofstream;
    }

    public BufferedWriter getWifiofstream() {
        return wifiofstream;
    }

    public BufferedWriter getVisualofstream() {
        return visualofstream;
    }

}

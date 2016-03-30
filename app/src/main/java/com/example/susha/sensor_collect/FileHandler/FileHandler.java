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

public class FileHandler {
    private final Context mContext;

    private File wifiFile;
    private File visualFile;
    private File gyroscopeFile;
    private File magneticFile;
    private File accelerometerFile;
    private File orientationFile;
    private File gravityFile;
    private File summaryFile;

    private BufferedWriter wifiOutputStream;
    private BufferedWriter visualOutputStream;
    private BufferedWriter gyroscopeOutputStream;
    private BufferedWriter magneticOutputStream;
    private BufferedWriter accelerometerOutputStream;
    private BufferedWriter orientationOutputStream;
    private BufferedWriter gravityOutputStream;
    private BufferedWriter summaryOutputStream;

    private ArrayList<String> toBeScanned;

    public FileHandler(Context context){
        // Create a list of what is to be checked (MediaScannerConnection list)
        toBeScanned = new ArrayList<String>();
        this.mContext = context;
    }

    public void initOutputStreams(){
        // Init all bufferWriter objects to null, letting ifChecked modify them
        wifiOutputStream = null;
        visualOutputStream = null;
        gyroscopeOutputStream = null;
        magneticOutputStream = null;
        accelerometerOutputStream = null;
        orientationOutputStream = null;
        gravityOutputStream = null;
    }

    public void createWifi(String dir) throws IOException {
        wifiFile = new File(dir);
        toBeScanned.add(dir);
        wifiOutputStream = new BufferedWriter(new FileWriter(wifiFile));
    }
    public void createVisual(String dir) throws IOException {
        visualFile = new File(dir);
        toBeScanned.add(dir);
        visualOutputStream = new BufferedWriter(new FileWriter(visualFile));
    }
    public void createGyro(String dir) throws IOException {
        gyroscopeFile = new File(dir);
        toBeScanned.add(dir);
        gyroscopeOutputStream = new BufferedWriter(new FileWriter(gyroscopeFile));
    }
    public void createMagnetic(String dir) throws IOException {
        magneticFile = new File(dir);
        toBeScanned.add(dir);
        magneticOutputStream = new BufferedWriter(new FileWriter(magneticFile));
    }
    public void createAccelerometer(String dir) throws IOException {
        accelerometerFile = new File(dir);
        toBeScanned.add(dir);
        accelerometerOutputStream = new BufferedWriter(new FileWriter(accelerometerFile));
    }
    public void createOrientation(String dir) throws IOException {
        orientationFile = new File(dir);
        toBeScanned.add(dir);
        orientationOutputStream = new BufferedWriter(new FileWriter(orientationFile));
    }
    public void createGravity(String dir) throws IOException {
        gravityFile = new File(dir);
        toBeScanned.add(dir);
        gravityOutputStream = new BufferedWriter(new FileWriter(gravityFile));
    }

    public void createGPS(String dir) throws IOException {
        toBeScanned.add(dir);
    }

    public void createSummary(String dir) throws IOException {
        summaryFile = new File(dir);
        toBeScanned.add(dir);
        summaryOutputStream = new BufferedWriter(new FileWriter(summaryFile));
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
            summaryOutputStream.write(summaryFileText);
            summaryOutputStream.flush();
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
            summaryOutputStream.write(summaryFileText);
            summaryOutputStream.flush();
        } catch (IOException e) {
            Toast.makeText(mContext, "Failed to write to Summary file.txt", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(mContext, "No GPS Signal \n GPS coordinates excluded for summary file .", Toast.LENGTH_LONG).show();
    }

    public void invokeMediaScanner(){
        // Iterate through the toBeScanned list for MediaScannerConnection
        String[] toBeScannedStr = new String[toBeScanned.size()];
        toBeScannedStr = toBeScanned.toArray(toBeScannedStr);
        MediaScannerConnection.scanFile(mContext, toBeScannedStr, null, null);

    }

    public void visualStreamWrite(String path) throws IOException {
        visualOutputStream.write(path);
        visualOutputStream.flush();
    }

    public void wifiStreamWrite(String path) throws IOException {
        wifiOutputStream.write(path);
        wifiOutputStream.flush();
    }

    public void writeAccelerometer(String add) throws IOException {
        accelerometerOutputStream.write(add);
        accelerometerOutputStream.flush();
    }

    public void writeGravity(String add) throws IOException {
        gravityOutputStream.write(add);
        gravityOutputStream.flush();
    }

    public void writeGyroscope(String add) throws IOException {
        gyroscopeOutputStream.write(add);
        gyroscopeOutputStream.flush();
    }

    public void writeOrientation(String add) throws IOException {
        orientationOutputStream.write(add);
        orientationOutputStream.flush();
    }

    public void writeMagnetic(String add) throws IOException {
        magneticOutputStream.write(add);
        magneticOutputStream.flush();
    }

    public boolean isAccelerometerStreamNull(){
        return accelerometerOutputStream == null;
    }
    public boolean isGravityStreamNull(){
        return gravityOutputStream == null;
    }
    public boolean isGyroscopeStreamNull(){
        return gyroscopeOutputStream == null;
    }
    public boolean isOrientationStreamNull(){
        return orientationOutputStream == null;
    }
    public boolean isMagneticStreamNull(){
        return magneticOutputStream == null;
    }
}

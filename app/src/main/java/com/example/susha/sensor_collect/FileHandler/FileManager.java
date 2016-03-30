package com.example.susha.sensor_collect.FileHandler;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by edwardwang on 3/30/16.
 */
public class FileManager {
    //Singleton object
    //Since this is left public, the constructor can be left private because it should not be called anywhere else in the project
    public static FileManager instance = new FileManager();

    private Context mContext;

    private File wifiFile;
    private File visualFile;
    private File gyroscopeFile;
    private File magneticFile;
    private File accelerometerFile;
    private File orientationFile;
    private File gravityFile;
    private File summaryFile;

    private BufferedWriter wifiOutputStream = null;
    private BufferedWriter visualOutputStream;
    private BufferedWriter gyroscopeOutputStream;
    private BufferedWriter magneticOutputStream;
    private BufferedWriter accelerometerOutputStream;
    private BufferedWriter orientationOutputStream;
    private BufferedWriter gravityOutputStream;
    private BufferedWriter summaryOutputStream;

    //Buffer which streams will write to
    private ArrayList<String> buffer;

    private FileManager(){
        initOutputStreams();
    }

    private void initOutputStreams(){
        wifiOutputStream = null;
        visualOutputStream = null;
        gyroscopeOutputStream = null;
        magneticOutputStream = null;
        accelerometerOutputStream = null;
        orientationOutputStream = null;
        gravityOutputStream = null;
    }

    /**
     * Create a new file and output stream for a given sensor to a specified directory. GPS however
     * is managed differently and therefore will not be having its own File/BufferWriter.
     * @param fileType
     * @param dir
     * @throws IOException
     */
    public void createSensorFileAndOutputStream(FileType fileType, String dir) throws IOException {
        if(fileType != FileType.GPS){
            File file;
            BufferedWriter outputStream;
            switch(fileType){
                case WIFI:
                    file = wifiFile;
                    outputStream = wifiOutputStream;
                    break;
                case GYROSCOPE:
                    file = gyroscopeFile;
                    outputStream = gyroscopeOutputStream;
                    break;
                case MAGNETIC:
                    file = magneticFile;
                    outputStream = magneticOutputStream;
                    break;
                case ACCELEROMETER:
                    file = accelerometerFile;
                    outputStream = accelerometerOutputStream;
                    break;
                case ORIENTATION:
                    file = orientationFile;
                    outputStream = orientationOutputStream;
                    break;
                case GRAVITY:
                    file = gravityFile;
                    outputStream = gravityOutputStream;
                    break;
                case SUMMARY:
                    file = summaryFile;
                    outputStream = summaryOutputStream;
                    break;
            }
            file = new File(dir);
            outputStream = new BufferedWriter(new FileWriter(file));
        }
        buffer.add(dir);
    }

    /**
     * Depending on the isGPSNull bool, the long and lat will be set, device info is collected and
     * flushed/written to given directory.
     * @param isGPSNull
     * @param latitude
     * @param longitude
     */
    public void fillSummaryWithGPS(boolean isGPSNull,Double latitude, Double longitude){
        String summaryFileText;
        if(!isGPSNull){
            summaryFileText = Long.toString(new Date().getTime()) + "\t" + latitude + " " + longitude
                    + "\n" ;
        }else{
            summaryFileText = Long.toString(new Date().getTime()) + "\t" +"0.0 0.0" + "\n";
            Toast.makeText(mContext, "No GPS Signal \n GPS coordinates excluded for summary file .", Toast.LENGTH_LONG).show();
        }
        summaryFileText += getDeviceInformation();

        outputToDir(FileType.SUMMARY, summaryFileText);
    }

    /**
     * @return debugging information involving device information.
     */
    private String getDeviceInformation(){
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return "Debug:" + "\n Device ID: " + tm.getDeviceId() +
                "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")" +
                "\n OS API Level: " + android.os.Build.VERSION.SDK_INT +
                "\n Device: " + android.os.Build.DEVICE +
                "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
    }

    /**
     * Writes information to given file and outputs it to the directory.
     * @param fileType
     * @param info
     */
    public void outputToDir(FileType fileType, String info){
        BufferedWriter outputStream;
        String errorMessage;
        switch (fileType){
            case WIFI:
                outputStream = wifiOutputStream;
                errorMessage = "No wifi";   //TODO:Need to update this message
                break;
            case GYROSCOPE:
                outputStream = gyroscopeOutputStream;
                errorMessage = "Gyroscope record fail; queue full";
                break;
            case MAGNETIC:
                outputStream = magneticOutputStream;
                errorMessage = "Magnetometer record fail; queue full";
                break;
            case ACCELEROMETER:
                outputStream = accelerometerOutputStream;
                errorMessage = "Acceleration record fail; queue full";
                break;
            case ORIENTATION:
                outputStream = orientationOutputStream;
                errorMessage = "Depreciated Orientation record fail; queue full";
                break;
            case GRAVITY:
                outputStream = gravityOutputStream;
                errorMessage = "Gravity record fail; queue full";
                break;
            case SUMMARY:
                outputStream = summaryOutputStream;
                errorMessage = "Failed to write to Summary file.txt";
                break;
            default:
                outputStream = null;
                errorMessage="Default Error Message";
        }
        try{
            outputStream.write(info);
            outputStream.flush();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isStreamNull(BufferedWriter outputStream){
        return (outputStream==null);
    }
}

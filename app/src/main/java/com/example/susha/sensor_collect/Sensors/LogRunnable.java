package com.example.susha.sensor_collect.Sensors;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.HandlerThread;
import android.widget.Toast;

import com.example.susha.sensor_collect.FileHandler.FileHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by susha on 9/16/2015.
 */
public class LogRunnable implements Runnable{
    private Context mContext;
    private SensorManager mSensorManager = null;
    private Sensor mSensor;
    private File mLogFile = null;
    private FileOutputStream mFileStream = null;
    private SensorEventListener mListener;
    private HandlerThread mHandlerThread;
    private FileHandler fileHandler;

    private List<Sensor> currentDevice = new ArrayList<Sensor>();

    private float[] mGravity;
    private float[] mGeomagnetic;
    private int sensorScanRate;
    boolean run;

    /* Never used, will delete eventually
    LogRunnable(Context context, BufferedWriter ofstream, boolean[] value) {
        mContext = context;
        //inertiaofstream = ofstream;
        run = value[0];
    }*/

    public LogRunnable(Context context, FileHandler fileHandler, boolean[] value, int sensorScanRate) {
        mContext = context;
        this.fileHandler = fileHandler;
        run = value[0];
        this.sensorScanRate = sensorScanRate;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        /*mHandlerThread = new HandlerThread("InertiaLogListener");
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());*/


        mListener = new SensorEventListener() {
            @Override
            public final void onSensorChanged(SensorEvent event) {
                Date current = new Date();

                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) && !fileHandler.isAccelerometerStreamNull()) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    mGravity = event.values;
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        fileHandler.writeAccelerometer(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Accel record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) && !fileHandler.isGravityStreamNull()) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        fileHandler.writeGravity(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Gravity record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) && !fileHandler.isGyroscopeStreamNull()) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        fileHandler.writeGyroscope(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Gyro record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }

                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) && !fileHandler.isOrientationStreamNull()) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        fileHandler.writeOrientation(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Depreciated Orientation record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) && !fileHandler.isMagneticStreamNull()) {
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    mGeomagnetic = event.values;
                    try {
                        fileHandler.writeMagnetic(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Magnetometer record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (mGravity != null && mGeomagnetic != null && !fileHandler.isOrientationStreamNull()) {
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                    if (success) {
                        float orientation[] = new float[3];
                        SensorManager.getOrientation(R, orientation);
                        String add = Long.toString(current.getTime()) + "\t" + orientation[0] + "\t" + orientation[1] + "\t" + orientation[2] + "\n";
                        try {
                            fileHandler.writeOrientation(add);
                        } catch (IOException e) {
                            Toast.makeText(mContext, "'Updated' Orientation record fail; queue full", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        for (Sensor insert : currentDevice) {
            mSensorManager.registerListener(mListener, insert, sensorScanRate,100);
        }
    }


    public void cleanThread() {

        //Unregister the listener
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mListener);
        }
/*
        if (mHandlerThread.isAlive())
            mHandlerThread.quit();*/


        //Flush and close file stream
        if (mFileStream != null) {
            try {
                mFileStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mFileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

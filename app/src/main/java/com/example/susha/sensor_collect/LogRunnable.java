package com.example.susha.sensor_collect;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
    private BufferedWriter inertiaofstream;

    private BufferedWriter gyroscopeofstream;
    private BufferedWriter magneticofstream;
    private BufferedWriter accelerometerofstream;
    private BufferedWriter orientationofstream;
    private BufferedWriter gravityofstream;

    private List<Sensor> currentDevice = new ArrayList<Sensor>();

    private float[] mGravity;
    private float[] mGeomagnetic;
    boolean run;

    LogRunnable(Context context, BufferedWriter ofstream, boolean[] value) {
        mContext = context;
        inertiaofstream = ofstream;
        run = value[0];
    }

    LogRunnable(Context context, BufferedWriter gyroscopeofstream, BufferedWriter magneticofstream,
                BufferedWriter accelerometerofstream, BufferedWriter orientationofstream, BufferedWriter gravityofstream, boolean[] value) {
        mContext = context;
        this.gyroscopeofstream = gyroscopeofstream;
        this.magneticofstream = magneticofstream;
        this.accelerometerofstream = accelerometerofstream;
        this.orientationofstream = orientationofstream;
        this.gravityofstream = gravityofstream;
        //inertiaofstream = gyroscopeofstream;

        run = value[0];
    }

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
        mHandlerThread = new HandlerThread("InertiaLogListener");
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());


        mListener = new SensorEventListener() {
            @Override
            public final void onSensorChanged(SensorEvent event) {
                Date current = new Date();
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    mGravity = event.values;
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        accelerometerofstream.write(add);
                        accelerometerofstream.flush();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Accel record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        gravityofstream.write(add);
                        gravityofstream.flush();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Gravity record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        gyroscopeofstream.write(add);
                        gyroscopeofstream.flush();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Gyro record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }

                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        orientationofstream.write(add);
                        orientationofstream.flush();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Depreciated Orientation record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)) {
                    String add = Long.toString(current.getTime()) + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    mGeomagnetic = event.values;
                    try {
                        magneticofstream.write(add);
                        magneticofstream.flush();
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Magnetometer record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (mGravity != null && mGeomagnetic != null) {
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                    if (success) {
                        float orientation[] = new float[3];
                        SensorManager.getOrientation(R, orientation);
                        String add = Long.toString(current.getTime()) + "\t" + orientation[0] + "\t" + orientation[1] + "\t" + orientation[2] + "\n";
                        try {
                            orientationofstream.write(add);
                            orientationofstream.flush();
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
            mSensorManager.registerListener(mListener, insert, SensorManager.SENSOR_DELAY_NORMAL, handler);
        }
    }


    public void cleanThread() {

        //Unregister the listener
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mListener);
        }

        if (mHandlerThread.isAlive())
            mHandlerThread.quit();


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

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
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
            currentDevice.add(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        /*mHandlerThread = new HandlerThread("InertiaLogListener");
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());*/


        mListener = new SensorEventListener() {
            @Override
            public final void onSensorChanged(SensorEvent event) {
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) && !fileHandler.isAccelerometerStreamNull()) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    mGravity = event.values;

                    String add = System.currentTimeMillis()+ "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        fileHandler.writeAccelerometer(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Accel record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) && !fileHandler.isGravityStreamNull()) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    String add = System.currentTimeMillis() + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        fileHandler.writeGravity(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Gravity record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) && !fileHandler.isGyroscopeStreamNull()) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    String add = System.currentTimeMillis() + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    try {
                        fileHandler.writeGyroscope(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Gyro record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }

                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) && !fileHandler.isOrientationStreamNull()) {// && !lowAccuracy[currentDevice.indexOf
                    //(event.sensor)]) {
                    // Convert the rotation-vector to a 4x4 matrix.
                    float[] mRotationMatrix = new float[16];
                   // float[] mRotationMatrixFromVector = new float[16];
                    SensorManager.getRotationMatrixFromVector(mRotationMatrix,event.values);
                   // SensorManager.remapCoordinateSystem(mRotationMatrixFromVector,SensorManager.AXIS_X, SensorManager.AXIS_Z,
                   //                 mRotationMatrix);

                    float[] orientationVals = new float[3];
                    SensorManager.getOrientation(mRotationMatrix, orientationVals);

                    // Optionally convert the result from radians to degrees
                    orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
                    orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
                    orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

                    //String add = System.currentTimeMillis() + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    String add = System.currentTimeMillis() + "\t" + orientationVals[0] + "\t" + orientationVals[1] + "\t" + orientationVals[2] + "\n";
                    try {
                        fileHandler.writeOrientation(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Depreciated Orientation record fail; queue full", Toast.LENGTH_SHORT).show();
                    }
                }
                if (event.sensor == mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) && !fileHandler.isMagneticStreamNull()) {
                    String add = System.currentTimeMillis() + "\t" + event.values[0] + "\t" + event.values[1] + "\t" + event.values[2] + "\n";
                    mGeomagnetic = event.values;
                    try {
                        fileHandler.writeMagnetic(add);
                    } catch (IOException e) {
                        Toast.makeText(mContext, "Magnetometer record fail; queue full", Toast.LENGTH_SHORT).show();
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

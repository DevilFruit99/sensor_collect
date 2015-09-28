package com.example.susha.sensor_collect;

import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Date;


/**
 * Created by susha on 9/25/2015.
 */
public class FileManagement {

    private File gyroscope;
    private File magnetic;
    private File accelerometer;
    private File orientation;
    private File gravity;

    private File inertiafile;
    private File wififile;
    private File visualfile;
    private Uri fileUri;

    private Date datestamp;
    private Date audioDateStamp;

    private static FileManagement fmInstance = null;
    protected FileManagement(){
        //Singleton class exists only to defeat instantiation
        //Check public apps directory if directory is made
        //if not make an app directory called sensor_collect

    }
    public static FileManagement getFmInstance(){
        if(fmInstance==null){
            fmInstance = new FileManagement();
        }
        return fmInstance;
    }


}

package com.example.susha.sensor_collect.GUI;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.susha.sensor_collect.R;

import java.io.IOException;

/**
 * Created by linti on 3/20/2016.
 */
public class RecordingScreenGUI {
    public Activity activity;
    private Button stopRecord;
    private Button openCamera;

    public RecordingScreenGUI(Activity activity){
        this.activity = activity;
        //GUI setup
        stopRecord = (Button) this.activity.findViewById(R.id.finish_recording);
        openCamera = (Button) this.activity.findViewById(R.id.open_camera);
    }

    public Button getOpenCamera() {
        return openCamera;
    }
    public Button getStopRecord() {
        return stopRecord;
    }

}

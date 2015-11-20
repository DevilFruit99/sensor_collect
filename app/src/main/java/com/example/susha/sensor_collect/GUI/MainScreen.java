package com.example.susha.sensor_collect.GUI;

import android.app.Activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.example.susha.sensor_collect.R;

/**
 * Created by Dardan on 11/8/2015.
 */
public class MainScreen{

    public Activity activity;

    private EditText input;
    private TextView output;
    private TextView output2;
    private TextView textPictureCount;
    private Switch toggle;
    private Switch switchGPS;
    private Switch switchWifi;
    private Switch switchVisual;
    private Switch switchGyroscope;
    private Switch switchMagnetic;
    private Switch switchAccelerometer;
    private Switch switchOrientation;
    private Switch switchGravity;
    private Button pulse;
    private Button camera;
    private Button video;

    private View mLayout;

    public MainScreen(Activity activity){
        this.activity = activity;
        initGUI();
    }

    private void initGUI(){

        //GUI setup
        input = (EditText) this.activity.findViewById(R.id.editText);
        output = (TextView) this.activity.findViewById(R.id.textView);
        toggle = (Switch) this.activity.findViewById(R.id.switch1);
        pulse = (Button) this.activity.findViewById(R.id.button);
        camera = (Button) this.activity.findViewById(R.id.button2);
        video = (Button) this.activity.findViewById(R.id.button3);
        output2 = (TextView) this.activity.findViewById(R.id.textView2);
        textPictureCount = (TextView) this.activity.findViewById(R.id.textPictureCount);

        // Setup toggle switches
        switchGPS = (Switch) this.activity.findViewById(R.id.switchGPS);
        switchWifi = (Switch) this.activity.findViewById(R.id.switchWifi);
        switchVisual = (Switch) this.activity.findViewById(R.id.switchVisual);
        switchGyroscope = (Switch) this.activity.findViewById(R.id.switchGyroscope);
        switchMagnetic = (Switch) this.activity.findViewById(R.id.switchMagnetic);
        switchAccelerometer = (Switch) this.activity.findViewById(R.id.switchAccelerometer);
        switchOrientation = (Switch) this.activity.findViewById(R.id.switchOrientation);
        switchGravity = (Switch) this.activity.findViewById(R.id.switchGravity);

        //Constant initialization
        input.setHint("Session name");
        toggle.setTextOff("Start");
        toggle.setTextOn("Recording");
        textPictureCount.setText("# of pictures taken: 0");

        // Initialize switches text.
        switchGPS.setTextOff("Disabled");
        switchGPS.setTextOn("Enabled");
        switchGPS.setChecked(true);
        switchWifi.setTextOff("Disabled");
        switchWifi.setTextOn("Enabled");
        switchWifi.setChecked(true);
        switchVisual.setTextOff("Disabled");
        switchVisual.setTextOn("Enabled");
        switchVisual.setChecked(true);
        switchGyroscope.setTextOff("Disabled");
        switchGyroscope.setTextOn("Enabled");
        switchGyroscope.setChecked(true);
        switchMagnetic.setTextOff("Disabled");
        switchMagnetic.setTextOn("Enabled");
        switchMagnetic.setChecked(true);
        switchAccelerometer.setTextOff("Disabled");
        switchAccelerometer.setTextOn("Enabled");
        switchAccelerometer.setChecked(true);
        switchOrientation.setTextOff("Disabled");
        switchOrientation.setTextOn("Enabled");
        switchOrientation.setChecked(true);
        switchGravity.setTextOff("Disabled");
        switchGravity.setTextOn("Enabled");
        switchGravity.setChecked(true);
    }

    public void disableSwitches(){
        // Disable the switches after recording.
        switchWifi.setEnabled(false);
        switchVisual.setEnabled(false);
        switchGyroscope.setEnabled(false);
        switchMagnetic.setEnabled(false);
        switchAccelerometer.setEnabled(false);
        switchOrientation.setEnabled(false);
        switchGravity.setEnabled(false);
        switchGPS.setEnabled(false);
    }

    public void resetGUI(){
        //input.setFocusable(true);
        input.setText("");
        input.setHint("New session name...");
        input.requestFocus();
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        //add code to upload to server
        //show progress dialog
        output2.setText("");
        textPictureCount.setText("# of pictures taken: 0");

        // Enable the switches after recording.
        switchWifi.setEnabled(true);
        switchVisual.setEnabled(true);
        switchGyroscope.setEnabled(true);
        switchMagnetic.setEnabled(true);
        switchAccelerometer.setEnabled(true);
        switchOrientation.setEnabled(true);
        switchGravity.setEnabled(true);
        switchGPS.setEnabled(true);
        camera.setEnabled(false);
    }
    public void disablePulse(){
        pulse.setEnabled(false);
    }

    public void disableCamera(){
        camera.setEnabled(false);
    }

    public void disableVideo(){
        video.setEnabled(false);
    }

    public void setToggleText(String text){
        toggle.setTextOn(text);
    }

    public void setOutputText(String output) {
        this.output.setText(output);
    }

    public boolean getSwitchVisualStatus() {
        return switchVisual.isChecked();
    }

    public boolean getSwitchGPSStatus() {
        return switchGPS.isChecked();
    }

    public boolean getSwitchGyroscopeStatus() {
        return switchGyroscope.isChecked();
    }

    public boolean getSwitchMagneticStatus() {
        return switchMagnetic.isChecked();
    }

    public boolean getSwitchAccelerometerStatus() {
        return switchAccelerometer.isChecked();
    }

    public boolean getSwitchOrientationStatus() {
        return switchOrientation.isChecked();
    }

    public boolean getSwitchGravityStatus() {
        return switchGravity.isChecked();
    }

    public Button getPulse() {
        return pulse;
    }

    public Button getCamera() {
        return camera;
    }

    public Button getVideo() {
        return video;
    }

    public boolean getSwitchWifiStatus() {
        return switchWifi.isChecked();
    }

    public TextView getTextPictureCount() {
        return textPictureCount;
    }

    public String getInputText() {
        return input.getText().toString();
    }

    public void setOutput2Text(String text) {
        output2.setText(text);
    }

    public Switch getToggle() {
        return toggle;
    }
}

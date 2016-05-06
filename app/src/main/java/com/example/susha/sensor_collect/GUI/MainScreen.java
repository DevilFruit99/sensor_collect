package com.example.susha.sensor_collect.GUI;

import android.app.Activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    private Button buttonRecord;
    private SharedPreferences SP;

    private View mLayout;

    public MainScreen(Activity activity){
        this.activity = activity;
        SP = PreferenceManager.getDefaultSharedPreferences(activity);
        initGUI();
    }

    private void initGUI(){

        //GUI setup
        input = (EditText) this.activity.findViewById(R.id.editText);
        output = (TextView) this.activity.findViewById(R.id.textView);
        buttonRecord = (Button) this.activity.findViewById(R.id.switch1);
        output2 = (TextView) this.activity.findViewById(R.id.textView2);


        //Constant initialization
        input.setHint("Session name");

    }


    public void initSP(){
        boolean test = SP.getBoolean("switchWifi",true);
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
    }
    public void setOutputText(String output) {
        this.output.setText(output);
    }

    public boolean getSwitchVisualStatus() {
        return SP.getBoolean("switchVisual", true);
    }

    public boolean getSwitchGPSStatus() {
        return SP.getBoolean("switchGPS", true);
    }

    public boolean getSwitchGyroscopeStatus() {
        return SP.getBoolean("switchGyroscope", true);
    }

    public boolean getSwitchMagneticStatus() {
        return SP.getBoolean("switchMagnetic", true);
    }

    public boolean getSwitchAccelerometerStatus() {
        return SP.getBoolean("switchAccelerometer",true);
    }

    public boolean getSwitchOrientationStatus() {
        return SP.getBoolean("switchOrientation", true);
    }

    public boolean getSwitchGravityStatus() {
        return SP.getBoolean("switchGravity",true);
    }

    public boolean getSwitchWifiStatus() {
        return SP.getBoolean("switchWifi",true);
    }

    public String getInputText() {
        return input.getText().toString();
    }

    public void setOutput2Text(String text) {
        output2.setText(text);
    }

    public Button getButtonRecord() {
        return buttonRecord;
    }
}

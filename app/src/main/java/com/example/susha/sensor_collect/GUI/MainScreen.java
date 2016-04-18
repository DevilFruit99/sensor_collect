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
    private TextView textPictureCount;
    private Button buttonRecord;
    private Button pulse;
    private Button camera;
    private Button video;
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
        pulse = (Button) this.activity.findViewById(R.id.button);
        camera = (Button) this.activity.findViewById(R.id.button2);
        video = (Button) this.activity.findViewById(R.id.button3);
        output2 = (TextView) this.activity.findViewById(R.id.textView2);
        textPictureCount = (TextView) this.activity.findViewById(R.id.textPictureCount);


        //Constant initialization
        input.setHint("Session name");
        textPictureCount.setText("# of pictures taken: 0");

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
        textPictureCount.setText("# of pictures taken: 0");

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
        return SP.getBoolean("switchWifi",true);
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

    public Button getButtonRecord() {
        return buttonRecord;
    }
}

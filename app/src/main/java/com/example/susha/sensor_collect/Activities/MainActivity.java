package com.example.susha.sensor_collect.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioRecord;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.susha.sensor_collect.FileHandler.FileHandler;
import com.example.susha.sensor_collect.GUI.MainScreen;
import com.example.susha.sensor_collect.GUI.Preferences;
import com.example.susha.sensor_collect.R;
import com.example.susha.sensor_collect.Sensors.LogRunnable;
import com.example.susha.sensor_collect.Sensors.MyLocationListener;
import com.example.susha.sensor_collect.Sensors.WifiScan;
import com.example.susha.sensor_collect.Server.AsyncUpload;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    List<ScanResult> AP = new ArrayList<ScanResult>();
    AudioRecord record;
    private WifiManager wifiManager;
    private WifiManager.WifiLock scan;
    private Thread myThread;
    private Thread recordingThread;
    private LogRunnable myLogRunnable;
    private Uri fileUri;
    private Date datestamp;
    private Date audioDateStamp;
    private DataOutputStream dataOutputStream;
    private boolean run;
    private boolean isRecording;
    private String tone;
    private String visualpath;
    private String appDirName = "BluePrint";
    public static File SessionDir;
    private FileHandler fileHandler;
    private static Timer Wifitimer;
    private SharedPreferences SP;
    //private static TimerTask doAsynchronousTask;

    public int testcount;

    //textPictureCount field required for onActivityResult. Temp workaround
    private TextView textPictureCount;
    static final int FINISHED_RECORDING_REQUEST = 1;  // The request code for recording screen
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    //final long minGPSTime = 10*1000; //1000 miliseconds, 10 muiltiplier = 10 seconds
    private int wifiScanRate;
    private int sensorScanRate;

    private LocationListener mlocListener;
    private LocationManager mlocManager;
    private static final int REQUEST_LOC=0;
    //TODO Once sensorCollectGUI is a singleton, this variable is no longer needed and instead can be recreated in the onActivityResult method
    MainScreen sensorCollectGUI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        testcount = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SP = PreferenceManager.getDefaultSharedPreferences(this);

        //Initialize GUI object
        sensorCollectGUI = new MainScreen(this);

        //Init fileHandle
        fileHandler = new FileHandler(MainActivity.this);

        //saves output file location. /data/data/com.example.susha.sensor_collect/files
        sensorCollectGUI.setOutputText(getBaseContext().getFilesDir().toString());

        sensorCollectGUI.disablePulse();
        sensorCollectGUI.disableCamera();
        sensorCollectGUI.disableVideo();

        final String input = sensorCollectGUI.getInputText();
        textPictureCount = sensorCollectGUI.getTextPictureCount();
        //WiFi set up
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        scan = wifiManager.createWifiLock("expScan");
        tone = "test1.mp3";

        //begin begin recording
        sensorCollectGUI.getButtonRecord().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                //Get date for naming file
                    String summaryFileText = "";
                    String sessionDirectoryString = "";
                    //Data file creation
                    try {
                        wifiScanRate = SP.getInt("SEEKBAR_VALUE_WIFI", 20000);
                        sensorScanRate = SP.getInt("SEEKBAR_VALUE_SENSOR", 20000);

                        //app folder is setup
                        File ProjectDir = new File(Environment.getExternalStorageDirectory() + File.separator + appDirName);

                        if (!ProjectDir.exists()) {
                            ProjectDir.mkdir();
                        }
                        Calendar calNow = Calendar.getInstance();
                        Date current = new Date();
                        calNow.setTimeInMillis(current.getTime());
                        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                        String sessionName = (calNow.get(Calendar.MONTH) + 1) + "_" +
                                calNow.get(Calendar.DATE) + "_" + calNow.get(Calendar.YEAR) + "_" +
                                calNow.get(Calendar.HOUR_OF_DAY) + "-" + calNow.get(Calendar.MINUTE) +
                                "-" + calNow.get(Calendar.SECOND) + "(" + tm.getDeviceId() + ")";
                        sessionDirectoryString = ProjectDir + File.separator + sessionName;
                        SessionDir = new File(sessionDirectoryString);
                        if (!SessionDir.exists()) {
                            SessionDir.mkdir();
                        }
                        //run = false;

                        // Set streams to null
                        fileHandler.setStreamsNull();

                        // Check switches
                        if (sensorCollectGUI.getSwitchWifiStatus()) {
                            fileHandler.createWifi(SessionDir + File.separator + "wifi.txt");
                        }
                        if (sensorCollectGUI.getSwitchVisualStatus()) {
                            fileHandler.createVisual(SessionDir + File.separator + "visual.txt");
                            sensorCollectGUI.getCamera().setEnabled(true);
                        }
                        if (sensorCollectGUI.getSwitchGyroscopeStatus()) {
                            fileHandler.createGyro(SessionDir + File.separator + input + "gyroscope.txt");
                        }
                        if (sensorCollectGUI.getSwitchMagneticStatus()) {
                            fileHandler.createMagnetic(SessionDir + File.separator + input + "magnetic.txt");
                        }
                        if (sensorCollectGUI.getSwitchAccelerometerStatus()) {
                            fileHandler.createAccelerometer(SessionDir + File.separator + input + "accelerometer.txt");
                        }
                        if (sensorCollectGUI.getSwitchOrientationStatus()) {
                            fileHandler.createOrientation(SessionDir + File.separator + input + "orientation.txt");
                        }
                        if (sensorCollectGUI.getSwitchGravityStatus()) {
                            fileHandler.createGravity(SessionDir + File.separator + input + "gravity.txt");
                        }

                        /* Use the LocationManager class to obtain GPS locations */
                        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        mlocListener = new MyLocationListener(MainActivity.this, sensorCollectGUI.getSwitchGPSStatus());
                        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, SP.getInt("SEEKBAR_VALUE_GPS", 20000), 0, mlocListener);
                        if (sensorCollectGUI.getSwitchGPSStatus()) {
                            fileHandler.createGPS(SessionDir + File.separator + "gps.txt");
                        }

                        visualpath = SessionDir.getAbsolutePath();

                        //Make summary file
                        fileHandler.createSummary(SessionDir + File.separator + "Summary file.txt");
                        try {
                            Location location = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            location.getLatitude();
                            fileHandler.fillSummaryWithGPS(location.getLatitude(), location.getLongitude());
                        } catch (NullPointerException e) {
                            fileHandler.fillSummaryWithoutGPS();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startRecord(sensorCollectGUI.getSwitchWifiStatus());
                    sensorCollectGUI.setOutput2Text("Recording data...");



                    Intent recordScreenIntent = new Intent(MainActivity.this, RecordingScreen.class);
                    recordScreenIntent.putExtra("sessionDirectoryString",sessionDirectoryString);
                    /*
                    Create a new intent and switch activities
                    */
                    startActivityForResult(recordScreenIntent,FINISHED_RECORDING_REQUEST);
            }
        });


        //picture set up
        sensorCollectGUI.getCamera().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Create intent to capture image

                /*fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);//name camera file
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
*/
                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

              /*  try {
                    //fileHandler.visualStreamWrite(Long.toString(datestamp.getTime()) + " \t" + visualpath);
                    *//*Toast.makeText(getBaseContext(), Long.toString(datestamp.getTime()) + " \t" + visualpath, Toast.LENGTH_SHORT).show();*//*

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        //ultrasonic mp3 setup; spawn new thread
        sensorCollectGUI.getPulse().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        //mpwork();
                    }
                }).start();
            }
        });

        //video capture set up
        sensorCollectGUI.getVideo().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                try {
                    fileHandler.visualStreamWrite(Long.toString(datestamp.getTime()) + " \t" + visualpath);
                    /*Toast.makeText(getBaseContext(), Long.toString(datestamp.getTime()) + " \t" + visualpath, Toast.LENGTH_SHORT).show();*/
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }





    private void makeToast(String toastText) {
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }


    //Recording thread (spawns child wifi recording thread and inertia recording thread)
    private void startRecord(boolean SwitchStatus) {
        //check wifi switch status
        if(SwitchStatus){
            final Handler handler = new Handler();
            Wifitimer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                //Call async task to collect data
                                new WifiScan(fileHandler,getBaseContext()).execute();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                            }
                        }
                    });
                }
            };
            Wifitimer.schedule(doAsynchronousTask, 0, wifiScanRate); //execute in every 5000 ms
        }


        boolean cheat[] = new boolean[1];
        cheat[0] = run;
        myLogRunnable = new LogRunnable(MainActivity.this,fileHandler, cheat, sensorScanRate);
        myThread= new Thread(myLogRunnable);
        myThread.start();
    }


    @Override
    //Handling camera/camcorder data
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
               /* Toast.makeText(this, "Image saved to:\n" +
                        visualpath, Toast.LENGTH_LONG).show();*/
                try {
                    fileHandler.visualStreamWrite(/*Long.toString(datestamp.getTime())*/"test" + "\t" + visualpath + "\n");

                    // Append pic count
                    // Trying to avoid global variables, so instead read the string and append
                    CharSequence temp = textPictureCount.getText();
                    int count = Character.getNumericValue(textPictureCount.getText().charAt(temp.length()-1));
                    count++;
                    String updatedText = temp.subSequence(0,temp.length()-1).toString() + Integer.toString(count);
                    textPictureCount.setText(updatedText);

                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    Bitmap bp = (Bitmap) data.getExtras().get("data");

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        visualpath, Toast.LENGTH_LONG).show();
                try {
                    fileHandler.visualStreamWrite(Long.toString(datestamp.getTime()) + "\t" + visualpath + "\n");
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }

        if (requestCode == FINISHED_RECORDING_REQUEST){
            myLogRunnable.cleanThread();
            //Stop timer on async wifi scan
            // TODO Check switch
            if (sensorCollectGUI.getSwitchWifiStatus()) {
                Wifitimer.cancel();
                Wifitimer.purge();
                Wifitimer = null;
            }
            String confirmMsg = "";
            final AsyncUpload task = new AsyncUpload(this);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            confirmMsg ="Do you want to upload this recording?";

            alertDialogBuilder.setMessage(confirmMsg);
            //yes button
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    task.execute(SessionDir);

                }
            });

            //no button
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(RESULT_CANCELED);

                }
            });


            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            sensorCollectGUI.resetGUI();
            //Update contents of files for MTP connection
            fileHandler.invokeMediaScanner();
            //Remove GPS service
            mlocManager.removeUpdates(mlocListener);
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Init preference screen
            Intent i = new Intent(this, Preferences.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(SessionDir.getPath(), "Pictures");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Pictures", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        datestamp = new Date();
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(datestamp);
        Date current = new Date();
        File mediaFile;
        if (type == 1)
            visualpath = mediaStorageDir + File.separator +
                    System.currentTimeMillis() + ".jpg";
        if (type == 2)
            visualpath = mediaStorageDir + File.separator +
                    current.getTime() + ".mp4";
        if (type == MEDIA_TYPE_IMAGE || type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(visualpath);
        } else {
            return null;
        }

        return mediaFile;
    }
}

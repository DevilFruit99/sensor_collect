package com.example.susha.sensor_collect.MainActivity;

import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.Snackbar;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.SensorManager;
import android.os.AsyncTask;

import com.example.susha.sensor_collect.FileHandler.FIleHandler;
import com.example.susha.sensor_collect.GUI.MainScreen;
import com.example.susha.sensor_collect.LogRunnable;
import com.example.susha.sensor_collect.MyLocationListener;
import com.example.susha.sensor_collect.R;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
    private FIleHandler fileHandler;

    //textPictureCount field required for onActivityResult. Temp workaround
    private TextView textPictureCount;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    final long minGPSTime = 10*1000; //1000 miliseconds, 10 muiltiplier = 10 seconds

    private LocationListener mlocListener;
    private LocationManager mlocManager;
    private static final int REQUEST_LOC=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize GUI object
        final MainScreen sensorCollectGUI = new MainScreen(this);

        //Init fileHandle
        fileHandler = new FIleHandler(MainActivity.this);

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
        sensorCollectGUI.getToggle().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                //Get date for naming file

                //output.setText(Environment.getExternalStoragePublicDirectory("Senior Design").toString());

                sensorCollectGUI.setToggleText("Recording");
                run = isChecked;
                if (isChecked) {
                    String summaryFileText = "";
                    //Data file creation
                    try {

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
                        SessionDir = new File(ProjectDir + File.separator + sessionName);
                        if (!SessionDir.exists()) {
                            SessionDir.mkdir();
                        }
                        run = false;

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

                        //GPS ENABLE SWITCH NOT IMPLEMENTED. REPLACE TRUE WITH METHOD
                        // PLACE THESE IN CONTENTS
                        /* Use the LocationManager class to obtain GPS locations */
                        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                        mlocListener = new MyLocationListener(MainActivity.this);
                        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minGPSTime, 0, mlocListener);
                        if (true) {
                            fileHandler.createGPS(SessionDir + File.separator + "gps.txt");
                        }

                        visualpath = SessionDir.getAbsolutePath();

                        // Disable the switches after recording.
                        sensorCollectGUI.disableSwitches();

                        //Make summary file
                        fileHandler.createSummary(SessionDir + File.separator + "Summary file.txt");
                        try {
                            Location location = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            location.getLatitude();
                            fileHandler.fillSummaryWithGPS(location.getLatitude(), location.getLongitude());
                        }
                        catch (NullPointerException e){
                            fileHandler.fillSummaryWithoutGPS();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //input.setFocusable(false);
                    //input.setFocusable(false);
                    //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(input.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                    startRecord(sensorCollectGUI.getSwitchWifiStatus());
                    sensorCollectGUI.setOutput2Text("Recording data...");
                } else {
                    myLogRunnable.cleanThread();
                    sensorCollectGUI.resetGUI();
                    //Update contents of files for MTP connection
                    fileHandler.invokeMediaScanner();

                    //Remove GPS service
                    mlocManager.removeUpdates(mlocListener);

                }
            }
        });

        //picture set up
        sensorCollectGUI.getCamera().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Create intent to capture image

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                try {
                    fileHandler.visualStreamWrite(Long.toString(datestamp.getTime()) + " \t" + visualpath);
                    Toast.makeText(getBaseContext(), Long.toString(datestamp.getTime()) + " \t" + visualpath, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
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
                    Toast.makeText(getBaseContext(), Long.toString(datestamp.getTime()) + " \t" + visualpath, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Recording thread (spawns child wifi recording thread and inertia recording thread)
    private void startRecord(boolean SwitchStatus) {

        if(SwitchStatus) {
            new Thread(new Runnable() {
                public void run() {
                    while (run) {
                    }
                    wifiScan();
                }
            }).start();
        }
        boolean cheat[] = new boolean[1];
        cheat[0] = run;
        //new Thread(new LogRunnable(MainActivity.this, inertiaofstream, cheat)).start();
        myLogRunnable = new LogRunnable(MainActivity.this,fileHandler, cheat);
        myThread= new Thread(myLogRunnable);
        myThread.start();
    }


    @Override
    //Handling camera/camcorder data
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        visualpath, Toast.LENGTH_LONG).show();
                try {
                    fileHandler.visualStreamWrite(Long.toString(datestamp.getTime()) + "\t" + visualpath + "\n");

                    // Append pic count
                    // Trying to avoid global variables, so instead read the string and append
                    CharSequence temp = textPictureCount.getText();
                    int count = Character.getNumericValue(textPictureCount.getText().charAt(temp.length()-1));
                    count++;
                    String updatedText = temp.subSequence(0,temp.length()-1).toString() + Integer.toString(count);
                    textPictureCount.setText(updatedText);

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
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
    }


    //Ultrasound thread/method
    private void mpwork() {

        audioDateStamp = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(datestamp);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(appDirName), timeStamp + ".pcm");
        try {
            fileHandler.visualStreamWrite(Long.toString(datestamp.getTime()) + "\t" + outputFile.getPath() + "\n");
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
        }
        if (outputFile.exists())
            outputFile.delete();
        try {
            outputFile.createNewFile();
            OutputStream outputStream = new FileOutputStream(outputFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            dataOutputStream = new DataOutputStream(bufferedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int minBufferSize = AudioRecord.getMinBufferSize(44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

        record.startRecording(); //Use AudioRecord class to record full audio data
        isRecording = true;
        //Use thread to stream input audio to file
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();

        //Use mediaplayer to play 20khz asset
        MediaPlayer mp = new MediaPlayer();
        try {
            AssetManager manager = getBaseContext().getAssets();
            AssetFileDescriptor descriptor = manager.openFd(tone);
            mp.setDataSource(descriptor.getFileDescriptor());
            mp.setLooping(false);
            mp.prepare();
            mp.start();
            Toast.makeText(getBaseContext(), "MP START", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                try {
                    isRecording = false;
                    record.stop();
                    record.release();
                    dataOutputStream.close();
                    record = null;
                    recordingThread = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mp.release();
                Toast.makeText(getBaseContext(), "MP RELEASE", Toast.LENGTH_SHORT).show();
            }
        });

    }


    //helper method to record audio data
    private void writeAudioDataToFile() {
        // Write the output audio in byte
        int minBufferSize = AudioRecord.getMinBufferSize(11025,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        short[] audioData = new short[minBufferSize];

        try {
            while (isRecording) {
                int numberOfShort = record.read(audioData, 0, minBufferSize);
                for (int i = 0; i < numberOfShort; i++) {
                    dataOutputStream.writeShort(audioData[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //helper method to perform wifi scan
    public final void wifiScan() {

        scan.acquire(); //Acquire wifi lock
        AP = wifiManager.getScanResults();  //scan vicinity
        ScanResult[] event = new ScanResult[3]; //create record to store largest 3 signals
        if (!AP.isEmpty())
            for (int i = 0; i < 3 && !AP.isEmpty(); i++) {
                ScanResult temp = AP.get(0);
                for (ScanResult largest : AP) {
                    if (largest.level > temp.level) {
                        temp = largest;
                    }
                }
                event[i] = temp;
                AP.remove(temp);
            }
        //Record in file
        Date current = new Date();
        String add = Long.toString(current.getTime()) + "WiFi \t" + event[0] + "\t" + event[1] + "\t" + event[2] + "\n";
        try {
            fileHandler.wifiStreamWrite(add);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "WiFi record fail; queue full", Toast.LENGTH_SHORT).show();
        }
        scan.release();
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(datestamp);
        File mediaFile;
        if (type == 1)
            visualpath = mediaStorageDir + File.separator +
                    "IMG_" + timeStamp + ".jpg";
        if (type == 2)
            visualpath = mediaStorageDir + File.separator +
                    "IMG_" + timeStamp + ".mp4";
        if (type == MEDIA_TYPE_IMAGE || type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(visualpath);
        } else {
            return null;
        }

        return mediaFile;
    }
}

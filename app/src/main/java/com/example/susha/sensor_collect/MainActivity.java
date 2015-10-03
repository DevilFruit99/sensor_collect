package com.example.susha.sensor_collect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.hardware.SensorEventListener;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


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

    private EditText input;
    private TextView output;
    private TextView output2;
    private Switch toggle;
    private Button pulse;
    private Button camera;
    private Button video;


    List<ScanResult> AP = new ArrayList<ScanResult>();
    AudioRecord record;
    private WifiManager wifiManager;
    private WifiManager.WifiLock scan;

    private File gyroscope;
    private File magnetic;
    private File accelerometer;
    private File orientation;
    private File gravity;
    private BufferedWriter gyroscopeofstream;
    private BufferedWriter magneticofstream;
    private BufferedWriter accelerometerofstream;
    private BufferedWriter orientationofstream;
    private BufferedWriter gravityofstream;

    private BufferedWriter inertiaofstream;
    private BufferedWriter wifiofstream;
    private BufferedWriter visualofstream;
    private File inertiafile;
    private File wififile;
    private File visualfile;
    private Uri fileUri;
    private Date datestamp;
    private Date audioDateStamp;
    private DataOutputStream dataOutputStream;
    private boolean run;
    private boolean isRecording;
    private Thread recordingThread;
    private String tone;
    private String visualpath;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GUI setup
        input = (EditText) findViewById(R.id.editText);
        output = (TextView) findViewById(R.id.textView);
        toggle = (Switch) findViewById(R.id.switch1);
        pulse = (Button) findViewById(R.id.button);
        camera = (Button) findViewById(R.id.button2);
        video = (Button) findViewById(R.id.button3);
        output2 = (TextView) findViewById(R.id.textView2);


        //Constant initialization
        output.setText(getBaseContext().getFilesDir().toString());//saves output file location. /data/data/com.example.susha.sensor_collect/files
        //Environment.getExternalStoragePublicDirectory("Senior Design").toString());
        input.setHint("Session name");
        toggle.setTextOff("Start");
        toggle.setTextOn("Recording");

        pulse.setEnabled(false);
        camera.setEnabled(false);
        video.setEnabled(false);

        //WiFi set up
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        scan = wifiManager.createWifiLock("expScan");
        tone = "test1.mp3";





        //begin begin recording
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                //Get date for naming file


                //output.setText(Environment.getExternalStoragePublicDirectory("Senior Design").toString());

                toggle.setTextOn("Recording");
                run = isChecked;
                if (isChecked) {
                    //Data file creation
                    try {

                        //app folder is setup
                        File ProjectDir = new File(Environment.getExternalStorageDirectory()+File.separator+"SeniorDesign");

                        if(!ProjectDir.exists()){
                            ProjectDir.mkdir();
                        }
                        Calendar calNow = Calendar.getInstance();
                        Date current=new Date();
                        calNow.setTimeInMillis(current.getTime());
                        String sessionName = "BluePrint_"+calNow.get(Calendar.MONTH)+"_"+
                                calNow.get(Calendar.DATE)+"_"+calNow.get(Calendar.YEAR)+"_"+
                                calNow.get(Calendar.HOUR)+":"+calNow.get(Calendar.MINUTE)+
                                ":"+calNow.get(Calendar.SECOND);
                        File SessionDir =  new File(ProjectDir+File.separator+ sessionName);
                        if(!SessionDir.exists()){
                            SessionDir.mkdir();
                        }
                        run = false;
                        inertiafile = new File(SessionDir+File.separator+ "inertia.txt");
                        wififile = new File(SessionDir+File.separator+ "wifi.txt");
                        visualfile = new File(SessionDir+File.separator+ "visual.txt");
                        gyroscope = new File(SessionDir+File.separator+ input.getText() + "gyroscope.txt");
                        magnetic = new File(SessionDir+File.separator+ input.getText() + "magnetic.txt");
                        accelerometer = new File(SessionDir+File.separator+ input.getText() + "accelerometer.txt");
                        orientation = new File(SessionDir+File.separator+ input.getText() + "orientation.txt");
                        gravity = new File(SessionDir+File.separator+ input.getText() + "gravity.txt");

                       // inertiafile = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), "inertia.txt");
                       // wififile = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), "wifi.txt");
                       // visualfile = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), "visual.txt");
//
                       // gyroscope = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), input.getText() + "gyroscope.txt");
                       // magnetic = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), input.getText() + "magnetic.txt");
                       // accelerometer = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), input.getText() + "accelerometer.txt");
                       // orientation = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), input.getText() + "orientation.txt");
                       // gravity = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), input.getText() + "gravity.txt");
                        gyroscopeofstream = new BufferedWriter(new FileWriter(gyroscope)); //program crashes here
                        magneticofstream = new BufferedWriter(new FileWriter(magnetic));
                        accelerometerofstream = new BufferedWriter(new FileWriter(accelerometer));
                        orientationofstream = new BufferedWriter(new FileWriter(orientation));
                        gravityofstream = new BufferedWriter(new FileWriter(gravity));

                        inertiaofstream = new BufferedWriter(new FileWriter(inertiafile));
                        wifiofstream = new BufferedWriter(new FileWriter(wififile));
                        visualofstream = new BufferedWriter(new FileWriter(visualfile));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //input.setFocusable(false);
                    //input.setFocusable(false);
                    //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(input.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                    startRecord();
                    output2.setText("Recording data...");
                } else {
                    //input.setFocusable(true);
                    input.setText("");
                    input.setHint("New session name...");
                    input.requestFocus();
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    output2.setText("");
                }
                camera.setEnabled(isChecked);
            }
        });

        //picture set up
        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Create intent to capture image

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                try {
                    visualofstream.write(Long.toString(datestamp.getTime()) + " \t" + visualpath);
                    visualofstream.flush();
                    Toast.makeText(getBaseContext(), Long.toString(datestamp.getTime()) + " \t" + visualpath, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ultrasonic mp3 setup; spawn new thread
        pulse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        //mpwork();
                    }
                }).start();
            }
        });

        //video capture set up
        video.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                try {
                    visualofstream.write(Long.toString(datestamp.getTime()) + " \t" + visualpath);
                    visualofstream.flush();
                    Toast.makeText(getBaseContext(), Long.toString(datestamp.getTime()) + " \t" + visualpath, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Recording thread (spawns child wifi recording thread and inertia recording thread)
    private void startRecord() {
        new Thread(new Runnable() {
            public void run() {
                while (run) {
                }
                wifiScan();
            }
        }).start();
        boolean cheat[] = new boolean[1];
        cheat[0] = run;
        //new Thread(new LogRunnable(MainActivity.this, inertiaofstream, cheat)).start();
        new Thread(new LogRunnable(MainActivity.this, gyroscopeofstream,
                magneticofstream, accelerometerofstream, orientationofstream, gravityofstream, cheat)).start();
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
                    visualofstream.write(Long.toString(datestamp.getTime()) + "\t" + visualpath + "\n");
                    visualofstream.flush();
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
                    visualofstream.write(Long.toString(datestamp.getTime()) + "\t" + visualpath + "\n");
                    visualofstream.flush();
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
        File outputFile = new File(Environment.getExternalStoragePublicDirectory("Senior Design"), timeStamp + ".pcm");
        try {
            visualofstream.write(Long.toString(datestamp.getTime()) + "\t" + outputFile.getPath() + "\n");
            visualofstream.flush();
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
            wifiofstream.write(add);
            wifiofstream.flush();
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

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                "Senior Design"), "Pictures");

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

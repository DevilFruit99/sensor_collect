package com.example.susha.sensor_collect.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.susha.sensor_collect.FileHandler.FileHandler;
import com.example.susha.sensor_collect.GUI.CameraPreview;
import com.example.susha.sensor_collect.GUI.RecordingScreenGUI;
import com.example.susha.sensor_collect.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordingScreen extends Activity {
    ListView listView ;
    private Uri fileUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private FileHandler fileHandler;
    private String visualpath;
    private String picturePath;
    private Date datestamp;
    private Camera camera;
    private CameraPreview mPreview;
    long shutterTime;
    private static final String TAG = RecordingScreen.class.getName();

    String[] values = new String[] { "This is a temp wrapper",
            "Whether or not listview will be used is still undecided",
            "Simple List View In Android",
            "Just an example",
            "Android Example",
            "List View Source Code",
            "List View Array Adapter",
            "Android Example List View"
    };
    private Camera.ShutterCallback shutterCallBack = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            shutterTime = System.currentTimeMillis();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_screen);
        //Saturate the steps for the listview
        //TODO Listview is a temp wrapper

        //Initialize GUI object
        final RecordingScreenGUI recordingScreenGUI = new RecordingScreenGUI(this);

        //Init fileHandle
        fileHandler = new FileHandler(RecordingScreen.this);
        // Set streams to null
        fileHandler.setStreamsNull();
        //gps = new File(MainActivity.SessionDir + File.separator + "gps.txt");
        Intent intent = getIntent();
        visualpath = intent.getExtras().getString("sessionDirectoryString");
        try {
            fileHandler.createVisual(visualpath + File.separator + "visual.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get an instance of camera
        camera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listView);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });

        recordingScreenGUI.getOpenCamera().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //get an image from camera
                camera.takePicture(shutterCallBack, null,mPicture);
            }
        });

        Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
            public void onShutter() {
            }
        };

        recordingScreenGUI.getStopRecord().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });
    }

    /**
     * reset cam
     */
    private void resetCam() {
        camera.startPreview();
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, shutterTime);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                resetCam();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording_screen, menu);
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

    private File getOutputMediaFile(int type, long shutterTime) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this
        File mediaStorageDir = new File(visualpath + File.separator, "Pictures");

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
        Date timeStamp = new Date();
        File mediaFile;
        if (type == 1)
            picturePath = mediaStorageDir + File.separator +
                    shutterTime + ".jpg";
        if (type == 2)
            picturePath = mediaStorageDir + File.separator +
                    "IMG_" + timeStamp + ".mp4";
        if (type == MEDIA_TYPE_IMAGE || type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(picturePath);
        } else {
            return null;
        }

        return mediaFile;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

}


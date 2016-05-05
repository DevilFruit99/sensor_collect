package com.example.susha.sensor_collect.Activities;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

import com.example.susha.sensor_collect.FileHandler.FileHandler;
import com.example.susha.sensor_collect.GUI.RecordingScreenGUI;
import com.example.susha.sensor_collect.R;
import com.example.susha.sensor_collect.Server.FTPTransfer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordingScreen extends FragmentActivity implements OnMapReadyCallback{
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

    String[] values = new String[] { "This is a temp wrapper",
            "Whether or not listview will be used is still undecided",
            "Simple List View In Android",
            "Just an example",
            "Android Example",
            "List View Source Code",
            "List View Array Adapter",
            "Android Example List View"
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_screen);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(RecordingScreen.this);  // This calls OnMapReady(..). (Asynchronously)
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
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Create intent to capture image

                datestamp = new Date();
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                try {
                    fileHandler.visualStreamWrite(Long.toString(datestamp.getTime()) + " \t" + visualpath + "\n");
                    //Toast.makeText(getBaseContext(), Long.toString(datestamp.getTime()) + " \t" + visualpath, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Visual record fail; queue full", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recordingScreenGUI.getStopRecord().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void finishActivityDialog() {

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //make a function in the future to limit the bounds of the map by using a listener
        LatLngBounds SBU = new LatLngBounds(
                new LatLng(40.910522, -73.136147), new LatLng(40.922764, -73.115162));

        LatLng marker = new LatLng(40.915176, -73.123122);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.915176, -73.123122))
                .title("Default Marker"));
    }



    @Override
    //Handling camera/camcorder data
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        datestamp = new Date();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        visualpath, Toast.LENGTH_LONG).show();
                /*
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
                }*/
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
    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private File getOutputMediaFile(int type) {
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
                    timeStamp.getTime() + ".jpg";
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
}


package com.example.susha.sensor_collect.Activities;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.susha.sensor_collect.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class RecordingScreen extends FragmentActivity implements OnMapReadyCallback{
    ListView listView ;

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
}

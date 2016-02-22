package com.example.susha.sensor_collect.GUI;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.example.susha.sensor_collect.R;

/**
 * Created by linti on 11/29/2015.
 */
public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
        private SeekBarPreference _seekBarPrefSensor;
        private SeekBarPreference _seekBarPrefWifi;
        private SeekBarPreference _seekBarPrefGps;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);


            // Get widgets :
            _seekBarPrefSensor = (SeekBarPreference) this.findPreference("SEEKBAR_VALUE_SENSOR");
            _seekBarPrefWifi = (SeekBarPreference) this.findPreference("SEEKBAR_VALUE_WIFI");
            _seekBarPrefGps = (SeekBarPreference) this.findPreference("SEEKBAR_VALUE_GPS");

            // Set listener :
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            // Set sensor summary :
            int radius_sensor = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE_SENSOR", 20000);
            _seekBarPrefSensor.setSummary(this.getString(R.string.settings_summary_sensor).replace("$1", "" + radius_sensor));
            // Set wifi summary :
            int radius_wifi = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE_WIFI", 10000);
            _seekBarPrefWifi.setSummary(this.getString(R.string.settings_summary_wifi).replace("$1", "" + radius_wifi));
            // Set gps summary :
            int radius_gps = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE_GPS", 10000);
            _seekBarPrefGps.setSummary(this.getString(R.string.settings_summary_gps).replace("$1", "" + radius_gps));


        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            int test;
            //TODO replace "Current value is $1" with dynamic fetching of string from resources
            if(key.compareTo("SEEKBAR_VALUE_SENSOR")==0){
                test = sharedPreferences.getInt("SEEKBAR_VALUE_SENSOR", 20000);
                _seekBarPrefSensor.setSummary("Current value is $1".replace("$1", "" + test));
            }
            if(key.compareTo("SEEKBAR_VALUE_WIFI")==0){
                test = sharedPreferences.getInt("SEEKBAR_VALUE_WIFI", 10000);
                _seekBarPrefWifi.setSummary("Current value is $1".replace("$1", "" + test));
            }
            if(key.compareTo("SEEKBAR_VALUE_GPS")==0){
                test = sharedPreferences.getInt("SEEKBAR_VALUE_GPS", 10000);
                _seekBarPrefGps.setSummary("Current value is $1".replace("$1", "" + test));
            }
            /*
            // Set sensor summary :
            int radius_sensor = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE_SENSOR", 20000);
            _seekBarPrefSensor.setSummary(this.getString(R.string.settings_summary_sensor).replace("$1", "" + radius_sensor));
            // Set wifi summary :
            int radius_wifi = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE_WIFI", 10000);
            _seekBarPrefWifi.setSummary(this.getString(R.string.settings_summary_wifi).replace("$1", "" + radius_wifi));
            // Set gps summary :
            int radius_gps = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE_GPS", 10000);
            _seekBarPrefGps.setSummary(this.getString(R.string.settings_summary_gps).replace("$1", "" + radius_gps));*/
        }
    }
}



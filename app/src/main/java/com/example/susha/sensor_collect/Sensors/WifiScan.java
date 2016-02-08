package com.example.susha.sensor_collect.Sensors;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.example.susha.sensor_collect.FileHandler.FileHandler;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by linti on 1/29/2016.
 */
public class WifiScan extends AsyncTask<Void, Void, Void> {
    private FileHandler fileHandler;
    private final Context context;
    String wifis[];

    public WifiScan(FileHandler fh,Context cntxt){
        this.fileHandler = fh;
        this.context = cntxt;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //FileHandler fileHandler=params[0];
        WifiManager wifi;
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        //context.registerReceiver(new BroadcastReceiver() {
            //@Override
            //public void onReceive(Context context, Intent intent) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                boolean a = wifiManager.startScan();//request a scan for access points
                final List<ScanResult> results = wifiManager.getScanResults();//list of access points from the last scan

                String filtered[] = new String[results.size()];
                wifis = new String[results.size()];
                for(int i = 0; i < results.size(); i++){
                    wifis[i] = ((results.get(i)).toString());
                }
                int counter = 0;

                    Date current = new Date();

                    for (String eachWifi : wifis) {
                        String[] temp = eachWifi.split(",");
                        filtered[counter] = temp[0] +temp[1] +temp[4]+temp[5]+temp[6]  ;
                        //TODO calculate theoretical distance using frequency and level
                        //System.out.println("test \n");
                        counter++;
                    }

                 try {
                     fileHandler.wifiStreamWrite(Long.toString(current.getTime()) + "\n");
                     for (int i = 0;i<counter;i++) {
                         fileHandler.wifiStreamWrite(filtered[i] + "\n");
                     }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*for (final ScanResult result : results) {
                    System.out.println("ScanResult level: " + result.level);
                }*/
            //}
        //}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));




        return null;
    }
}


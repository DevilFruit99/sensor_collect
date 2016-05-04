package com.example.susha.sensor_collect.Server;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.susha.sensor_collect.Activities.MainActivity;

import java.io.File;

/**
 * Created by susha on 5/3/2016.
 */
public class AsyncUpload extends AsyncTask<File,Integer,Long> {

    Context context;
    long totalSize;
    public AsyncUpload(Activity activity) {
        context = activity.getBaseContext();
        dialog = new ProgressDialog(activity);
        alertDialogBuilder  = new AlertDialog.Builder(activity);
    }
    private ProgressDialog dialog;
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onPreExecute(){
        dialog.setMessage("Uploading files, please wait.");
        dialog.show();
    }

    @Override
    protected Long doInBackground(File... files) {
        int count = files.length;
        totalSize = 0;
        for (int i = 0; i < count; i++) {
            totalSize += new FTPTransfer(PreferenceManager.getDefaultSharedPreferences(context)).uploadFile(files[i],context);
            publishProgress((int) ((i / (float) count) * 100));
            //Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return totalSize;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        String confirmMsg = "";
        if (totalSize != 0) {//Upload successful
            confirmMsg = "Upload Successful";
        } else { //Upload unsuccessful
            confirmMsg = "Sorry could not connect to server, manually transfer files";

        }
        alertDialogBuilder.setMessage(confirmMsg);
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }});
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}


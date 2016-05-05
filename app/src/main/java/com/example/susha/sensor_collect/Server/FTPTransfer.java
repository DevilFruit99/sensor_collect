package com.example.susha.sensor_collect.Server;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

public class FTPTransfer {
    Session session = null;
    private String user="";
    private String host="";
    private String pass = "";
    private static int port=22;
    static SharedPreferences SP;
    public FTPTransfer(SharedPreferences SP){
         FTPTransfer.SP =SP;

    }
    public long uploadFile(File srcFile, Context context){
        long size=0;

        try {
            session = getSSH().getSession(getUser(),getHost(),port);
            // remove the hard coding and add UI screen to get credentials
            //Use this to prompt password
            session.setPassword(getPass());//hardcoded for testing

            // Avoid asking for key confirmation
            //TODO NOT SECURE! need to find better way

            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp c = (ChannelSftp)channel;

            //TODO remove. test to see if connection works
            Vector vv=c.ls("/home/Server3/Blueprint/");
            if(vv!=null){
                for(int i=0;i<vv.size();i++){
                    Log.i("sftp ls command",vv.elementAt(i).toString());
                }
            }

            Log.i("sftp", srcFile.getAbsolutePath());//TODO remove. just for testing
            //TODO create code for recreating dir
            c.cd("/home/Server3/Blueprint/data/Android");
            String dstFile="/home/Server3/Blueprint/data/Android/"+srcFile.getName()+"/";
            c.mkdir(srcFile.getName());
            c.cd(dstFile);
            for(File file: srcFile.listFiles()){
                if(file.isDirectory()){
                    c.mkdir(file.getName());
                    for(File innerFile: file.listFiles()){
                        c.put(innerFile.getAbsolutePath(),dstFile+"/"+file.getName(),ChannelSftp.OVERWRITE);

                    }
                    c.cd("..");
                    continue;
                }
                Log.i("sftp", "uploading " + file.getName());//TODO remove. just for testing
                c.put(file.getAbsolutePath(),dstFile,ChannelSftp.OVERWRITE);
                size++;
            }
            c.disconnect();
            //BAD IMPL: c.put(srcFile.getAbsolutePath(),"/home/Server3/Blueprint/Android");// TODO ideally would like to implement put(src,dest,monitor,mode);
            //mode can be OVERWRITE, RESUME, or APPEND
        } catch (JSchException e) {

            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }
        //maybe use scp to copy dir http://www.jcraft.com/jsch/examples/ScpTo.java.html

        return size;
    }

    public static String getUser(){

        return SP.getString("user", "defaultUser");
    }
    public static String getHost(){
        return SP.getString("host","defaultHost");
    }

    //TODO show dialog for password if entered incorrectly
    public static String getPass(){
        return SP.getString("pass","defaultPass");
    }


    private static JSch ssh;
    public static JSch getSSH() {
        return (ssh==null)?new JSch():ssh;
    }

}

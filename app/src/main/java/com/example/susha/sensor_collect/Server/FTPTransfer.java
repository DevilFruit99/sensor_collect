package com.example.susha.sensor_collect.Server;


import android.content.Context;
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
    String user="Server3";
    String host="130.245.191.166";
    int port=22;
    public FTPTransfer(){

    }
    public long uploadFile(File srcFile, Context context){
        long size=0;
        JSch ssh = new JSch();
        try {
            session = ssh.getSession(user,host,port);
            // remove the hard coding and add UI screen to get credentials
            UserInfo ui = new MyUserInfo(context);
            //session.setUserInfo(ui);//Use this to prompt password
            session.setPassword("LAB166");//hardcoded for testing

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
            for(File file: srcFile.listFiles()){
                if(file.isDirectory()){
                    continue;
                }

                Log.i("sftp", file.getAbsolutePath());//TODO remove. just for testing
                c.put(file.getAbsolutePath(),dstFile);//ERROR
            }

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

}
package com.techexchange.mobileapps.assignment3;

import android.os.HandlerThread;
import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class SendThread extends HandlerThread {

    private Handler sendHandler;
    private Socket socket;

    public SendThread(String name,Socket socket) {
        super(name);
        this.socket = socket;
    }

    public void postTask(String task){
        sendHandler.post(sendData(task));

    }

    public void prepareHandler(){
        sendHandler = new Handler(getLooper());//getLooper()
    }

    public Runnable sendData(String string){
        return new Runnable() {
            @Override
            public void run() {
                try{
                    PrintWriter print = new PrintWriter(socket.getOutputStream(), true);
                    print.println(string);
                } catch(IOException e){
                    Log.d("Send Thread", "IO Exception", e);
                }
            }
        };
    }
}

package com.techexchange.mobileapps.assignment3;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import android.util.Log;

public class ReceiveThread extends HandlerThread{
    private Handler receiveHandler;
    private Socket socket;
    public ReceiveThread(String name, Socket socket) {
        super(name);
        this.socket = socket;
    }

    public void postTask(Runnable task){
        receiveHandler.post(task);
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String in = reader.readLine();
            System.out.println("Hi-->" + in);
        }catch(IOException e){
            Log.d("Receive Thread", "IO Exception", e);
        }
        this.postTask(task);

    }

    public void prepareHandler(){
        receiveHandler = new Handler(getLooper());
    }


}


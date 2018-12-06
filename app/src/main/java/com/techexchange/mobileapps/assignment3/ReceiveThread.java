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
    private GameLogicGrid game;
    private BufferedReader br;
    public ReceiveThread(String name, Socket socket,GameLogicGrid game) {
        super(name);
        this.socket = socket;
        this.game = game;
        try{
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e){




















        }

    }


    @Override
    public void run() {
        super.run();
    }

    public void postTask(Runnable task){
        receiveHandler.post(task);

    }

    public void prepareHandler(){
        receiveHandler = new Handler(getLooper());
    }

    public Runnable repCall(){
        return new Runnable() {
            @Override
            public void run() {
                System.out.println("ENTRE");
                try{
                    String in = br.readLine();
                    deStringBundle(in);
                    System.out.println("Hi-->" + in);
                }catch(IOException e){
                    Log.d("Receive Thread", "IO Exception", e);
                }
                receiveHandler.post(repCall());

            }
        };

    }

    public void deStringBundle(String string){
        String[] values = string.split(",");
        System.out.println("String: "+ string);
        game.getTankOne().setCurrXPos(Integer.parseInt(values[0]));
        game.getTankOne().setCurrYPos(Integer.parseInt(values[1]));
        game.getTankOne().setRow(Integer.parseInt(values[2]));
        game.getTankOne().setColumn(Integer.parseInt(values[3]));


    }

}


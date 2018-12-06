package com.techexchange.mobileapps.assignment3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.util.Base64;
import android.util.Log;
import android.view.View;

public class ReceiveThread extends HandlerThread{
    private Handler receiveHandler;
    private Socket socket;
    private GameLogicGrid game;
    private BufferedReader br;
    private View gameView;
    public ReceiveThread(String name, Socket socket,GameLogicGrid game, View v) {
        super(name);
        this.socket = socket;
        this.game = game;
        this.gameView = v;
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

    public Runnable repCall(Boolean isServer){
        return new Runnable() {
            @Override
            public void run() {
               // System.out.println("ENTRE");
                try{
                    String in = br.readLine();
                    System.out.println("SERVER: " + isServer.toString() + " envio: "+ in);
                    deStringBundle(in, isServer);
                    //System.out.println("Hi-->" + in);
                }catch(IOException e){
                    Log.d("Receive Thread", "IO Exception", e);
                }
                receiveHandler.post(repCall(isServer));

            }
        };

    }

    public void deStringBundle(String string, Boolean isServer){
        String[] values = string.split(",");
        System.out.println("String: "+ string);
        Tank tank;
        if(isServer){
            tank = game.getTankTwo();
        }
        else{
            tank = game.getTankOne();
        }
        tank.setCurrXPos(Integer.parseInt(values[0]));
        tank.setCurrYPos(Integer.parseInt(values[1]));
        tank.setRow(Integer.parseInt(values[2]));
        tank.setColumn(Integer.parseInt(values[3]));
        if(Integer.parseInt(values[4])>=0 && Integer.parseInt(values[5])>=0){
            game.getGameGrid()[Integer.parseInt(values[4])][Integer.parseInt(values[5])].damageOcurred();
        }
        if(Integer.parseInt(values[6])==0){
            tank.getFireball().setActive(false);
        }
        else{
            tank.getFireball().setActive(true);
        }
        tank.getFireball().setPositionX(Integer.parseInt(values[7]));
        tank.getFireball().setPositionY(Integer.parseInt(values[8]));
        tank.getFireball().setVelocityX(Integer.parseInt(values[9]));
        tank.getFireball().setVelocityY(Integer.parseInt(values[10]));
        tank.setBitmap(checkOrientation(values[11],game));


    }

    public Bitmap checkOrientation(String string, GameLogicGrid gameGrid){
        if(string.equals("up")){
            return gameGrid.up;
        }
        else if(string.equals("down")){
            return gameGrid.down;

        }
        else if(string.equals("left")){
            return gameGrid.left;
        }
        else if(string.equals("right")){
            return gameGrid.right;
        }
        return null;
    }

}


package com.techexchange.mobileapps.assignment3;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final int PORT_NUMBER = 11002;
    Context context;
    private static AsyncTask<Void,Void,Void> task;
    View  laberinth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
      //  setContentView(laberinth);
        context = this;


        Button serverButton = findViewById(R.id.server_button);
        serverButton.setOnClickListener(v->{
        new ServerAsync(context).execute();

        });

        Button clientButton = findViewById(R.id.client_button);
        clientButton.setOnClickListener((View v) ->{
            new ClientAsync(context).execute();
            });

    }
//    public class ServerThread implements Runnable{
//        @Override
//        public void run(){
//            System.out.println("enter here");
//            Socket clientSocket = null;
//            try{
//                ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
//                clientSocket = serverSocket.accept();
//
//                System.out.println(clientSocket);
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                while(true) {
//                    String in = reader.readLine();
//                    System.out.println("Hi-->" + in);
//                }
//
//            } catch(IOException  ex){//| ClassNotFoundException
//                Log.d("Server Thread", "IO Exception", ex);
//            }
//        }
//    }

    public class ServerAsync extends AsyncTask<Context,Void,Socket>{
        private Context context;

        public ServerAsync(Context context){
            this.context = context;
        }

        @Override
        protected Socket doInBackground(Context... contexts) {
            Log.d(TAG, "doInBackground: server");
            System.out.println("enter here");
            Socket clientSocket = null;
            //context = contexts;
            try{
                ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
                clientSocket = serverSocket.accept();

                System.out.println(clientSocket);

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //while(true) {
                  //  String in = reader.readLine();
                   // System.out.println("Hi-->" + in);
                //}

            } catch(IOException  ex){//| ClassNotFoundException
                Log.d("Server Thread", "IO Exception", ex);
            }
            return clientSocket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            laberinth = new LaberinthView(context, socket);
            setContentView(laberinth);
            super.onPostExecute(socket);
        }
    }


    public class ClientAsync extends AsyncTask<Context,Void,Socket>{
        private Context context;
        private static final String SERVER = "10.0.0.65";

        public ClientAsync(Context context){
            this.context = context;
        }

        @Override
        protected Socket doInBackground(Context... contexts) {
            Socket socket = null;
            try{
                socket = new Socket(InetAddress.getByName(SERVER),PORT_NUMBER);
                PrintWriter print = new PrintWriter(socket.getOutputStream(), true);
                print.println("ADIOSSSS");
                return socket;
            } catch(IOException | NullPointerException ex ){
                Log.d("Client Thread", "IO Exception", ex);
            }
            return socket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            laberinth = new LaberinthView(context, socket);
            setContentView(laberinth);
            super.onPostExecute(socket);
        }
    }


//    public class ClientThread implements Runnable{
//        private static final String SERVER = "10.0.0.65";
//        @Override
//        public void run(){
//            try{
//                Socket socket = new Socket(InetAddress.getByName(SERVER),PORT_NUMBER);
//                PrintWriter print = new PrintWriter(socket.getOutputStream(), true);
//                print.println("ADIOSSSS");
//            } catch(IOException | NullPointerException ex ){
//                Log.d("Client Thread", "IO Exception", ex);
//            }
//        }
//    }

}






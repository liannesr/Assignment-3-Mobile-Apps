package com.techexchange.mobileapps.assignment3;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.net.wifi.WifiManager;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final int PORT_NUMBER = 11002;
    public String SERVER;
    Context context;
    private static AsyncTask<Void,Void,Void> task;
    View  laberinth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = this;
        EditText text = findViewById(R.id.name_edit_text);
        TextView textIp = findViewById(R.id.ip_text_view);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        textIp.setText("Your Ip Adress is: " +ipAddress );
        Button serverButton = findViewById(R.id.server_button);
        serverButton.setOnClickListener(v->{
            new ServerAsync(context).execute();

        });

        Button clientButton = findViewById(R.id.client_button);
        clientButton.setOnClickListener((View v) ->{
            SERVER = text.getText().toString();
            new ClientAsync(context).execute();
            });



    }


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
            try{
                ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
                clientSocket = serverSocket.accept();

                System.out.println(clientSocket);

            } catch(IOException  ex){//| ClassNotFoundException
                Log.d("Server Thread", "IO Exception", ex);
            }
            return clientSocket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            laberinth = new LaberinthView(context, socket, true);
            setContentView(laberinth);
            super.onPostExecute(socket);
        }
    }


    public class ClientAsync extends AsyncTask<Context,Void,Socket>{
        private Context context;
        public ClientAsync(Context context){
            this.context = context;
        }

        @Override
        protected Socket doInBackground(Context... contexts) {
            Socket socket = null;
            try{
                socket = new Socket(InetAddress.getByName(SERVER),PORT_NUMBER);
                return socket;
            } catch(IOException | NullPointerException ex ){
                Log.d("Client Thread", "IO Exception", ex);
            }
            return socket;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            laberinth = new LaberinthView(context, socket, false);
            setContentView(laberinth);
            super.onPostExecute(socket);
        }
    }


}
package com.example.mithunmistry.mhome;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class MyRoom extends AppCompatActivity {

    Socket socket;
    Integer ic = 1;
    Integer x = 1;
    int SERVERPORT = 6000;
    String SERVER_IP = "192.168.1.105";
    String message = "myroom";
    Timer timer;
    TimerTask timerTask;
    ProgressDialog dialog;
    Switch fan, light;
    int m = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fan = (Switch)findViewById(R.id.mrFan);
        light = (Switch)findViewById(R.id.mrLight);
        setSupportActionBar(toolbar);
        SharedPreferences portip = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ipr = portip.getString("IP", "192.168.1.100");
        int portr = portip.getInt("Port", 6000);
        SERVER_IP = ipr;
        SERVERPORT = portr;
        ic = 1;

        dialog = new ProgressDialog(MyRoom.this);
        dialog.setTitle("Connecting...");
        dialog.setMessage("Please wait..");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        startTimer();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 1, 1000); //
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                if(!dialog.isShowing()){
                    message = "o";
                    if(light.isChecked()){
                        message = message + "1";
                    }
                    else{
                        message = message + "0";
                    }
                    if(fan.isChecked()){
                        message = message + "1";
                    }
                    else{
                        message = message + "0";
                    }

                }

                new Thread(new ClientThread()).start();
                try {
                    String str = message;

                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), true);
                    out.println(str);
                    out.flush();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new ConnectionTask().execute();

            }
        };
    }
    class ConnectionTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String responce = null;
            try {

                InputStream input = socket.getInputStream();
                int lockSeconds = 10 * 1000;

                long lockThreadCheckpoint = System.currentTimeMillis();
                int availableBytes = input.available();
                while (availableBytes <= 0 && (System.currentTimeMillis() < lockThreadCheckpoint + lockSeconds)) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    availableBytes = input.available();
                }

                byte[] buffer = new byte[availableBytes];
                input.read(buffer, 0, availableBytes);
                responce = new String(buffer);

                //out.close();
                input.close();
                socket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responce;
        }

        protected void onPostExecute(String responce) {
            if (responce != null) {

                    String[] msg = responce.split("");
                    if(msg[0].equals("o")){
                        if(msg[1].equals("1")){
                            light.setChecked(true);
                        }
                        else{
                            light.setChecked(false);
                        }
                            if(msg[2].equals("1")){
                                fan.setChecked(true);
                            }
                            else{
                                fan.setChecked(false);
                            }
                    }
                if(m == 0){
                    message = "ok";
                    m = 1;
                    dialog.hide();
                }


            }
            else{
                //message = "door";
            }


        }
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {
            if (ic.intValue() == x.intValue()) {

                try {
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                    socket = new Socket(serverAddr, SERVERPORT);

                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
            else {

            }

        }
    }


}

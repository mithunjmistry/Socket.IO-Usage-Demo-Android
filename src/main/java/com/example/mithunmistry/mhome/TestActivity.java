package com.example.mithunmistry.mhome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.URISyntaxException;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class TestActivity extends AppCompatActivity {
    Switch testLight, testFan;
    String connection_to = "http://192.168.1.105:5002";
    TextView testData;
    boolean light_p = false; boolean fan_p = false;
    int i = 1;
    boolean m = false;
    boolean light_c = false;
    boolean fan_c = false;
    String message = "o00";
    String light_current = "0";
    String fan_current = "0";
    private Socket mSocket;
    boolean change_monitor = false;
    int cm = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        testLight = (Switch)findViewById(R.id.testLight);
        testFan = (Switch)findViewById(R.id.testFan);
        testData = (TextView)findViewById(R.id.testData);
        i = 1;
        m = false;
        light_p = false;
        fan_p = false;
        light_c = false;
        fan_c = false;
        change_monitor = false;
        cm = 2;
        SharedPreferences portip = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ipr = portip.getString("IP", "192.168.1.100");
        Integer portr = portip.getInt("Port", 6000);
        connection_to = "http://" + ipr + ":" + String.valueOf(portr);
        Log.d("ipchanged", connection_to);
        makeConnection();

                mSocket.connect();
                mSocket.on("new message", onNewMessage);
                listen();


                setSupportActionBar(toolbar);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            //private Socket mSocket;
            public void makeConnection()
            {

                try {
                    //mSocket = IO.socket("http://192.168.1.100:5001");
                    mSocket = IO.socket(connection_to);
                } catch (URISyntaxException e) {
                }
            }

            public void listen() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (i != 0) {
                            // do something
                            mSocket.emit("chat message", "cool");
                            i = i - 1;

                        }
                        if (i == 0 && m == false) {
                            if (testLight.isChecked()) {
                                light_c = true;
                                light_p = true;
                            } else {
                                light_c = false;
                                light_p = false;
                            }
                            if (testFan.isChecked()) {
                                fan_c = true;
                                fan_p = true;
                            } else {
                                fan_c = false;
                                fan_p = false;
                            }
                            m = true;
                        }
                        if (m) {
                            if (testLight.isChecked()) {
                                light_c = true;
                                light_current = "1";
                            } else {
                                light_c = false;
                                light_current = "0";
                            }
                            if (testFan.isChecked()) {
                                fan_c = true;
                                fan_current = "1";
                            } else {
                                fan_c = false;
                                fan_current = "0";
                            }
                            if ((light_c != light_p) || (fan_c != fan_p)) {
                                message = "";
                                message = "o" + light_current + fan_current;
                                light_p = light_c;
                                fan_p = fan_c;
                                change_monitor = true;
                                cm = 2;
                                //mSocket.emit("chat message", message);
                            } else {
                                mSocket.emit("chat message", "cool");
                            }
                        }
                        listening();
                    }
                }, 100);

            }

            public void listening() {
                // mSocket.on("new message", onNewMessage);
                Log.d("running continously", "indefinite");
                if(change_monitor) {
                    if (cm != 0) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSocket.emit("chat message", message);

                                cm = cm - 1;
                                if(cm == 0){
                                    change_monitor = false;
                                }
                                listening();
                            }
                        }, 1300);
                    }
                }
                else {
                    listen();
                }
            }

            @Override
            public void onDestroy() {
                super.onDestroy();
                mSocket.off("chat message");
                mSocket.disconnect();
            }

            public Emitter.Listener onNewMessage = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    (TestActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = (String) args[0];
                    /*String username;
                    //String message;
                    try {
                        username = data.getString("mithun");
                        //message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view*/
                            testData.setText(data);
                            String s = testData.getText().toString();
                            Character c1 = s.charAt(0);
                            Character c2 = s.charAt(1);
                            Character c3 = s.charAt(2);

                            if (c1.toString().equals("o")) {
                                if (c2.toString().equals("1")) {
                                    testLight.setChecked(true);
                                } else {
                                    testLight.setChecked(false);
                                }
                                if (c3.toString().equals("1")) {
                                    testFan.setChecked(true);
                                } else {
                                    testFan.setChecked(false);
                                }
                            }


                        }
                    });
                }
            };
        }

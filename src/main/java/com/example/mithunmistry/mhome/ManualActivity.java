package com.example.mithunmistry.mhome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class ManualActivity extends AppCompatActivity {
    String connection_to = "http://192.168.1.105:5002";
    private Socket mSocket;
    EditText manualEdit;
    TextView manualData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        manualEdit = (EditText)findViewById(R.id.manualEdit);
        manualData = (TextView)findViewById(R.id.manualData);
        SharedPreferences portip = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ipr = portip.getString("IP", "192.168.1.100");
        Integer portr = portip.getInt("Port", 6000);
        connection_to = "http://" + ipr + ":" + String.valueOf(portr);
        Log.d("ipchanged", connection_to);
        makeConnection();

        mSocket.connect();
        mSocket.on("new message", onNewMessage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void makeConnection()
    {

        try {
            //mSocket = IO.socket("http://192.168.1.100:5001");
            mSocket = IO.socket(connection_to);
        } catch (URISyntaxException e) {
        }
    }

    public void manualSend(View view){
        mSocket.emit("chat message", manualEdit.getText().toString());
    }

    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            (ManualActivity.this).runOnUiThread(new Runnable() {
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
                    manualData.setText(data);
                }
            });
        }
    };

}

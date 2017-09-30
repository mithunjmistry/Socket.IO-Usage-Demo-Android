package com.example.mithunmistry.mhome;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mithun Mistry on 29-06-2016.
 */
public class connection {
    Socket socket;
    Integer ic = 1;
    Integer x = 1;
    int SERVERPORT = 6000;
    String SERVER_IP = "192.168.1.100";
    String message = "pid";
    Timer timer;
    TimerTask timerTask;
}

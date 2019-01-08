package com.starzy.bio.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BIOHandler implements Runnable {

    private Socket socket;

    public BIOHandler(){}

    public BIOHandler(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();



            OutputStream outputStream = socket.getOutputStream();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

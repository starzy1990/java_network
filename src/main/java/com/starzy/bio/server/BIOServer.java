package com.starzy.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServer {

    public static void main(String[] args) throws IOException {

        //创建socket服务端并绑定端口2019
        ServerSocket serverSocket = new ServerSocket(2019);
        Socket socket = serverSocket.accept();
        new Thread(new BIOHandler(socket)).run();
    }
}

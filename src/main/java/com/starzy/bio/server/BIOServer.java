package com.starzy.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO服务器端
 * 实现接受客户端数据，解析接收数据，
 * 并使用反射技术调用对应的类中的方法，并把执行结果返回给客户端
 */
public class BIOServer {

    public static void main(String[] args) throws IOException {

        //创建socket服务端并绑定端口2019
        ServerSocket serverSocket = new ServerSocket(2019);
        Socket socket = serverSocket.accept();
        new Thread(new BIOHandler(socket)).run();
    }
}

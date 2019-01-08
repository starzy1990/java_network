package com.starzy.bio.client;

import java.io.*;
import java.net.Socket;

/**
 * BIO客户端，实现调用服务端类的方法
 * 发送数据格式为全类名：方法名：参数值，
 * 服务端返回执行方法结果
 */
public class BIOClient {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("127.0.0.1",2019);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        PrintWriter pw = new PrintWriter(new BufferedOutputStream(outputStream));
        pw.println("com.starzy.bio.server.TestBusiness:getPrice:yifu");
        pw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String readLine = br.readLine();

        System.out.println("client get result: " + readLine);

        inputStream.close();
        outputStream.close();
        socket.close();

    }
}

package com.starzy.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

    public static void main(String[] args) {

        //创建连接地址
        InetSocketAddress address = new InetSocketAddress("127.0.0.1",2019);
        //声明连接通道
        SocketChannel socketChannel = null;
        //建立写的缓冲区
        ByteBuffer writeBuf = ByteBuffer.allocate(1024);

        try {
            //打开通道
            socketChannel = SocketChannel.open();
            //进行连接
            socketChannel.connect(address);
            while (true){

                //定义一个字节数组，然后使用系统录入功能：
                byte[]bytes = new byte[1024];
                System.in.read(bytes);

                //把数据放到缓冲区中
                writeBuf.put(bytes);
                //对缓冲区进行复位
                writeBuf.flip();
                //写出数据
                socketChannel.write(writeBuf);
                //清空缓冲区数据
                writeBuf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(socketChannel!=null){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

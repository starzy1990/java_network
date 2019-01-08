package com.starzy.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * nio server实现
 *
 * 主要概念：多路复用器selector,服务端管道ServerSocketChannel, 客户端管道SocketChannel,缓存区Buffer
 * 路:每个io请求都可以进来（非阻塞），然后注册到selector, selector无限循环判断注册进来的管道是否有io操作，
 * 如果有io操作，查看状态，并做处理, 读取都在通道中进行
 *
 */
public class NIOServer implements Runnable {

    //多路复制器（管理所有的通道）
    private Selector selector;

    //建立缓冲区，缓冲读
    private ByteBuffer readBuf = ByteBuffer.allocate(1024);

    //建立缓冲区，缓冲写
    private ByteBuffer writeBuf = ByteBuffer.allocate(1024);

    public NIOServer(String hostname,int port){
        try {
            //打开多路复制器
            this.selector = Selector.open();

            //代开服务器通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //设置服务器通道为非阻塞模式，若果设置为true就和BIO效果一样
            serverSocketChannel.configureBlocking(false);
            //绑定地址和监听端口
            serverSocketChannel.bind(new InetSocketAddress(hostname,port));
            //服务器管道和客户端管道都注册到这个selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        //多路复用器selector无限循环查看注册管道是否有IO操作
        while (true){
            try {
                //必须让多路复用器开始监听
                this.selector.select();
                Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();

                while (keys.hasNext()){
                    //获取一个选择元素
                    SelectionKey key = keys.next();
                    //开始处理通道并从容器中移除
                    keys.remove();
                    //判断是否有效
                    if(key.isValid()){
                        if(key.isAcceptable()){
                            this.accept(key);
                        }
                        //若此key的通道的行为是"读"
                        if(key.isReadable()){
                            this.read(key);
                        }
                        //若此key的通道的行为是"写"
                        if(key.isWritable()){
                            this.write(key);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void write(SelectionKey key) {

        try {
            this.writeBuf.clear();
            SocketChannel channel = (SocketChannel)key.channel();
            int count = channel.write(this.writeBuf);
            channel.finishConnect();
            channel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel)key.channel();

            //清空缓冲区
            this.readBuf.clear();
            //获取注册的socket通道对象
        try {
            //读取数据
            int count = channel.read(this.readBuf);
            //未读取到数据
            if(count == -1){
                key.channel().close();
                key.cancel();
                return;
            }

            //有数据则进行读取，读取之前需要进行复位方法（把position和limit进行复位）
            this.readBuf.flip();
            //根据缓冲区的数据长度创建相应大小的byte数组，接收缓冲区的数据
            byte[]bytes = new byte[this.readBuf.remaining()];
            //接收缓冲区数据
            this.readBuf.get(bytes);
            String data = new String(bytes).trim();
            System.out.println("recv:" + data);
            //写回客户端

        } catch (IOException e) {
            key.cancel();
            try {
                channel.socket().close();
                channel.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return;
        }
    }

    private void accept(SelectionKey key){

        try {
            //获取服务器端通道
            ServerSocketChannel socketChannel = (ServerSocketChannel)key.channel();
            //执行阻塞方法
            SocketChannel sc = socketChannel.accept();
            //设置阻塞模式
            sc.configureBlocking(false);
            //注册到多路复用器上，并设置读取标识
            sc.register(this.selector, SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        new Thread(new NIOServer("127.0.0.1",2019)) .start();
    }

}

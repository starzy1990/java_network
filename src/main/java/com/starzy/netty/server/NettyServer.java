package com.starzy.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * • 配置服务器功能，如线程、端口 • 实现服务器处理程序，它包含业务逻辑，决定当有一个请求连接或接收数据时该做什么
 */
public class NettyServer {

    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();//创建线程组
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup)
                    //指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
                    .channel(NioServerSocketChannel.class)//注册服务端channel
                    /*
                    ChannelOption.SO_BACKLOG, 1024
                        用于构造服务器端套接字ServerSocket对象，标识服务器端请求处理线程全满时，用于临时
                        存放已完成三次握手的请求的队列的最大长度，如果未设置或所设置的值小于1，Java将使用默认值50。
                    ChannelOption.SO_KEEPALIVE, true
                        是否启用心跳保活机制；在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右
                        上层没有任何数据传输的情况下，这套机制才会被激活。
                    ChannelOption.TCP_NODELAY, true
                        在TCP/IP协议中，无论发送多少数据，总是在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认，
                        为了尽可能利用网络宽带，TCP总是希望尽可能的发送足够大的数据，这里就涉及到一个名为Nagle的算法，该算法的目的就是为了
                        尽可能发送大块数据，避免网络中充斥着许多小的数据块。
                        TCP_NODELAY就是用于启用或关闭Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；
                        如果减少发送次数减少网络交互，就设置为false，等积累到一定大小后再发送，默认为false。
                    ChannelOption.SO_REUSEADDR, true
                        SO_REUSEADDR允许启动一个监听服务器并捆绑其众所周知端口，即使以前建立的将此端口用作他们的本地端口的连接仍存在。
                        这通常是重启监听服务器时出现，若不设置此项，则bind时出错。
                        SO_REUSEADDR允许在同一端口上启动同一服务器的多个实例，只要每个实例捆绑一个不同的本地IP地址即可。
                        对于TCP，我们根本不可能启动捆绑相同IP地址和相同端口号的多个服务器。
                        SO_REUSEADDR允许单个进程捆绑同一端口到多个套接口上，只要每个捆绑指定不同的本地IP地址即可。这一般不用于TCP服务器。
                        SO_REUSEADDR允许完全重复的捆绑：当一个IP地址和端口绑定到某个套接口上时，还允许此IP地址和端口捆绑到另一个套接口上。
                        一般来说，这个特性仅在支持多播的系统上才有，而且只对UDP套接口而言（TCP不支持多播）
                    ChannelOption.SO_RCVBUF  AND  ChannelOption.SO_SNDBUF
                        定义接收或者传输的系统缓冲区buf的大小
                    ChannelOption.ALLOCATOR
                        Netty4使用对象池，重用缓冲区
                        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

                     */
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))//设置日志
                    .localAddress("127.0.0.1", 2019)//设置InetSocketAddress让服务器监听某个端口已等待客户端连接。
                    .childHandler(new ChannelInitializer<Channel>() {//设置childHandler执行所有的连接请求
                        protected void initChannel(Channel channel) throws Exception {
                            //设置Marshalling的编码和解码
                            /*channel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            channel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            channel.pipeline().addLast(new ReadTimeoutHandler(5));*/
                            channel.pipeline().addLast(new EchoInHandler1());
                            channel.pipeline().addLast(new EchoOutHandler1());
                            channel.pipeline().addLast(new EchoOutHandler2());
                            channel.pipeline().addLast(new EchoInHandler2());
                        }
                    });
            // 最后绑定服务器等待直到绑定完成，调用sync()方法会阻塞直到服务器完成绑定
            ChannelFuture channelFuture = null;

            channelFuture = serverBootstrap.bind().sync();

            System.out.println("开始监听，端口为：" + channelFuture.channel().localAddress());
            // 等待channel关闭，因为使用sync()，所以关闭操作也会被阻塞。
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 阻塞等待线程组关闭
            eventLoopGroup.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws Exception {
         new  NettyServer(2019).start();
    }
}

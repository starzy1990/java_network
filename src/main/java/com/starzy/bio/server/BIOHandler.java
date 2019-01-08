package com.starzy.bio.server;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 使用BIO模拟RPC
 */
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

            //客户端传入数据格式为---全类名：方法名：方法参数
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String readLine = bufferedReader.readLine();
            String[] split = readLine.split(":");
            String className = split[0];
            String methodName = split[1];
            String methodParam = split[2];

            //执行客户端调用方法
            Class<?> forName = Class.forName(className);//根据全类名反射获取类的class文件
            System.out.println("calling class:" + forName);
            Object newInstance = forName.newInstance();//根据类文件实例化对象
            Method method = forName.getMethod(methodName, String.class);//反射获取方法
            System.out.println("calling method:" + method);
            Object invoke = method.invoke(newInstance, methodParam);
            System.out.println("result:" + invoke);

            //把执行结果写回
            PrintWriter pw = new PrintWriter(new BufferedOutputStream(outputStream));
            pw.println(invoke);
            pw.flush();
            //释放资源
            bufferedReader.close();
            pw.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

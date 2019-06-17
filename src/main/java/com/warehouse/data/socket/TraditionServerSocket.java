package com.warehouse.data.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-10-26 13:41
 **/
public class TraditionServerSocket {


    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                //一旦有阻塞，则表示服务器与客户端获得了连接
                Socket socket = serverSocket.accept();

                System.out.println("Request:" + socket.toString() + "connected");

                new HandlerThread(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class HandlerThread implements Runnable {

        private Socket socket;
        public HandlerThread(Socket socket) {
            this.socket = socket;
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                //读取客户端发来的数据
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                //这里要注意和客户端输出流的写方法对应，否则会抛EOFException
                String str = inputStream.readUTF();

                System.out.println("客户端发来的内容为：" + str);

                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                System.out.print("请输入:\t");
                //控制台键盘输入的一行内容
                String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
                outputStream.writeUTF(s);

                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}

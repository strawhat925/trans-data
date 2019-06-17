package com.warehouse.data.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-10-26 13:48
 **/
public class TraditionClientSocket {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);

            //读取服务端响应的数据
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            //向服务端发送数据
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            System.out.print("请输入:\t");
            String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
            outputStream.writeUTF(s);

            String str = inputStream.readUTF();
            System.out.println("服务端响应内容为：" + str);


            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

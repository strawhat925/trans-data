package com.warehouse.data.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * http server.
 * <p>
 * @author zli
 * @create 2018-03-27 13:19
 **/
public class HttpServer {


    public static void main(String[] args) {
        new HttpServer(8080).startup();
    }


    private int port;

    public HttpServer(int port) {
        this.port = port;
    }


    public void startup() {
        try {
            ServerSocket server = new ServerSocket(port);
            for (; ; ) {
                Socket socket = server.accept();

                System.out.println("Request " + socket.toString() + " connected");
                LineNumberReader in = new LineNumberReader(new InputStreamReader(socket.getInputStream()));
                String lineInput;
                String requestPage = null;
                String userInfo = null;
                while ((lineInput = in.readLine()) != null) {
                    System.out.println(lineInput);
                    if (in.getLineNumber() == 1) {
                        requestPage = lineInput.substring(lineInput.indexOf('/') + 1, lineInput.lastIndexOf(' '));
                        System.out.println("Request page: " + requestPage);
                    } else if (lineInput.startsWith("Cookie: ")) {
                        userInfo = lineInput;
                        System.out.println("new User " + lineInput);
                    } else {
                        if (lineInput.isEmpty()) {
                            System.out.println("header finished");
                            doResponseGet(requestPage, socket, userInfo);
                        }
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void doResponseGet(String requestPage, Socket socket, String userInfo) throws IOException {
        final String WEB_ROOT = "/Users/strawhat925/Applications/workspace/trans-data";
        File file = new File(WEB_ROOT, requestPage);
        OutputStream out = socket.getOutputStream();
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            byte[] buf = new byte[in.available()];
            in.read(buf);
            in.close();
            out.write(buf);
            out.flush();
            socket.close();
            System.out.println("request complete.");
        } else {
            String msg = "I can't find bao zang...cry..\r\n";
            String response = "HTTP/1.1 200 OK\r\n";
            response += "Server: zli Server/0.1\r\n";
            if (null == userInfo) {
                response += genCookieHeader();
            }


            response += "Content-length: " + (msg.length() - 4) + "\r\n";
            response += "\r\n";

            response += msg;
            out.write(response.getBytes());
            out.flush();
        }

    }



    private String genCookieHeader(){
        String header = "Set-Cookie: jsessionid=" + System.currentTimeMillis() + ".zli; domain=localhost" + "\r\n";
        header += "Set-Cookie: autologin=true; domain=localhost" + "\r\n";

        return header;
    }


}

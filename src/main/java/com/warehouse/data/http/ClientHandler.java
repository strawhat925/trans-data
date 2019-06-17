package com.warehouse.data.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-28 12:08
 **/
public class ClientHandler implements Runnable {

    private final InputStream inputStream;
    private final Socket      socket;

    private HttpServerTD httpServerTD;

    public ClientHandler(InputStream inputStream, Socket socket, HttpServerTD httpServerTD) {
        this.inputStream = inputStream;
        this.socket = socket;
        this.httpServerTD = httpServerTD;
    }


    @Override
    public void run() {
        OutputStream outputStream = null;
        try {


            System.out.println("client handler thread name:" + Thread.currentThread().getName());

            outputStream = socket.getOutputStream();
            ITempFileManager tempFileManager = this.httpServerTD.getTempFileManagerFactory().create();
            HttpSession httpSession = new HttpSession(this.inputStream, outputStream, this.httpServerTD, tempFileManager);
            while (!socket.isClosed()){
                httpSession.execute();
            }

            /*LineNumberReader in = new LineNumberReader(new InputStreamReader(inputStream));
            String lineInput = null;
            String requestPage = null;
            while ((lineInput = in.readLine()) != null) {
                HttpServerTD.print(lineInput);

                // get or post
                //page
                if (in.getLineNumber() == 1)
                    requestPage = lineInput.substring(lineInput.indexOf("/") + 1, lineInput.lastIndexOf(" "));
                else if (1 == 2) {

                } else {
                    doResponseGet(requestPage, socket, null);
                }
            }
*/

        } catch (IOException e) {
            HttpServerTD.safeClose(outputStream);
            HttpServerTD.safeClose(inputStream);
            HttpServerTD.safeClose(socket);
        }

    }


    public void close() {
        HttpServerTD.safeClose(inputStream);
        HttpServerTD.safeClose(socket);
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
                //response += genCookieHeader();
            }


            response += "Content-length: " + (msg.length() - 4) + "\r\n";
            response += "\r\n";

            response += msg;
            out.write(response.getBytes());
            out.flush();
        }

    }


}

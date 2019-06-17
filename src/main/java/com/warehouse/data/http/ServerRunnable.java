package com.warehouse.data.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-27 14:09
 **/
public class ServerRunnable implements Runnable {

    private HttpServerTD httpServerTD;
    private int          timeout;

    private IOException bindException;
    private boolean hasBind = false;

    public ServerRunnable(HttpServerTD httpServerTD, int timeout) {
        this.httpServerTD = httpServerTD;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            httpServerTD.getServerSocket().bind(httpServerTD.host != null ? new InetSocketAddress(httpServerTD.host, httpServerTD.port) : new InetSocketAddress(httpServerTD.port));
            hasBind = true;
        } catch (IOException e) {
            bindException = e;
            return;
        }

        try {
            do {
                final Socket socket = httpServerTD.getServerSocket().accept();
                if (timeout > 0) {
                    socket.setSoTimeout(timeout);
                }
                InputStream in = socket.getInputStream();
                httpServerTD.iAsynRunner.exec(httpServerTD.createHandler(in, socket));

            } while (!httpServerTD.getServerSocket().isClosed());

        } catch (IOException e) {
            httpServerTD.print("Communication with the client broken");
        }

    }


    public IOException getBindException() {
        return bindException;
    }

    public boolean isHasBind() {
        return hasBind;
    }
}

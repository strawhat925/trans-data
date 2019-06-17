package com.warehouse.data.util;

import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.netty.handler.codec.http.HttpMethod;


/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-04-24 17:00
 **/
public class Test {


    public static void baseImage(String url) throws IOException {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod(HttpMethod.GET.name());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = conn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                    baos.flush();
                }

                BASE64Encoder encoder = new BASE64Encoder();
                String base = encoder.encode(baos.toByteArray());
                System.out.println(base);

            }

        } catch (IOException e) {
            throw e;
        }
    }


    public static void main(String[] args) {
        try {
            baseImage(" http://img.coocaa.com/www/attachment/forum/201602/16/085938u86ewu4l8z6flr6w.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.warehouse.data.http;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-04-09 20:30
 **/
public class TestHttpServer extends HttpServerTD {
    public TestHttpServer(String host, int port) {
        super(host, port);
    }


    public static void main(String[] args) {
        try {
            new TestHttpServer(null, 8080).start(0, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Response serve(HttpSession httpSession) {

        switch (httpSession.getUri()) {
            case "/xxsdf":
                System.out.println("==============");
                return Response.newFixedLengthResponse(Status.BAD_REQUEST, HttpServerTD.MIME_TEXTPLAIN, "测试。。。。");
            default:
                System.out.println("*******");
        }


        return super.serve(httpSession);
    }
}

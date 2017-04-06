package com.warehouse.data.nio;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.nio
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-28 11:03
 **/
public class Server {

    public static void main(String[] args) throws IOException {
        //5个reactor线程
        NIOReactorPool nioReactorPool = new NIOReactorPool("NIOReactor-IO", 5);
        NIOAcceptor nioAcceptor = new NIOAcceptor("NIOAcceptor-IO", "127.0.0.1", 8888, nioReactorPool);
        nioAcceptor.start();


    }
}

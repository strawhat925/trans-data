package com.warehouse.data.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.nio
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-28 11:00
 **/
public class Client {


    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);


        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress("127.0.0.1",8888));


        if(socketChannel.isConnectionPending()){
            //要finishConnect，否则会出现NotYetConnectedException异常
            socketChannel.finishConnect();
            socketChannel.write(ByteBuffer.wrap(new String("hello world").getBytes()));
        }

    }
}

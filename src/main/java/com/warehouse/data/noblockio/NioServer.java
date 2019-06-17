package com.warehouse.data.noblockio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-04-09 22:29
 **/
public class NioServer {


    public static void main(String[] args) {

        try {
            Selector selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            InetSocketAddress inetSocketAddress = new InetSocketAddress(9000);
            serverSocketChannel.socket().bind(inetSocketAddress);


            System.out.println("Start at ....");

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                int selectNum = selector.select();

                System.out.println("selected Number is : " + selectNum);
                Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                while (selectionKeys.hasNext()) {
                    SelectionKey selectionKey = selectionKeys.next();
                    if ((selectionKey.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        socketChannel.write(ByteBuffer.wrap("Welcome to changsha ....".getBytes()));

                    } else if ((selectionKey.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buff = ByteBuffer.allocate(100);
                       /* buff.put("\r\nFollow you :".getBytes());
                        socketChannel.read(buff);
                        buff.put("\r\n".getBytes());
                        buff.flip();
                        socketChannel.write(buff);*/

                       socketChannel.read(buff);

                        buff = (ByteBuffer) selectionKey.attachment();
                        if (buff == null || buff.hasRemaining()) {
                            int writeBufferSize = socketChannel.socket().getSendBufferSize();

                            buff = ByteBuffer.allocate(writeBufferSize * 50 + 2);
                            for (int i = 0; i < buff.capacity() - 2; i++) {
                                buff.put((byte) ('a' + i % 25));
                            }
                            buff.put("\r\n".getBytes());
                            buff.flip();

                            System.out.println("send buffer size: " + (writeBufferSize));
                        }


                        int writed = socketChannel.write(buff);
                        System.out.println("writed : " + writed);

                        if (buff.hasRemaining()) {
                            System.out.println("not write finished, remains " + buff.remaining());
                            buff = buff.compact();
                            selectionKey.attach(buff);
                            selectionKey.interestOps(selectionKey.readyOps() & SelectionKey.OP_WRITE);
                        } else {
                            selectionKey.attach(null);
                            selectionKey.interestOps(selectionKey.readyOps() & ~SelectionKey.OP_WRITE);
                        }
                    } else if ((selectionKey.readyOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
                        System.out.println("received write event");
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buff = (ByteBuffer) selectionKey.attachment();

                        if (buff != null) {
                            int writed = socketChannel.write(buff);
                            System.out.println("writed =====: " + writed);

                            if (buff.hasRemaining()) {
                                System.out.println("not write finished, remains " + buff.remaining());
                                buff.compact();
                                selectionKey.attach(buff);
                                selectionKey.interestOps(selectionKey.readyOps() & SelectionKey.OP_WRITE);
                            }else {
                                selectionKey.attach(null);
                                selectionKey.interestOps(selectionKey.readyOps() & ~SelectionKey.OP_WRITE);
                            }
                        }
                    }
                    selectionKeys.remove();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}

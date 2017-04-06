package com.warehouse.data.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import javax.imageio.IIOException;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.nio
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-28 9:55
 **/
public class NIOAcceptor extends Thread {
    private Selector selector;
    private ServerSocketChannel channel;
    private NIOReactorPool reactorPool;


    public NIOAcceptor(String name, String host, int port, NIOReactorPool reactorPool) throws IOException {
        super(name);
        this.reactorPool = reactorPool;
        //获取一个管理通道器
        this.selector = Selector.open();
        //获取一个接受连接socket
        this.channel = ServerSocketChannel.open();
        //设置非阻塞
        this.channel.configureBlocking(false);
        //绑定端口
        this.channel.bind(new InetSocketAddress(host, port));
        //注册事件
        this.channel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("start NIOAcceptor thread server.");
    }


    @Override
    public void run() {
        final Selector selector = this.selector;
        //轮询
        for (; ; ) {
            try {
                //阻塞，直到select事件到达
                selector.select(1000L);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                try {
                    for (SelectionKey key : selectionKeySet) {
                        if (key.isValid() && key.isAcceptable()) {
                            accept(selector);
                        }/* else if (key.isValid() && key.isReadable()) {
                            Processor processor = (Processor) key.attachment();
                            try{
                                processor.process(key);
                            }catch (IOException e){
                                processor.close();
                            }
                        } */else {
                            key.cancel();
                        }
                    }
                } finally {
                    selectionKeySet.clear();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void accept(Selector selector) {
        SocketChannel socketChannel = null;
        try {
            System.out.println("accept client success.");
            socketChannel = this.channel.accept();
            socketChannel.configureBlocking(false);

            //单个Reactor线程处理
            //SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            //selectionKey.attach(new Processor(socketChannel));
            //多个Reactor线程处理
            NIOReactor reactor = this.reactorPool.getNextReactor();
            reactor.postRegister(new Processor(socketChannel));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.warehouse.data.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.nio
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-28 10:49
 **/
public class Processor {
    private SocketChannel channel;
    private SelectionKey selectionKey;

    public Processor(SocketChannel channel) {
        this.channel = channel;
    }

    public void register(Selector selector) throws ClosedChannelException {
        selectionKey = this.channel.register(selector, SelectionKey.OP_READ, this);
    }

    public void process(SelectionKey key) throws IOException {
        //可以采用线程池处理
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int count = socketChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()));
    }



    public void close(){
        if(this.channel != null){
            try {
                this.channel.close();
                this.selectionKey.cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

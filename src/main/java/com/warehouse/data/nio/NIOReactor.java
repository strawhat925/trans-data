package com.warehouse.data.nio;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.nio
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-03-28 11:51
 **/
public final class NIOReactor {
    private final String name;
    private final RWThread reactorR;

    public NIOReactor(String name) throws IOException {
        this.name = name;
        this.reactorR = new RWThread();
    }

    public void startup() {
        new Thread(reactorR, this.name + "-RW").start();
    }

    public void postRegister(Processor processor) {
        this.reactorR.registerQueue.offer(processor);
        this.reactorR.selector.wakeup();
    }

    private final class RWThread extends Thread {
        private final Selector selector;
        private final ConcurrentLinkedQueue<Processor> registerQueue;

        public RWThread() throws IOException {
            this.selector = Selector.open();
            this.registerQueue = new ConcurrentLinkedQueue<Processor>();
        }

        @Override
        public void run() {
            final Selector selector = this.selector;
            Set<SelectionKey> selectionKeySet = null;

            for (; ; ) {
                try {
                    selector.select(1000L);
                    register(selector);
                    selectionKeySet = selector.selectedKeys();
                    for (SelectionKey key : selectionKeySet) {
                        Object att = key.attachment();
                        Processor processor = null;
                        try {
                            if (att != null && key.isValid()) {
                                processor = (Processor) att;
                                if (key.isReadable()) {
                                    processor.process(key);
                                }
                                if (key.isWritable()) {

                                }
                            } else {
                                key.channel();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            if(processor != null){
                                processor.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (selectionKeySet != null) {
                        selectionKeySet.clear();
                    }
                }
            }
        }


        private void register(Selector selector) {
            if (this.registerQueue.isEmpty()) {
                return;
            }
            Processor processor = null;
            while ((processor = this.registerQueue.poll()) != null) {
                try {
                    processor.register(selector);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

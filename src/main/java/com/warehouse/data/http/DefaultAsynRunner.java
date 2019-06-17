package com.warehouse.data.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-28 12:35
 **/
public class DefaultAsynRunner implements IAsynRunner{

    private final ExecutorService executorService;
    private final AtomicInteger       atc            = new AtomicInteger();
    private final List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<ClientHandler>());

    public DefaultAsynRunner(int threads){
         executorService = Executors.newFixedThreadPool(threads);
    }


    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    @Override
    public void closeAll() {
        clientHandlers.forEach(clientHandler ->{
                clientHandler.close();
        });
    }

    @Override
    public void close(ClientHandler clientHandler) {
        if(clientHandler != null){
            clientHandler.close();
        }
    }

    @Override
    public void exec(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
        executorService.execute(clientHandler);
    }


    private Thread createThread(ClientHandler clientHandler){
        Thread thread = new Thread(clientHandler);
        thread.setDaemon(true);
        thread.setName("HttpServerTD Request Processor #" + atc.getAndIncrement());
        return thread;
    }


    public static class DefaultThreadFactory implements ThreadFactory{

        @Override
        public Thread newThread(Runnable r) {
            return null;
        }
    }
}

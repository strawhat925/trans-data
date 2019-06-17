package com.warehouse.data.http;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-28 12:14
 **/
public interface IAsynRunner {

    void closeAll();

    void close(ClientHandler clientHandler);

    void exec(ClientHandler clientHandler);
}

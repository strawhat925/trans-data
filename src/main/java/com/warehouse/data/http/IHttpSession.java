package com.warehouse.data.http;

import java.io.IOException;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-03-29 09:09
 **/
public interface IHttpSession {

    void execute() throws IOException;


    void parseBody(Map<String, String> files) throws IOException, HttpServerTD.ResponseException;


    Map<String, String> getParams();
}

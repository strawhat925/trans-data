package com.warehouse.data.http;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-04-09 20:58
 **/
public interface ITempFileManager {


    void close();


    ITempFile createTempFile(String fileNameHint) throws Exception;

}

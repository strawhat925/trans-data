package com.warehouse.data.http;

import java.io.OutputStream;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-04-09 20:59
 **/
public interface ITempFile {

    void delete() throws Exception;

    String getName();

    OutputStream open() throws Exception;
}

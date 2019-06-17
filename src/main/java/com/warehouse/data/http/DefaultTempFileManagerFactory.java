package com.warehouse.data.http;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-04-09 21:12
 **/
public class DefaultTempFileManagerFactory implements IFactory<DefaultTempFileManager>{

    @Override
    public DefaultTempFileManager create() {
        return new DefaultTempFileManager();
    }
}

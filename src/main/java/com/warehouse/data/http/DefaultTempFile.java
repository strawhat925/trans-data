package com.warehouse.data.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-04-09 21:07
 **/
public class DefaultTempFile implements ITempFile {


    private final File file;

    private final OutputStream fstout;


    public DefaultTempFile(File tmpDir) throws IOException {
        this.file = File.createTempFile("HTTPD-", "", tmpDir);
        this.fstout = new FileOutputStream(this.file);
    }

    @Override
    public void delete() throws Exception {
        HttpServerTD.safeClose(fstout);
        if (!this.file.delete()) {

        }
    }

    @Override
    public String getName() {
        return this.file.getAbsolutePath();
    }

    @Override
    public OutputStream open() throws Exception {
        return this.fstout;
    }
}

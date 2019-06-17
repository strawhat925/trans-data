package com.warehouse.data.http;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-04-09 21:03
 **/
public class DefaultTempFileManager implements ITempFileManager {

    private final File tmpDir;

    private final List<ITempFile> tempFilesList;

    public DefaultTempFileManager() {
        this.tmpDir = new File(System.getProperty("java.io.tmpdir"));
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        this.tempFilesList = new ArrayList<>();
    }

    @Override
    public void close() {
        tempFilesList.forEach(action -> {
            try {
                action.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.tempFilesList.clear();
    }

    @Override
    public ITempFile createTempFile(String fileNameHint) throws Exception {
        DefaultTempFile tempFile = new DefaultTempFile(this.tmpDir);
        this.tempFilesList.add(tempFile);
        return tempFile;
    }
}

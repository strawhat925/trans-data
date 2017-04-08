package com.warehouse.data.netty.model;

import java.io.Serializable;

public class ResponseFile implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = -1425307876096494974L;


    public ResponseFile() {

    }


    public ResponseFile(long start, String fileMd5, String fileUrl) {
        super();
        this.start = start;
        this.fileMd5 = fileMd5;
        this.fileUrl = fileUrl;
        this.end = true;
        this.progress = 100;
    }

    public ResponseFile(long start, String fileMd5, long progress) {
        super();
        this.start = start;
        this.fileMd5 = fileMd5;
        this.end = false;
        this.progress = (int) progress;
    }


    /**
     * 开始 读取点
     */
    private long start;
    /**
     * 文件的 MD5值
     */
    private String fileMd5;
    /**
     * 文件下载地址
     */
    private String fileUrl;
    /**
     * 上传是否结束
     */
    private boolean end;
    /**
     * 进度
     */
    private int progress;

    public long getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


}

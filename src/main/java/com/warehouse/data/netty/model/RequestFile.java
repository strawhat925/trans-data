package com.warehouse.data.netty.model;

import java.io.File;
import java.io.Serializable;

public class RequestFile implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1425307876096494974L;

	private File file;
	private String fileName;// 文件名
	private long starPos;// 开始位置
	private byte[] bytes;// 文件字节数组
	private int endPos;// 结尾位置
	private String fileMd5; //文件的MD5值
	private String fileType;  //文件类型
	private long fileSize; //文件总长度


	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getStarPos() {
		return starPos;
	}

	public void setStarPos(long starPos) {
		this.starPos = starPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}

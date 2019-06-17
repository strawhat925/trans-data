package com.warehouse.data.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2019-05-15 14:20
 **/
public class MMapTest {

    static File file = new File("/Users/strawhat925/test.txt");

    public static void main(String[] args) {
        int[] sizes = {128, 256, 512, 4096, 8192, 1024 * 16, 1024 * 32, 1024 * 128, 1024 * 512};

        // 8M
        int fileSize = 8 * 1024 * 1024;
        try {
            for (int size : sizes) {
                //testChannel(size, fileSize);
                //testMMap(size, fileSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonString = "";
    }


    private static void testChannel(int size, int fileSize) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        RandomAccessFile rw = new RandomAccessFile(file, "rw");
        FileChannel channel = rw.getChannel();

        long start = System.currentTimeMillis();

        int writeSize = 0;
        while (writeSize < fileSize) {
            buffer.clear();
            buffer.put(new byte[size]);
            buffer.flip();
            channel.position(writeSize);
            channel.force(true);
            writeSize += size;
        }
        System.out.println("DirectBuffer + FileChannel write " + size + " bytes every time cost: " + (System.currentTimeMillis() - start) + "ms");

        if (file.exists()) {
            file.delete();
        }
    }

    private static void testMMap(int size, int fileSize) throws IOException {
        RandomAccessFile rw = new RandomAccessFile(file, "rw");
        FileChannel channel = rw.getChannel();

        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);

        long start = System.currentTimeMillis();

        int writeSize = 0;
        while (writeSize < fileSize) {
            mappedByteBuffer.put(new byte[size]);
            mappedByteBuffer.force();
            writeSize += size;
        }

        System.out.println("MappedByteBuffer write " + size + " bytes every time cost: " + (System.currentTimeMillis() - start) + "ms");

        if (file.exists()) {
            file.delete();
        }
    }

}

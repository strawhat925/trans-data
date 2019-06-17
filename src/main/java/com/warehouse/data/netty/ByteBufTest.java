package com.warehouse.data.netty;

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2018-10-26 09:34
 **/
public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);

        print("allocate ByteBuf(9,100)", buffer);

        //write 方法改变指针，写完之后写指针未到capacity的时候，buffer仍然可写
        buffer.writeBytes(new byte[]{1, 2, 3});
        print("writeBytes(1,2,3)", buffer);

        //write 方法改变指针，写完之后写指针未到capacity的时候，buffer仍然可以写，写完int类型之后指针增加4
        buffer.writeInt(12);
        print("writeInt(12)", buffer);

        //write 方法改变指针，写完之后写指针等于capacity的时候，buffer不可写
        buffer.writeBytes(new byte[]{4, 5});
        print("writeBytes(4,5)", buffer);

        //write 方法改变指针，写的时候发现buffer不可写则开始扩容，扩容之后capacity随即改变
        buffer.writeBytes(new byte[]{6});
        print("writeBytes(6)", buffer);


        //get 方法不改变读写指针
        System.out.printf("getByte(2) return:%s\n", buffer.getByte(2));
        System.out.printf("getShort(2) return:%s\n", buffer.getShort(2));
        System.out.printf("getInt(2) return:%s\n", buffer.getInt(2));
        print("getByte()", buffer);


        //set 方法不改变读写指针
        buffer.setByte(buffer.readableBytes() + 1, 0);
        print("setByte()", buffer);

        //read 方法改变读指针
        byte[] dst = new byte[buffer.readableBytes()];
        buffer.readBytes(dst);
        print("readBytes(" + dst.length + ")", buffer);
    }


    private static void print(String action, ByteBuf buffer) {
        System.out.println("after ===========" + action + "============");
        System.out.println("capacity(): " + buffer.capacity());
        System.out.println("maxCapacity(): " + buffer.maxCapacity());
        System.out.println("readerIndex(): " + buffer.readerIndex());
        System.out.println("readableBytes(): " + buffer.readableBytes());
        System.out.println("isReadable(): " + buffer.isReadable());
        System.out.println("writerIndex(): " + buffer.writerIndex());
        System.out.println("writableBytes(): " + buffer.writableBytes());
        System.out.println("isWritable(): " + buffer.isWritable());
        System.out.println("maxWritableBytes(): " + buffer.maxWritableBytes());
        System.out.println();
    }

}

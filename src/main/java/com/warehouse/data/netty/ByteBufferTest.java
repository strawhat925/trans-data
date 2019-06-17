package com.warehouse.data.netty;

import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.message.MessageClientIDSetter;

import java.nio.ByteBuffer;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2019-05-30 10:05
 **/
public class ByteBufferTest {

    public static void main(String[] args) {
        ByteBuffer tempBuffer = ByteBuffer.allocate(10);
        tempBuffer.position(2);
        System.out.println(UtilAll.getPid());
        tempBuffer.putInt(UtilAll.getPid());
        tempBuffer.position(0);
        try {
            System.out.println(new String(UtilAll.getIP()));
            tempBuffer.put(UtilAll.getIP());
        } catch (Exception e) {
            //tempBuffer.put(createFakeIP());
        }
        tempBuffer.position(6);
        tempBuffer.putInt(MessageClientIDSetter.class.getClassLoader().hashCode());
        String FIX_STRING = UtilAll.bytes2string(tempBuffer.array());
        System.out.println(FIX_STRING);
        //ByteBuffer.wrap(new byte[]{1, 3, 5});

        //创建一个指定capacity的ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        print("allocate 8", byteBuffer);

        //put 写完之后position未到capacity的时候，buffer仍然可写
        byteBuffer.put(new byte[]{1, 2, 3});
        print("put(1,2,3)", byteBuffer);


        //flip 从写模式切换到读模式，limit为buffer的有效长度
        byteBuffer.flip();
        print("flip()", byteBuffer);

        byteBuffer.get(0);
        print("get(index)", byteBuffer);
        byteBuffer.get(0);
        print("get(index)", byteBuffer);

        //get 两个字节
        byteBuffer.get(new byte[]{1, 2});
        print("get(1,2)", byteBuffer);

        //mark 为某一读过的位置position做标记，便于某些时候回退到该位置position
        byteBuffer.mark();
        print("mark", byteBuffer);

        //get 一个字节
        byteBuffer.get();
        print("get(3)", byteBuffer);

        //reset 将position属性值设为mark值
        byteBuffer.reset();
        print("reset", byteBuffer);

        //reset 后还有一个可读
        byteBuffer.get();
        print("get()", byteBuffer);


        //compact 将position与limit之间的数据复制到buffer的开始位置
        byteBuffer.compact();
        print("compact()", byteBuffer);

        byteBuffer.put(new byte[]{4, 5, 6});
        print("put(4,5,6)", byteBuffer);

        byteBuffer.clear();
        print("clear()", byteBuffer);

        byteBuffer.put((byte) 1);
        print("put(1)", byteBuffer);
        byteBuffer.flip();
        print("flip()", byteBuffer);



        //slice 部分复制，复制position到limit之间的内存快;数据不复制
        ByteBuffer bb = byteBuffer.slice();
        print("slice()", bb);
        bb.put((byte) 1);
        bb.flip();
        System.out.println("result:" + bb.get());


        //duplicate 完全复制，创建了一个与原始缓冲区相似的新缓冲区，两个缓冲区共享数据元素，拥有同样的容量，
        // 但每个缓冲区拥有各自的位置，上界和标记属性；对一个缓冲区内的数据元素所做的改变会反映在另外一个缓冲区上
        // 这一副本缓冲区具有与原始缓冲区同样的数据视图；如果原始的缓冲区为只读，或者为直接缓冲区，新的缓冲区将继承这些属性
        ByteBuffer db = byteBuffer.duplicate();
        print("duplicate", db);
        System.out.println("result:" + db.get());



        //创建一个direct的ByteBuffer
        ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(8);

    }


    private static void print(String action, ByteBuffer buffer) {
        System.out.println("after ===========" + action + "============");
        System.out.println("capacity(): " + buffer.capacity());
        System.out.println("limit(): " + buffer.limit());
        System.out.println("position(): " + buffer.position());
        //是否有可用空间
        System.out.println("hasRemaining(): " + buffer.hasRemaining());
        //查看还有多少可用的空间
        System.out.println("remaining:" + buffer.remaining());
    }
}

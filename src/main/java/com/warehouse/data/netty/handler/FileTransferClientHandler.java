package com.warehouse.data.netty.handler;

import com.warehouse.data.netty.model.RequestFile;
import com.warehouse.data.netty.model.ResponseFile;
import com.warehouse.data.netty.model.Secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;
import java.net.InetAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.netty.handler
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-08 14:44
 **/
public class FileTransferClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FileTransferClientHandler.class);
    private int byteRead;
    private volatile long start = 0;
    private RandomAccessFile randomAccessFile;
    private RequestFile requestFile;
    private final int minReadBufferSize = 8192;


    public FileTransferClientHandler(RequestFile requestFile) {
        if (requestFile.getFile().exists()) {
            if (!requestFile.getFile().isFile()) {
                logger.info("Not a file : " + requestFile.getFile());
                return;
            }
        }
        this.requestFile = requestFile;
    }


    /**
     * 客户端建立连接成功后调用，编写向服务端写数据逻辑
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
        Secure secure = new Secure();
        secure.setToken(InetAddress.getLocalHost().getHostAddress());
        ctx.writeAndFlush(secure);
    }


    /**
     * 负责读取服务端发来的数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Secure) {
            Secure secure = (Secure) msg;
            if(!secure.isAutoSuccess()){
                return;
            }
            randomAccessFile = new RandomAccessFile(requestFile.getFile(), "r");
            randomAccessFile.seek(requestFile.getStarPos());
            byte[] bytes = new byte[minReadBufferSize];
            if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                requestFile.setEndPos(byteRead);
                requestFile.setBytes(bytes);
                requestFile.setFileSize(randomAccessFile.length());
                ctx.writeAndFlush(requestFile);
            } else {
                //文件读完
                randomAccessFile.close();
            }
            return;
        }

        if (msg instanceof ResponseFile) {
            ResponseFile responseFile = (ResponseFile) msg;
            if (responseFile.isEnd()) {
                randomAccessFile.close();
            } else {
                start = responseFile.getStart();
                if (start != -1) {
                    randomAccessFile = new RandomAccessFile(requestFile.getFile(), "r");
                    randomAccessFile.seek(start);
                    int a = (int) (randomAccessFile.length() - start);
                    int sendLength = minReadBufferSize;
                    if (a < minReadBufferSize) {
                        sendLength = a;
                    }
                    byte[] bytes = new byte[sendLength];
                    if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                        requestFile.setEndPos(byteRead);
                        requestFile.setBytes(bytes);
                        ctx.writeAndFlush(requestFile);
                    } else {
                        randomAccessFile.close();
                        ctx.close();
                    }
                }
            }


        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(randomAccessFile != null){
            randomAccessFile.close();
        }
        ctx.close();
    }
}

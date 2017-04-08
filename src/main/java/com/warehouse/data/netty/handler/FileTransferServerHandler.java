package com.warehouse.data.netty.handler;

import com.warehouse.data.netty.model.RequestFile;
import com.warehouse.data.netty.model.ResponseFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.netty.handler
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-08 14:24
 **/
public class FileTransferServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FileTransferServerHandler.class);

    private volatile int byteRead;
    private volatile int start = 0;

    private RandomAccessFile randomAccessFile;
    private File file;
    private long fileSize = -1;

    private static String fileDir = "D:\\用户目录\\我的文档\\WeChat Files\\strawhat925\\Image";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RequestFile) {
            RequestFile requestFile = (RequestFile) msg;
            byte[] bytes = requestFile.getBytes();
            byteRead = requestFile.getEndPos();

            String md5 = requestFile.getFileMd5();
            if (start == 0) {
                String path = fileDir + File.separator + md5 + requestFile.getFileType();
                file = new File(path);
                fileSize = requestFile.getFileSize();

                //根据 MD5 和 文件类型 来确定是否存在这样的文件 如果存在就 秒传
                if (file.exists()) {
                    logger.info("file exists:{},{} > [{}]", requestFile.getFileName(), requestFile.getFileMd5(), ctx.channel().remoteAddress());
                    ResponseFile responseFile = new ResponseFile(start, md5, getFilePath());
                    ctx.writeAndFlush(responseFile);

                    //TODO 这里可以做 断点续传 ，读取当前已经存在文件的总长度  和 传输过来的文件总长度对比 如果不一致，则认为本地文件没有传完毕 则续传
                    // 不过这步骤必须做好安全之后来做，否则可能会出现 文件被恶意加入内容
                    return;
                }

                randomAccessFile = new RandomAccessFile(file, "rw");
            }

            randomAccessFile.seek(start);
            randomAccessFile.write(bytes);
            start = start + byteRead;

            if (byteRead > 0 && (start < fileSize && fileSize != 1)) {
                ResponseFile responseFile = new ResponseFile(start, md5, (start * 100) / fileSize);
                ctx.writeAndFlush(responseFile);
            } else {
                logger.info("file create success:{},{} > [{}]", requestFile.getFileName(), requestFile.getFileMd5(), ctx.channel().remoteAddress());
                ResponseFile responseFile = new ResponseFile(start, md5, getFilePath());
                ctx.writeAndFlush(responseFile);

                randomAccessFile.close();
                file = null;
                fileSize = -1;
                randomAccessFile = null;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
        ctx.close();
    }

    private String getFilePath() {
        return null;
    }
}

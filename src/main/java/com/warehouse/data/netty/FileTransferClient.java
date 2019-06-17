package com.warehouse.data.netty;

import com.warehouse.data.netty.codec.NettyMessageDecoder;
import com.warehouse.data.netty.codec.NettyMessageEncoder;
import com.warehouse.data.netty.handler.FileTransferClientHandler;
import com.warehouse.data.netty.model.RequestFile;
import com.warehouse.data.util.MD5FileUtil;

import java.io.File;
import java.io.IOException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.netty
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-08 14:39
 **/
public class FileTransferClient {


    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("must give two args: host | port");
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        String filePath = "D:\\用户目录\\我的文档\\WeChat Files\\strawhat925\\Image\\Image\\735859399-weichat-master.zip";
        RequestFile requestFile = new RequestFile();
        File file = new File(filePath);
        requestFile.setFile(file);
        requestFile.setFileName(file.getName());
        requestFile.setFileType(getSuffix(filePath));
        requestFile.setStarPos(0);
        requestFile.setFileMd5(MD5FileUtil.getFileMD5String(file));

        FileTransferClient client = new FileTransferClient();
        client.connect(host, port, requestFile);
    }


    private static String getSuffix(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        return fileType;
    }

    public void connect(String host, int port, final RequestFile requestFile) {
        EventLoopGroup boss = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        //bootstrap.attr()
        try {
            //1、指定线程模型
            bootstrap.group(boss)
                    //2、NIO
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    //3、IO处理逻辑
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new ObjectEncoder())
                                    .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingResolver(null)))
                                    .addLast(new NettyMessageEncoder())
                                    .addLast(new NettyMessageDecoder())
                                    .addLast(new FileTransferClientHandler(requestFile));

                            //socketChannel.attr()
                        }
                    });
            //4、建立连接
            ChannelFuture future = bootstrap.connect(host, port).addListener(future1 -> {
                if (future1.isSuccess()) {
                    System.out.println("连接成功");
                } else {
                    System.out.println("连接失败");
                }
            }).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
        }

    }
}

package com.warehouse.data.netty;

import com.warehouse.data.netty.codec.NettyMessageDecoder;
import com.warehouse.data.netty.codec.NettyMessageEncoder;
import com.warehouse.data.netty.handler.FileTransferServerHandler;
import com.warehouse.data.netty.handler.SecureServerHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.netty
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-08 13:53
 **/
public class FileTransferServer {
    private static final Logger logger = LoggerFactory.getLogger(FileTransferServer.class);


    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("must give one args: port");
        }
        int port = Integer.parseInt(args[0]);
        FileTransferServer server = new FileTransferServer();
        server.start(port);
    }

    public void start(int port) {
        EventLoopGroup boos = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boos, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new ObjectEncoder())
                                    //最大长度
                                    .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingResolver(null)))

                                    .addLast(new NettyMessageEncoder())
                                    .addLast(new NettyMessageDecoder())
                                    .addLast(new SecureServerHandler())
                                    .addLast(new FileTransferServerHandler());

                        }
                    });
            logger.info("start netty server port:{}", port);
            ChannelFuture future = null;
            future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


}

package com.warehouse.data.netty;

import com.warehouse.data.netty.codec.NettyMessageDecoder;
import com.warehouse.data.netty.codec.NettyMessageEncoder;
import com.warehouse.data.netty.handler.FileTransferServerHandler;
import com.warehouse.data.netty.handler.SecureServerHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

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
        /**
         * 用生活中的例子来讲就是，一个工厂要运作，必然要有一个老板负责从外面接活，然后有很多员工，负责具体干活，老板就是bossGroup，员工们就是workerGroup，bossGroup接收完连接，扔给workerGroup去处理。
         */


        //表示监听端口，accept新连接的线程组
        EventLoopGroup boos = new NioEventLoopGroup();
        //表示处理每一条连接的数据读写线程组
        EventLoopGroup worker = new NioEventLoopGroup();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boos, worker)
                    //NIO NioServerSocketChannel.class
                    //BIO OioServerSocketChannel.class
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //用于指定在服务端启动过程中的一些逻辑，通常情况下用不着
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("服务端启动中...");
                        }
                    })
                    //用于指定处理新连接数据的读写处理逻辑
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
            future = bootstrap.bind(port).addListener(future1 -> {
                if (future1.isSuccess()) {
                    System.out.println("端口绑定成功");
                } else {
                    System.out.println("端口绑定失败");
                }
            }).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


}

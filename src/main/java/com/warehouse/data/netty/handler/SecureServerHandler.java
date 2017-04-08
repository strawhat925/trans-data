package com.warehouse.data.netty.handler;


import com.alibaba.druid.util.StringUtils;
import com.warehouse.data.netty.model.Secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.netty.handler
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-08 14:13
 **/
public class SecureServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SecureServerHandler.class);
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Secure) {
            Secure secure = (Secure) msg;
            if (secure.getToken() != null) {
                //TODO  验证 token 是否存在，并且token对应的 ip和 ctx里面来源ip是否一致
                InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                if (StringUtils.equals(secure.getToken(), inetSocketAddress.getAddress().getHostAddress())) {
                    logger.info("NEW TCP > " + ctx.channel().remoteAddress());
                    logger.info("now connection count > " + channels.size());
                    channels.add(ctx.channel());
                    secure.setAutoSuccess(true);
                    ctx.writeAndFlush(secure);
                    return;
                }
            }
            secure.setAutoSuccess(false);
            ctx.writeAndFlush(secure);
        } else {
            if (!channels.contains(ctx.channel())) {
                Secure secure = new Secure();
                secure.setAutoSuccess(false);
                ctx.writeAndFlush(secure);
                ctx.close();
            } else {
                //继续执行
                ctx.fireChannelRead(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

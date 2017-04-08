package com.warehouse.data.netty.codec;

import com.warehouse.data.util.ObjectConvertUtil;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.netty.codec
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-08 14:06
 **/
public class NettyMessageEncoder extends MessageToMessageEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
        list.add(ObjectConvertUtil.request(o));
    }
}

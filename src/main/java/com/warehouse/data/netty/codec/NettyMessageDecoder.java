package com.warehouse.data.netty.codec;

import com.warehouse.data.util.ObjectConvertUtil;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * ${DESCRIPTION}
 * package com.warehouse.data.netty.codec
 *
 * @author zli [liz@yyft.com]
 * @version v1.0
 * @create 2017-04-08 14:09
 **/
public class NettyMessageDecoder extends MessageToMessageDecoder<String> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
        Object value = ObjectConvertUtil.convertModle(s);
        list.add(value);
    }
}

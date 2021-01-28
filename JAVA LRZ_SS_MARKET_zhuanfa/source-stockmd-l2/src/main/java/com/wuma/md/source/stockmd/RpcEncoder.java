package com.wuma.md.source.stockmd;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class RpcEncoder extends MessageToByteEncoder {


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            out.writeBytes(((String)msg).getBytes(CharsetUtil.US_ASCII)); //消息体中包含我们要发送的数据
    }

}

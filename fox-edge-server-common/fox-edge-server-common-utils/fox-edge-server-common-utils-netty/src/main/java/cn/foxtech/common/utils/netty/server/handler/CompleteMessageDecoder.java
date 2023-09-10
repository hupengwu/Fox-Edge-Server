package cn.foxtech.common.utils.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 消息解码器
 */
public class CompleteMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 拆箱
        ByteBuf result = in;
        byte[] bytes = new byte[result.readableBytes()];
        result.readBytes(bytes);

        // 装箱
        out.add(bytes);
    }
}

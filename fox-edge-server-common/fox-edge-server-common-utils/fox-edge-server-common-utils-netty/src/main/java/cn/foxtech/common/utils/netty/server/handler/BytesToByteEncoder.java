package cn.foxtech.common.utils.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * 发送报文编码器：将上一环节生成的待发送byte[]报文，写入channel的out缓存中（发送数据）
 */
public class BytesToByteEncoder extends MessageToByteEncoder<byte[]> {
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
        out.writeBytes(msg);
    }
}

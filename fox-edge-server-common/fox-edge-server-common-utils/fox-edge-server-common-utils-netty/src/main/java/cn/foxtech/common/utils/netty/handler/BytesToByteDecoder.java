/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 接收文解码器：将上一环节生成的ByteBuf，转换成byte[]给下一环节处理。
 */
public class BytesToByteDecoder extends ByteToMessageDecoder {
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

package cn.foxtech.common.utils.iec104.server.handler;

import cn.foxtech.device.protocol.v1.iec104.core.encoder.ApduEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.ValueEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消息解码器
 */
public class MessageDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 拆箱
        ByteBuf result = in;
        byte[] bytes = new byte[result.readableBytes()];
        result.readBytes(bytes);

        LOGGER.info("接收报文:" + ValueEncoder.byteArrayToHexString(bytes));

        // 尝试解码，此时为U帧格式
        ApduEntity apduEntity = ApduEncoder.decodeApdu(bytes);

        // 将APDU装箱
        out.add(apduEntity);
    }
}

package cn.foxtech.common.utils.iec104.server.handler;

import cn.foxtech.device.protocol.v1.iec104.core.encoder.ValueEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 将上一环节生成的待发送byte[]报文，写入channel的out缓存中（发送数据）
 */
public class BytesToByteEncoder extends MessageToByteEncoder<byte[]> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BytesToByteEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
		LOGGER.info("发送报文:" + ValueEncoder.byteArrayToHexString(msg));
		out.writeBytes(msg);
	}
}
